package model.database

import java.sql.DriverManager

abstract class DatabaseConnector {
  val conn = DriverManager.getConnection("jdbc:sqlite:agnosco.db")
  conn.setAutoCommit(false)

  
}
