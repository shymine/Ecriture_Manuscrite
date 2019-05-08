package model

import model.database.{Database, DatabaseSqlite}
import model.preparation.{Preparator, PreparatorImpl}
import model.recogniser.{Recogniser, SampleExport}
import model.recogniser.laia.LaiaRecogniser

object ImplFactory {
	val databaseImpl: Database = new DatabaseSqlite
	val recogniserImpl: Recogniser = new SampleExport //new LaiaRecogniser
	val preparatorImpl: Preparator = new PreparatorImpl

	def setConnectionData(ip: String, port1: Int, port2: Int): Unit = {
		preparatorImpl.setConnectionData(ip, port1, port2)
	}
}
