package model.database
import java.io.IOException
import java.nio.file.{Files, Paths}
import java.sql.{Connection, DriverManager}

import model.common.{Document, Example, Page, Project}
import org.sqlite.SQLiteException

import scala.collection.mutable.ArrayBuffer

object DatabaseSqlite {
	private var idCount: Long = 0
	def incrementID: Long = {
		idCount += 1
		idCount
	}
	def setIDCounter(id: Long) = idCount = id
}

class DatabaseSqlite extends Database {

	val DATABASE_NAME = "agnosco"
	val THUMBNAILS_FOLDER = "thumbnails"

	private var conn: Connection = null


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
			"id INTEGER PRIMARY KEY NOT NULL, " +
				"name VARCHAR(64), " +
				"recogniser VARCHAR(64)")

		createTable(
			"documents",
			"id INTEGER PRIMARY KEY NOT NULL, " +
				"name VARCHAR(64), " +
				"projectId INTEGER, " +
				"FOREIGN KEY(projectId) REFERENCES projects(id)")

		createTable(
			"pages",
			"id INTEGER PRIMARY KEY NOT NULL, " +
				"imagePath VARCHAR(256), " +
				"groundTruthPath VARCHAR(256), " +
				"documentId INTEGER, " +
				"FOREIGN KEY(documentId) REFERENCES documents(id)")

		createTable(
			"examples",
			"id INTEGER PRIMARY KEY NOT NULL, " +
				"imagePath VARCHAR(256), " +
				"transcript VARCHAR(256), " +
				"pageId INTEGER, " +
				"FOREIGN KEY(pageId) REFERENCES pages(id)")

		createTable(
			"identifiers",
			"name VARCHAR(10) PRIMARY KEY NOT NULL, " +
				"id INTEGER NOT NULL"
		)

		try {
			val sql = "SELECT id FROM identifiers WHERE name = counter"
			val pstmt = conn.createStatement()
			val id = pstmt.executeQuery(sql)
			println(id)
			DatabaseSqlite.setIDCounter(id.getLong("id"))
			pstmt.close()
			conn.commit()
			true
		}catch {
			case e: SQLiteException => {
				println("First Time, setting the counter to 0")
				DatabaseSqlite.setIDCounter(0)
				true
			}
			case e: Exception => {
					System.err.println(e.getStackTrace)
					false
			}
		}
	}

	override def disconnect: Boolean = {
		try {
			val sql = s"UPDATE identifiers SET id = ${DatabaseSqlite.idCount} WHERE name = counter"
			val pstmt = conn.createStatement()
			pstmt.executeUpdate(sql)
			pstmt.close()
			conn.commit()
			conn.close()
			true
		}catch {
			case e: SQLiteException => {
				System.err.println(e.getStackTrace)
				false
			}
		}
	}

	override def getProject(id: Int): Option[Project] = {
		val sql = "SELECT * FROM projects WHERE id = ?"
		val pstmt = conn.prepareStatement(sql)
		pstmt.setInt(1, id)
		val resultSet = pstmt.executeQuery()

		if (!resultSet.next) return None

		val name = resultSet.getString("name")
		try {
			val recogniser = null // TODO : resultSet.getString("recogniser")
			val documents = getDocumentsOfProject(id)

			Some(Project(id, name, recogniser, documents))
		} catch {
			case _ : Exception => None
		}
	}

	override def addProject(project: Project): Unit = {
		val sql = s"INSERT INTO projects (name, recogniser) VALUES (${DatabaseSqlite.incrementID}, ${project.name}, ${project.recogniser.toString})"
	}

	override def deleteProject(id: Int): Unit = ???

	override def getDocument(id: Int): Option[Document] = {
		val sql = "SELECT * FROM pages WHERE id = ?"
		val pstmt = conn.prepareStatement(sql)
		pstmt.setInt(1, id)
		val resultSet = pstmt.executeQuery()

		if (!resultSet.next) return None

		val name = resultSet.getString("name")
		val pages = getPagesOfDocument(id)

		Some(Document(id, name, pages, true))
	}

	override def addDocument(document: Document): Unit = ???

	override def deleteDocument(id: Int): Unit = ???

	override def getPage(id: Int): Option[Page] = {
		val sql = "SELECT * FROM pages WHERE id = ?"
		val pstmt = conn.prepareStatement(sql)
		pstmt.setInt(1, id)
		val resultSet = pstmt.executeQuery()

		if (!resultSet.next) return None

		val imagePath = resultSet.getString("imagePath")
		val groundTruthPath = resultSet.getString("groundTruthPath")
		val examples = getExamplesOfPage(id)

		Some(Page(id, imagePath, groundTruthPath, examples, true))
	}

	override def addPage(page: Page): Unit = ???

	override def deletePage(id: Int): Unit = ???

	override def getExample(id: Int): Option[Example] = {
		val sql = "SELECT * FROM examples WHERE id = ?"
		val pstmt = conn.prepareStatement(sql)
		pstmt.setInt(1, id)
		val resultSet = pstmt.executeQuery()

		if (!resultSet.next) return None

		val imagePath = resultSet.getString("imagePath")
		val transcript =
			resultSet.getString("transcript") match {
				case "null" => None
				case t => Some(t)
			}

		Some(Example(id, imagePath, transcript))
	}

	override def addExample(example: Example): Unit = ???

	override def deleteExample(id: Int): Unit = ???

	override def getAllProject(): Iterable[Project] = ???

	override def getDocumentsOfProject(id: Int): Iterable[Document] = {
		val sql = "SELECT * FROM documents WHERE idProject = ?"
		val pstmt = conn.prepareStatement(sql)
		pstmt.setInt(1, id)
		val resultSet = pstmt.executeQuery()

		val documents = new ArrayBuffer[Document]()

		while (resultSet.next) {
			val name = resultSet.getString("name")
			val pages = getPagesOfDocument(id)

			documents += Document(id, name, pages, true)
		}

		documents
	}

	override def getPagesOfDocument(id: Int): Iterable[Page] = {
		val sql = "SELECT * FROM pages WHERE idDocument = ?"
		val pstmt = conn.prepareStatement(sql)
		pstmt.setInt(1, id)
		val resultSet = pstmt.executeQuery()

		val pages = new ArrayBuffer[Page]()

		while (resultSet.next) {
			val imagePath = resultSet.getString("imagePath")
			val groundTruthPath = resultSet.getString("groundTruthPath")
			val examples = getExamplesOfPage(id)

			pages += Page(id, imagePath, groundTruthPath, examples, true)
		}

		pages
	}

	override def getExamplesOfPage(id: Int): Iterable[Example] = {
		val sql = "SELECT * FROM examples WHERE pageId = ?"
		val pstmt = conn.prepareStatement(sql)
		pstmt.setInt(1, id)
		val resultSet = pstmt.executeQuery()

		val examples = new ArrayBuffer[Example]()

		while (resultSet.next) {
			val imagePath = resultSet.getString("imagePath")
			val transcript =
				resultSet.getString("transcript") match {
					case "null" => None
					case t => Some(t)
				}

			examples += Example(id, imagePath, transcript)
		}

		examples
	}

	override def saveExampleEdition(examples: Iterable[Example]): Unit = ???
}
