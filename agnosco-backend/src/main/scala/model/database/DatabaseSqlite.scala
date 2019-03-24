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
			println("Table " + tableName + " created successfully.")
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

			name = "getAllProjects"
			statement = conn.prepareStatement("SELECT * FROM projects")
			map.put(name, statement)



			map
		}

		true
	}

	override def disconnect: Boolean = {
		try {
			conn.close()
			conn = null
			true
		}catch {
			case e: SQLiteException =>
				System.err.println(e.getStackTrace)
				false
		}
	}



	override def getProject(id: Long): Option[Project] = {/*
		val sql = s"SELECT * FROM projects WHERE id = $id"
		val res = getStatement(sql)

		if (!res.next) return None

		val name = res.getString("name")
		try {
			val recogniser = RecogniserType.withName(res.getString("recogniser"))
			val documents = getDocumentsOfProject(id)
			Some(Project(id, name, recogniser, documents))
		} catch {
			case _ : Exception => None
		}*/
		None
	}

	override def addProject(project: Project): Unit = {
		val pstmt = statements("addProjects")
		pstmt.setString(1, project.name)
		pstmt.setString(2, project.recogniser.toString)
		pstmt.executeUpdate()
	}

	override def deleteProject(id: Long): Unit = {
		val stmt = statements("deleteProject")
		stmt.setLong(1, id)
		stmt.executeUpdate()
	}

	override def getDocument(id: Long): Option[Document] = {/*
		val sql = s"SELECT * FROM pages WHERE id = $id"
		val res = getStatement(sql)

		if (!res.next) return None

		val name = res.getString("name")
		val pages = getPagesOfDocument(id)
		val prepared = res.getBoolean("prepared")

		Some(Document(id, name, pages, prepared))*/
		None
	}

	override def addDocument(document: Document, projectID: Long): Unit = {/*
		val sql = s"INSERT INTO documents (name, prepared, projectId) VALUES (${document.name}, 0, $projectID)"
		pushStatement(sql)*/
	}

	override def deleteDocument(id: Long): Unit = {/*
		val sql = s"DELETE FROM documents WHERE id=$id"
		pushStatement(sql)*/
	}

	override def getPage(id: Long): Option[Page] = {/*
		val sql = s"SELECT * FROM pages WHERE id = $id"
		val res = getStatement(sql)

		if (!res.next) return None

		val imagePath = res.getString("imagePath")
		val groundTruthPath = res.getString("groundTruthPath")
		val examples = getExamplesOfPage(id)

		Some(Page(id, imagePath, groundTruthPath, examples))*/
		None
	}

	override def addPage(page: Page, documentId: Long): Unit = {/*
		val sql = s"INSERT INTO pages (imagePath, groundTruthPath, documentId) VALUES (${page.imagePath}, ${page.groundTruthPath}, $documentId)"
		pushStatement(sql)*/
	}

	override def deletePage(id: Long): Unit = {/*
		val sql = s"DELETE FROM pages WHERE id=$id"
		pushStatement(sql)*/
	}

	override def getExample(id: Long): Option[Example] = {/*
		val sql = s"SELECT * FROM examples WHERE id = $id"
		val res = getStatement(sql)

		if (!res.next) None

		val imagePath = res.getString("imagePath")
		val transcript =
			res.getString("transcript") match {
				case "null" => None
				case t => Some(t)
			}
		val enabled = res.getBoolean("enabled")
		val validated = res.getBoolean("validated")

		Some(Example(id, imagePath, transcript, enabled, validated))*/
		None
	}

	override def addExample(example: Example, pageId: Long): Unit = {/*
		val sql = s"INSERT INTO examples (imagePath, transcript, pageId, enabled, validated) VALUES (${example.imagePath}, ${example.transcript}, $pageId, ${example.enabled}, ${example.validated})"
		pushStatement(sql)*/
	}

	override def deleteExample(id: Long): Unit = {/*
		val sql = s"DELETE FROM examples WHERE id=$id"
		pushStatement(sql)*/
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

	override def getDocumentsOfProject(id: Long): Iterable[Document] = {/*
		val sql = s"SELECT * FROM documents WHERE idProject = $id"
		val res = getStatement(sql)
		val documents = new ArrayBuffer[Document]()

		while (res.next) {
			val name = res.getString("name")
			val pages = getPagesOfDocument(id)
			val prepared = res.getBoolean("prepared")
			documents += Document(id, name, pages, prepared)
		}

		documents*/
		List()
	}

	override def getPagesOfDocument(id: Long): Iterable[Page] = {/*
		val sql = s"SELECT * FROM pages WHERE idDocument = $id"
		val res = getStatement(sql)
		val pages = new ArrayBuffer[Page]()

		while (res.next) {
			val imagePath = res.getString("imagePath")
			val groundTruthPath = res.getString("groundTruthPath")
			val examples = getExamplesOfPage(id)

			pages += Page(id, imagePath, groundTruthPath, examples)
		}

		pages*/
		List()
	}

	override def getExamplesOfPage(id: Long): Iterable[Example] = {/*
		val sql = s"SELECT * FROM examples WHERE pageId = $id"
		val res = getStatement(sql)
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

		examples*/
		List()
	}

	override def saveExampleEdition(examples: Iterable[Example]): Unit = {/*
		examples.foreach(example => {
			val sql = s"UPDATE examples SET imagePath=${example.imagePath} transcript=${example.transcript}  WHERE id=${example.id}"
			pushStatement(sql)
		})*/
	}

	override def documentArePrepared(documents: Iterable[Document]): Unit = {/*
		documents.foreach(doc => {
			val sql = s"UPDATE documents SET prepared=1 WHERE id=${doc.id}"
			pushStatement(sql)
		})*/
	}
}
