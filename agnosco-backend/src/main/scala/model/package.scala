import model.database.Database
import model.database.DatabaseSqlite
import model.preparation.{Processing, ProcessingImpl}
import model.recogniser.Recogniser
import model.recogniser.laia.LaiaConnector

package object model {
	def databaseImpl: Database = new DatabaseSqlite
	def preparationImpl: Processing = new ProcessingImpl
	def recogniserImpl: Recogniser = new LaiaConnector
}
