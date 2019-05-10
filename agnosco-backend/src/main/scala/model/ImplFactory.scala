package model

import model.database.{Database, DatabaseSqlite}
import model.preparation.{Preparator, PreparatorImpl}
import model.recogniser.{Recogniser, SampleExport}

/**
  * The implementations by default of the skeleton
  */
object ImplFactory {
	val databaseImpl: Database = new DatabaseSqlite
	val recogniserImpl: Recogniser = new SampleExport
	val preparatorImpl: Preparator = new PreparatorImpl
}
