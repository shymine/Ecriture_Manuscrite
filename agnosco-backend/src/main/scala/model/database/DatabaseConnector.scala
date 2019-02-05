package model.database

import java.sql.DriverManager
import org.sqlite.SQLiteException
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

abstract class DatabaseConnector {
  val DATABASE_NAME = "agnosco"
  val THUMBNAILS_FOLDER = "thumbnails"

  Class.forName("org.sqlite.JDBC")
  private val conn = DriverManager.getConnection("jdbc:sqlite:" + DATABASE_NAME + ".db")
  conn.setAutoCommit(false)

  // Thumbnails folder creation (if it does not exist)
  createFolder(THUMBNAILS_FOLDER)

  // Tables creation (if they do not exist)
  createTable(
    "table_author",
    "id INTEGER PRIMARY KEY NOT NULL, " +
    "name VARCHAR(256)")

  createTable(
    "table_type",
    "id INTEGER PRIMARY KEY NOT NULL, " +
    "name VARCHAR(256)")

  createTable(
    "table_operating_mode",
    "id INTEGER PRIMARY KEY NOT NULL, " +
    "name VARCHAR(256)")

  createTable(
    "table_recogniser",
    "id INTEGER PRIMARY KEY NOT NULL, " +
    "name VARCHAR(256)")

  createTable(
    "table_document",
    "id INTEGER PRIMARY KEY NOT NULL, " +
    "name VARCHAR(256), " +
    "idAuthor INTEGER, " +
    "idType INTEGER, " +
    "idMode INTEGER, " +
    "dateAdd DATETIME, " +
    "FOREIGN KEY(idAuthor) REFERENCES table_author(id), " +
    "FOREIGN KEY(idType) REFERENCES table_type(id), " +
    "FOREIGN KEY(idMode) REFERENCES table_operating_mode(id)")

  createTable(
    "table_examples",
    "id INTEGER PRIMARY KEY NOT NULL, " +
    "idOwner INTEGER, " +
    "picture VARCHAR(256), " +
    "FOREIGN KEY(idOwner) REFERENCES table_document(id)")

  createTable(
    "table_ground_truth",
    "id INTEGER PRIMARY KEY NOT NULL, " +
    "transcript VARCHAR(1024), " +
    "idExample INTEGER, " +
    "FOREIGN KEY(idExample) REFERENCES table_examples(id)")

  createTable(
    "table_user_annotations",
    "id INTEGER PRIMARY KEY NOT NULL, " +
    "transcript VARCHAR(256), " +
    "idExample INTEGER, " +
    "creationDate DATETIME, " +
    "FOREIGN KEY(idExample) REFERENCES table_examples(id)")

  createTable(
    "table_transcriptions",
    "id INTEGER PRIMARY KEY NOT NULL, " +
    "transcript VARCHAR(256), " +
    "idExample INTEGER, " +
    "idRecognizer INTEGER, " +
    "creationDate DATETIME, " +
    "FOREIGN KEY(idExample) REFERENCES table_examples(id), " +
    "FOREIGN KEY(idRecognizer) REFERENCES table_recogniser(id)")

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
