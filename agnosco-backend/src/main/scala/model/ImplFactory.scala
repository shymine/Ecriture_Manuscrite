package model

import model.database.{Database, DatabaseSqlite}
import model.preparation.{Processing, ProcessingImpl}
import model.recogniser.Recogniser
import model.recogniser.laia.LaiaRecogniser

object ImplFactory {

	def databaseImpl: Database = new DatabaseSqlite
	def recogniserImpl: Recogniser = new LaiaRecogniser
	def processingImpl: Processing = new ProcessingImpl
}
