package model.database

import java.sql.DriverManager

import org.sqlite.SQLiteException
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

import model.common.{Document, Example, Page, Project}

import scala.collection.mutable.ArrayBuffer

class DatabaseConnector {

  val DATABASE_NAME = "agnosco"
  val THUMBNAILS_FOLDER = "thumbnails"

  Class.forName("org.sqlite.JDBC")
  private val conn = DriverManager.getConnection("jdbc:sqlite:" + DATABASE_NAME + ".db")
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

  private def createFolder(folderPath : String) : Unit = {
    val path = Paths.get(folderPath)
    try
      Files.createDirectories(path)
    catch {
      case e: IOException =>
        e.printStackTrace()
    }
  }

  private def createTable(tableName : String, query : String) : Unit = {
    try {
      val sql = "CREATE TABLE IF NOT EXISTS " + tableName + "(" + query + ")"
      val pstmt = conn.prepareStatement(sql)
      pstmt.executeUpdate()
      conn.commit()
      System.out.println("Table " + tableName + " created successfully.")
    } catch {
      case e: SQLiteException =>
        System.out.println("Table " + tableName + " has already been created.")
        e.printStackTrace()
      case e: Exception =>
        System.err.println(e.getClass.getName + ": " + e.getMessage)
    }
  }

  def getExample(id : Int) : Option[Example] = {
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

  def getExamplesOfPage(id : Int) : ArrayBuffer[Example] = {
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

  def getPage(id : Int) : Option[Page] = {
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

  def getPagesOfDocument(id : Int) : ArrayBuffer[Page] = {
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

  def getDocument(id : Int) : Option[Document] = {
    val sql = "SELECT * FROM pages WHERE id = ?"
    val pstmt = conn.prepareStatement(sql)
    pstmt.setInt(1, id)
    val resultSet = pstmt.executeQuery()

    if (!resultSet.next) return None

    val name = resultSet.getString("name")
    val pages = getPagesOfDocument(id)

    Some(Document(id, name, pages, true))
  }

  def getDocumentsOfProject(id : Int) : ArrayBuffer[Document] = {
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

  def getProject(id : Int) : Option[Project] = {
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
      case e : Exception => None
    }
  }
}
