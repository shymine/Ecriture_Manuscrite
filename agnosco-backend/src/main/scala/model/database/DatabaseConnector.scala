package model.database

import java.sql.DriverManager
import org.sqlite.SQLiteException
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

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
}
