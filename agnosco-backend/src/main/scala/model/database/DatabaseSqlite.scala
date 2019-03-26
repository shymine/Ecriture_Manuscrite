package model.database
import java.io.IOException
import java.nio.file.{Files, Paths}
import java.sql.{Connection, DriverManager, PreparedStatement, ResultSet}

import model.common._
import org.sqlite.SQLiteException

import scala.collection.mutable
import scala.collection.mutable.{ArrayBuffer, ArrayBuilder}

/*
object DatabaseSqlite {
	private var idCount: Long = 0
	def incrementID: Long = {
		idCount += 1
		idCount
	}
	def setIDCounter(id: Long) = idCount = id
}
*/
// Using AUTOINCREMENT funtionality so if it works, no need for this
class DatabaseSqlite extends Database {

	val DATABASE_NAME = "agnosco"
	val THUMBNAILS_FOLDER = "thumbnails"
	private var statements : mutable.Map[String, PreparedStatement] = _

	private var conn: Connection = _


	private def createFolder(folderPath : String) : Unit = {
		val path = Paths.get(folderPath)
		try
			Files.createDirectories(path)
		catch {
			case e: IOException => e.printStackTrace()
		}
	}

	private def createTable(tableName : String, query : String) : Unit = {
		try {
			val sql = "CREATE TABLE IF NOT EXISTS " + tableName + "(" + query + ")"
			val pstmt = conn.prepareStatement(sql)
			pstmt.executeUpdate()
			conn.commit()
			//println("Table " + tableName + " created successfully.")
		} catch {
			case e: SQLiteException =>
				System.err.println("Table " + tableName + " has already been created.")
				e.printStackTrace()
			case e: Exception =>
				System.err.println(e.getClass.getName + ": " + e.getMessage)
		}
	}

	override def connect: Boolean = {
		Class.forName("org.sqlite.JDBC")
		conn = DriverManager.getConnection("jdbc:sqlite:" + DATABASE_NAME + ".db")
		conn.setAutoCommit(false)

		// Thumbnails folder creation (if it does not exist)
		createFolder(THUMBNAILS_FOLDER)

		// Tables creation (if they do not exist)
		createTable(
			"projects",
			"id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				"name VARCHAR(64), " +
				"recogniser VARCHAR(64)")

		createTable(
			"documents",
			"id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				"name VARCHAR(64), " +
				"prepared BOOL, " +
				"projectId INTEGER NOT NULL, " +
				"FOREIGN KEY(projectId) REFERENCES projects(id)")

		createTable(
			"pages",
			"id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				"imagePath VARCHAR(256), " +
				"groundTruthPath VARCHAR(256), " +
				"documentId INTEGER NOT NULL, " +
				"FOREIGN KEY(documentId) REFERENCES documents(id)")

		createTable(
			"examples",
			"id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				"imagePath VARCHAR(256), " +
				"transcript VARCHAR(256), " +
				"enabled BOOL, " +
				"validated BOOL, " +
				"pageId INTEGER NOT NULL, " +
				"FOREIGN KEY(pageId) REFERENCES pages(id)")

		statements = {
			val map = mutable.Map[String, PreparedStatement]()

			var name = "getProject"
			var statement = conn.prepareStatement("SELECT * FROM projects WHERE id=?")
			map.put(name, statement)


			name = "addProject"
			statement = conn.prepareStatement("INSERT INTO projects (name, recogniser) VALUES (?,?)")
			map.put(name, statement)

			name = "deleteProject"
			statement = conn.prepareStatement("DELETE FROM projects WHERE id=?")
			map.put(name, statement)

			name = "getDocument"
			statement = conn.prepareStatement("SELECT * FROM documents WHERE id=?")
			map.put(name, statement)

			name = "addDocument"
			statement = conn.prepareStatement("INSERT INTO documents (name, prepared, projectId) VALUES (?,?,?)")
			map.put(name, statement)

			name = "deleteDocument"
			statement = conn.prepareStatement("DELETE FROM documents WHERE id=?")
			map.put(name, statement)

			name = "getPage"
			statement = conn.prepareStatement("SELECT * FROM pages WHERE id=?")
			map.put(name, statement)

			name = "addPage"
			statement = conn.prepareStatement("INSERT INTO pages (imagePath, groundTruthPath, documentId) VALUES (?,?,?)")
			map.put(name, statement)

			name = "deletePage"
			statement = conn.prepareStatement("DELETE FROM pages WHERE id=?")
			map.put(name, statement)

			name = "getExample"
			statement = conn.prepareStatement("SELECT * FROM examples WHERE id=?")
			map.put(name, statement)

			name = "addExample"
			statement = conn.prepareStatement("INSERT INTO examples (imagePath, transcript, pageId, enabled, validated) VALUES (?,?,?,?,?)")
			map.put(name, statement)

			name = "deleteExample"
			statement = conn.prepareStatement("DELETE FROM examples WHERE id=?")
			map.put(name, statement)

			name = "getAllProjects"
			statement = conn.prepareStatement("SELECT * FROM projects")
			map.put(name, statement)

			name = "getDocumentsOfProject"
			statement = conn.prepareStatement("SELECT * FROM documents WHERE projectId=?")
			map.put(name, statement)

			name = "getPagesOfDocument"
			statement = conn.prepareStatement("SELECT * FROM pages WHERE documentId=?")
			map.put(name, statement)

			name = "getExamplesOfPage"
			statement = conn.prepareStatement("SELECT * FROM examples WHERE pageId=?")
			map.put(name, statement)

			name = "saveExampleEdition"
			statement = conn.prepareStatement("UPDATE examples SET imagePath=?, transcript=?, enabled=?, validated=? WHERE id=?")
			map.put(name, statement)

			name = "documentPrepared"
			statement = conn.prepareStatement("UPDATE documents SET prepared=1 WHERE id=?")
			map.put(name, statement)

			map
		}

		true
	}

	override def disconnect: Boolean = {
		try {
			conn.commit() //TODO uncomment this line for production
			statements.keys.foreach(key => statements(key).close())

			conn.close()
			conn = null
			true
		}catch {
			case e: SQLiteException =>
				println("Problem disconnection")
				e.printStackTrace(System.err)
				false
		}
	}



	override def getProject(id: Long): Option[Project] = {
		val pstmt = statements("getProject")
		pstmt.setLong(1, id)
		val result = pstmt.executeQuery()

		if(!result.next()) return None

		Some(Project(result.getLong("id"), result.getString("name"), RecogniserType.withName(result.getString("recogniser")), List()))
	}

	override def addProject(project: Project): Project = {
		val pstmt = statements("addProject")
		pstmt.setString(1, project.name)
		pstmt.setString(2, project.recogniser.toString)
		pstmt.executeUpdate()
		val res = getAllProject.find(p => p.name == project.name)
		res.get
	}

	override def deleteProject(id: Long): Unit = {
		val stmt = statements("deleteProject")
		stmt.setLong(1, id)
		stmt.executeUpdate()
	}

	override def getDocument(id: Long): Option[Document] = {
		val stmt = statements("getDocument")
		stmt.setLong(1, id)
		val result = stmt.executeQuery()

		if (!result.next) return None

		val name = result.getString("name")
		val prepared = result.getBoolean("prepared")

		Some(Document(id, name, List(), prepared))
	}

	override def addDocument(document: Document, projectID: Long): Document = {
		val stmt = statements("addDocument")
		stmt.setString(1, document.name)
		stmt.setBoolean(2, document.prepared)
		stmt.setLong(3, projectID)
		stmt.executeUpdate()
		val res = getDocumentsOfProject(projectID).find(d => d.name == document.name).get
		res
	}

	override def deleteDocument(id: Long): Unit = {
		val stmt = statements("deleteDocument")
		stmt.setLong(1, id)
		stmt.executeUpdate()
	}

	override def getPage(id: Long): Option[Page] = {
		val stmt = statements("getPage")
		stmt.setLong(1, id)
		val res = stmt.executeQuery()

		if (!res.next) return None

		val imagePath = res.getString("imagePath")
		val groundTruthPath = res.getString("groundTruthPath")

		Some(Page(id, imagePath, groundTruthPath, List()))
	}

	override def addPage(page: Page, documentId: Long): Page = {
		val stmt = statements("addPage")
		stmt.setString(1, page.imagePath)
		stmt.setString(2, page.groundTruthPath)
		stmt.setLong(3, documentId)
		stmt.executeUpdate()
		val res = getPagesOfDocument(documentId).find(p => p.imagePath == page.imagePath).get
		res
	}

	override def deletePage(id: Long): Unit = {
		val stmt = statements("deletePage")
		stmt.setLong(1, id)
		stmt.executeUpdate()
	}

	override def getExample(id: Long): Option[Example] = {
		val stmt = statements("getExample")
		stmt.setLong(1, id)
		val res = stmt.executeQuery()

		if (!res.next) return None

		val imagePath = res.getString("imagePath")
		val transcript =
			res.getString("transcript") match {
				case "null" => None
				case t => Some(t)
			}
		val enabled = res.getBoolean("enabled")
		val validated = res.getBoolean("validated")

		Some(Example(id, imagePath, transcript, enabled, validated))
	}

	override def addExample(example: Example, pageId: Long): Example = {
		val stmt = statements("addExample")
		stmt.setString(1, example.imagePath)
		stmt.setString(2, example.transcript.orNull)
		stmt.setLong(3, pageId)
		stmt.setBoolean(4, example.enabled)
		stmt.setBoolean(5, example.validated)
		stmt.executeUpdate()
		val res = getExamplesOfPage(pageId).find(e => e.imagePath == example.imagePath).get
		res
	}

	override def deleteExample(id: Long): Unit = {
		val stmt = statements("deleteExample")
		stmt.setLong(1, id)
		stmt.executeUpdate()
	}

	override def getAllProject: Iterable[Project] = {
		val stmt = statements("getAllProjects")
		val res = stmt.executeQuery()
		val projects = new ArrayBuffer[Project]()
		while(res.next) {
			val name = res.getString("name")
			val recogniser = RecogniserType.withName(res.getString("recogniser"))
			val id = res.getLong("id")
			projects += Project(id, name, recogniser, List())
		}
		projects
	}

	override def getDocumentsOfProject(id: Long): Iterable[Document] = {
		val stmt = statements("getDocumentsOfProject")
		stmt.setLong(1, id)
		val res = stmt.executeQuery()
		val documents = new ArrayBuffer[Document]()

		while (res.next) {
			val name = res.getString("name")
			val prepared = res.getBoolean("prepared")
			documents += Document(id, name, List(), prepared)
		}

		documents
	}

	override def getPagesOfDocument(id: Long): Iterable[Page] = {
		val stmt = statements("getPagesOfDocument")
		stmt.setLong(1, id)
		val res = stmt.executeQuery()
		val pages = new ArrayBuffer[Page]()

		while (res.next) {
			val imagePath = res.getString("imagePath")
			val groundTruthPath = res.getString("groundTruthPath")
			pages += Page(id, imagePath, groundTruthPath, List())
		}

		pages
	}

	override def getExamplesOfPage(id: Long): Iterable[Example] = {
		val stmt = statements("getExamplesOfPage")
		stmt.setLong(1, id)
		val res = stmt.executeQuery()
		val examples = new ArrayBuffer[Example]()

		while (res.next) {
			val imagePath = res.getString("imagePath")
			val transcript =
				res.getString("transcript") match {
					case "null" => None
					case t => Some(t)
				}
			val enabled = res.getBoolean("enabled")
			val validated = res.getBoolean("validated")
			examples += Example(id, imagePath, transcript, enabled, validated)
		}

		examples
	}

	override def saveExampleEdition(examples: Iterable[Example]): Unit = {
		examples.foreach(example => {
			val stmt = statements("saveExampleEdition")
			stmt.setString(1, example.imagePath)
			stmt.setString(2, example.transcript.orNull)
			stmt.setBoolean(3, example.enabled)
			stmt.setBoolean(4, example.validated)
			stmt.setLong(5, example.id)
			stmt.executeUpdate()
		})
	}

	override def documentArePrepared(documentsId: Iterable[Long]): Unit = {
		documentsId.foreach(docId => {
			val stmt = statements("documentPrepared")
			stmt.setLong(1, docId)
			stmt.executeUpdate()
		})
	}
}
