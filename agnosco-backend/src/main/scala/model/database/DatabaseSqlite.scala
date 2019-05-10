package model.database
import java.io.{File, FileNotFoundException, IOException, PrintWriter}
import java.nio.file.{Files, Paths}
import java.sql.{Connection, DriverManager, PreparedStatement, ResultSet}

import model.common._
import org.sqlite.SQLiteException

import scala.collection.mutable
import scala.collection.mutable.{ArrayBuffer, ArrayBuilder}
import scala.io.Source

/**
  * SQlite implementation of the Trait Database
  */
class DatabaseSqlite extends Database {
	/** The name of the database file */
	val DATABASE_NAME = "agnosco"
	/** The name of the thumbnail file */
	val THUMBNAILS_FOLDER = "thumbnails"
	/** The differents statements that are called */
	private lazy val statements : mutable.Map[String, PreparedStatement] = {
		val map = mutable.Map[String, PreparedStatement]()

		var name = "getProject"
		var statement = conn.prepareStatement("SELECT * FROM projects WHERE id=?")
		map.put(name, statement)

		name = "addProject"
		statement = conn.prepareStatement("INSERT INTO projects ( name, recogniser) VALUES (?,?)")
		map.put(name, statement)

		name = "deleteProject"
		statement = conn.prepareStatement("DELETE FROM projects WHERE id=?")
		map.put(name, statement)

		name = "getDocument"
		statement = conn.prepareStatement("SELECT * FROM documents WHERE idD=?")
		map.put(name, statement)

		name = "addDocument"
		statement = conn.prepareStatement("INSERT INTO documents ( name, prepared, projectId) VALUES (?,?,?)")
		map.put(name, statement)

		name = "deleteDocument"
		statement = conn.prepareStatement("DELETE FROM documents WHERE idD=?")
		map.put(name, statement)

		name = "getPage"
		statement = conn.prepareStatement("SELECT * FROM pages WHERE idP=?")
		map.put(name, statement)

		name = "addPage"
		statement = conn.prepareStatement("INSERT INTO pages (groundTruth, documentId) VALUES (?,?)")
		map.put(name, statement)

		name = "deletePage"
		statement = conn.prepareStatement("DELETE FROM pages WHERE idP=?")
		map.put(name, statement)

		name = "getExample"
		statement = conn.prepareStatement("SELECT * FROM examples WHERE idE=?")
		map.put(name, statement)

		name = "addExample"
		statement = conn.prepareStatement("INSERT INTO examples ( imagePath, transcript, pageId, enabled, validated) VALUES (?,?,?,?,?)")
		map.put(name, statement)

		name = "deleteExample"
		statement = conn.prepareStatement("DELETE FROM examples WHERE idE=?")
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
		statement = conn.prepareStatement("UPDATE examples SET imagePath=?, transcript=?, enabled=?, validated=? WHERE idE=?")
		map.put(name, statement)

		name = "documentPrepared"
		statement = conn.prepareStatement("UPDATE documents SET prepared=1 WHERE idD=?")
		map.put(name, statement)

		map
	}
	/** The connection to the database */
	private lazy val conn: Connection = DriverManager.getConnection("jdbc:sqlite:" + DATABASE_NAME + ".db")
	// Creates the folder with the given path
	private def createFolder(folderPath : String) : Unit = {
		val path = Paths.get(folderPath)
		try
			Files.createDirectories(path)
		catch {
			case e: IOException => e.printStackTrace()
		}
	}
	// Creates the database tables if not created
	private def createTable(tableName : String, query : String) : Unit = {
		try {
			val sql = "CREATE TABLE IF NOT EXISTS " + tableName + "(" + query + ")"
			val pstmt = conn.prepareStatement(sql)
			pstmt.executeUpdate()
			conn.commit()
		} catch {
			case e: SQLiteException =>
				e.printStackTrace()
			case e: Exception =>
				System.err.println(e.getClass.getName + ": " + e.getMessage)
		}
	}

	/**
	  * {@inheritDoc}
	  * @return true if succed to connect
	  */
	override def connect: Boolean = {
		Class.forName("org.sqlite.JDBC")
		conn.setAutoCommit(false)

		// Thumbnails folder creation (if it does not exist)
		createFolder(THUMBNAILS_FOLDER)

		// Tables creation (if they do not exist)
		createTable(
			"projects",
			"id INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, " +
			"name VARCHAR(64), " +
				"recogniser VARCHAR(64)")
		createTable(
			"documents",
			"idD INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, " +
				"name VARCHAR(64), " +
				"prepared BOOL, " +
				"projectId INTEGER NOT NULL, " +
				"FOREIGN KEY(projectId) REFERENCES projects(id)")
		createTable(
			"pages",
			"idP INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, " +
				// "imagePath VARCHAR(500000), " +
				"groundTruth VARCHAR(256), " +
				"documentId INTEGER NOT NULL, " +
				"FOREIGN KEY(documentId) REFERENCES documents(idD)")
		createTable(
			"examples",
			"idE INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, " +
				"imagePath VARCHAR(256), " +
				"transcript VARCHAR(256), " +
				"enabled BOOL, " +
				"validated BOOL, " +
				"pageId INTEGER NOT NULL, " +
				"FOREIGN KEY(pageId) REFERENCES pages(idP)")

		true
	}
	/**
	 * {@inheritDoc}
	 * @return true if succed to disconnect
	 */
	override def disconnect: Boolean = {
		try {
			conn.commit() //TODO uncomment this line for production
			statements.keys.foreach(key => {
				statements(key).clearParameters()
			})
			true
		}catch {
			case e: SQLiteException =>
				println("Problem disconnection")
				e.printStackTrace(System.err)
				false
		}
	}

	/**
	 * {@inheritDoc}
	 */
	override def getProject(id: Long): Option[Project] = {
		val stmt = statements("getProject")
		stmt.setLong(1, id)
		val result = stmt.executeQuery()
		stmt.clearParameters()
		if(!result.next()) return None
		Some(Project(result.getLong("id"), result.getString("name"), RecogniserType.withName(result.getString("recogniser")), List()))
	}


	/**
	 * {@inheritDoc}
	 */
	override def addProject(project: Project): Project = {
		val stmt = statements("addProject")
		stmt.setString(1, project.name)
		stmt.setString(2, project.recogniser.toString)
		stmt.executeUpdate()
		stmt.clearParameters()
		val res = getAllProject.find(p => p.name == project.name)
		res.get
	}

	/**
	 * {@inheritDoc}
	 */
	override def deleteProject(id: Long): Unit = {
		val stmt = statements("deleteProject")
		stmt.setLong(1, id)
		stmt.executeUpdate()
		stmt.clearParameters()
	}

	/**
	 * {@inheritDoc}
	 */
	override def getDocument(id: Long): Option[Document] = {
		val stmt = statements("getDocument")
		stmt.setLong(1, id)
		val result = stmt.executeQuery()
		stmt.clearParameters()
		if (!result.next) return None
		val name = result.getString("name")
		val prepared = result.getBoolean("prepared")
		val idD = result.getLong("idD")
		Some(Document(idD, name, List(), prepared))
	}

	/**
	 * {@inheritDoc}
	 */
	override def addDocument(document: Document, projectID: Long): Document = {
		val stmt = statements("addDocument")
		stmt.setString(1, document.name)
		stmt.setBoolean(2, document.prepared)
		stmt.setLong(3, projectID)
		stmt.executeUpdate()
		stmt.clearParameters()
		val res = getDocumentsOfProject(projectID).find(d => d.name == document.name).get
		res
	}

	/**
	 * {@inheritDoc}
	 */
	override def deleteDocument(id: Long): Unit = {
		 val stmt = statements("deleteDocument")
		stmt.setLong(1, id)
		stmt.executeUpdate()
		stmt.clearParameters()
	}

	/**
	 * {@inheritDoc}
	 */
	override def getPage(id: Long): Option[Page] = {
		 val stmt = statements("getPage")
		stmt.setLong(1, id)
		val res = stmt.executeQuery()
		stmt.clearParameters()
		if (!res.next) return None
		val groundTruth = res.getString("groundTruth")
		val idP = res.getLong("idP")
		Some(Page(idP, groundTruth, List()))
	}

	/**
	 * {@inheritDoc}
	 */
	override def addPage(page: Page, documentId: Long): Page = {
		 val stmt = statements("addPage")
		stmt.setString(1, page.groundTruth)
		stmt.setLong(2, documentId)
		stmt.executeUpdate()
		stmt.clearParameters()
		val res = getPagesOfDocument(documentId).find(p => p.groundTruth == page.groundTruth).get
		res
	}

	/**
	 * {@inheritDoc}
	 */
	override def deletePage(id: Long): Unit = {
		val stmt = statements("deletePage")
		stmt.setLong(1, id)
		stmt.executeUpdate()
		stmt.clearParameters()
	}

	/**
	 * {@inheritDoc}
	 */
	override def getExample(id: Long): Option[Example] = {
		 val stmt = statements("getExample")
		stmt.setLong(1, id)
		val res = stmt.executeQuery()
		stmt.clearParameters()
		if (!res.next) return None
		val imagePath = res.getString("imagePath")
		val transcript =
			res.getString("transcript") match {
				case "null" => None
				case t => Some(t)
			}
		val enabled = res.getBoolean("enabled")
		val validated = res.getBoolean("validated")
		val idE = res.getLong("idE")
		Some(Example(idE, imagePath, transcript, enabled, validated))
	}

	/**
	 * {@inheritDoc}
	 */
	override def addExample(example: Example, pageId: Long): Example = {
		val stmt = statements("addExample")
		stmt.setString(1, example.imagePath)
		stmt.setString(2, example.transcript.orNull)
		stmt.setLong(3, pageId)
		stmt.setBoolean(4, example.enabled)
		stmt.setBoolean(5, example.validated)
		stmt.executeUpdate()
		stmt.clearParameters()
		val res = getExamplesOfPage(pageId).find(e => e.imagePath == example.imagePath).get
		res
	}

	/**
	 * {@inheritDoc}
	 */
	override def deleteExample(id: Long): Unit = {
		val stmt = statements("deleteExample")
		stmt.setLong(1, id)
		stmt.executeUpdate()
		stmt.clearParameters()
	}

	/**
	 * {@inheritDoc}
	 */
	override def getAllProject: Iterable[Project] = {
		val stmt = statements("getAllProjects")
		val res = stmt.executeQuery()
		stmt.clearParameters()
		val projects = new ArrayBuffer[Project]()
		while(res.next) {
			val name = res.getString("name")
			val recogniser = RecogniserType.withName(res.getString("recogniser"))
			val id = res.getLong("id")
			projects += Project(id, name, recogniser, List())
		}
		projects
	}

	/**
	 * {@inheritDoc}
	 */
	override def getDocumentsOfProject(id: Long): Iterable[Document] = {
		 val stmt = statements("getDocumentsOfProject")
		stmt.setLong(1, id)
		val res = stmt.executeQuery()
		stmt.clearParameters()
		val documents = new ArrayBuffer[Document]()
		while (res.next) {
			val name = res.getString("name")
			val prepared = res.getBoolean("prepared")
			val idD = res.getLong("idD")
			documents += Document(idD, name, List(), prepared)
		}
		documents
	}

	/**
	 * {@inheritDoc}
	 */
	override def getPagesOfDocument(id: Long): Iterable[Page] = {
		val stmt = statements("getPagesOfDocument")
		stmt.setLong(1, id)
		val res = stmt.executeQuery()
		stmt.clearParameters()
		val pages = new ArrayBuffer[Page]()
		while (res.next) {
			val groundTruth = res.getString("groundTruth")
			val idP = res.getLong("idP")
			pages += Page(idP, groundTruth, List())
		}
		pages
	}

	/**
	 * {@inheritDoc}
	 */
	override def getExamplesOfPage(id: Long): Iterable[Example] = {
		val stmt = statements("getExamplesOfPage")
		stmt.setLong(1, id)
		val res = stmt.executeQuery()
		stmt.clearParameters()
		val examples = new ArrayBuffer[Example]()
		while (res.next) {
			val imagePath = res.getString("imagePath")
			val transcript =
				res.getString("transcript") match {
					case "null" | null => None
					case t => Some(t)
				}
			val enabled = res.getBoolean("enabled")
			val validated = res.getBoolean("validated")
			val idE = res.getLong("idE")
			examples += Example(idE, imagePath, transcript, enabled, validated)
		}
		examples
	}

	/**
	 * {@inheritDoc}
	 */
	override def saveExampleEdition(examples: Iterable[Example]): Unit = {
		examples.foreach(example => {
			 val stmt = statements("saveExampleEdition")
			stmt.setString(1, example.imagePath)
			stmt.setString(2, example.transcript.orNull)
			stmt.setBoolean(3, example.enabled)
			stmt.setBoolean(4, example.validated)
			stmt.setLong(5, example.id)
			stmt.executeUpdate()
			stmt.clearParameters()
		})
	}

	/**
	 * {@inheritDoc}
	 */
	override def documentArePrepared(documentsId: Iterable[Long]): Unit = {
		documentsId.foreach(docId => {
			val stmt = conn.prepareStatement("UPDATE documents SET prepared=1 WHERE idD=?")
			stmt.setLong(1, docId)
			stmt.executeUpdate()
			stmt.clearParameters()
		})
	}
}
