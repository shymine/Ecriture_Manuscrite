package model

import model.database.{Database, DatabaseSqlite}
import model.preparation.{Processing, ProcessingImpl}
import model.recogniser.Recogniser
import model.recogniser.laia.LaiaConnector

object ImplFactory {

	def databaseImpl: Database = new DatabaseSqlite
	def recogniserImpl: Recogniser = new LaiaConnector
	def processingImpl: Processing = new ProcessingImpl
}
