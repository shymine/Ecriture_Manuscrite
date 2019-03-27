import java.net.URI
import java.util.logging.Logger

import org.glassfish.grizzly.http.server.HttpServer
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory
import org.glassfish.jersey.server.ResourceConfig
import resource.AgnoscoResource

object Main {
	// Base URI the Grizzly HTTP server will listen on
	// When building docker images, replace the address with http://0.0.0.0:4444/
	// http://localhost:4444/ is for testing purpose only.
	val HTTP_ADDRESS = "http://localhost:8000/"
	val LOGGER: Logger = Logger.getLogger(Main.getClass.getName)

	def startServer() : HttpServer = {
	    val rc = new ResourceConfig()
		rc.registerClasses(classOf[AgnoscoResource])
		GrizzlyHttpServerFactory.createHttpServer(URI.create(HTTP_ADDRESS), rc)
	}

	def main(args: Array[String]) : Unit = {
		/*import model.preparation.ProcessingImpl

		val examples = new ProcessingImpl().prepareData(
			List("/Users/cloudyhug/Documents/cours/projet/test.xml",
					 "/Users/cloudyhug/Documents/cours/projet/testtest.xml"))
		println(examples)*/
		
		launch
	    val server = startServer()
	    System.in.read()
	    server.shutdownNow()
	}

	def launch: Unit = {
		import model.common._
		import model.database.DatabaseConnector

		val database: DatabaseConnector = new DatabaseConnector()

		database.connect

		val project1: Project = database.addProject(Project(-1, "super projet", RecogniserType.Laia, List()))
		val project2: Project = database.addProject(Project(-1, "project de la vida", RecogniserType.None, List()))

		val document1: Document = database.addDocument(Document(-1, "docu numero uno", List(), false), project1.id)
		val document2: Document = database.addDocument(Document(-1, "docu numero dos", List(), false), project1.id)
		val document3 : Document = database.addDocument(Document(-1, "Glouglou", List(), false), project2.id)

		val page1 = database.addPage(Page(-1, "imagePath1", "groundTruth1", List()), document1.id)
		val page2 = database.addPage(Page(-1, "imagePath2", "groundTruth2", List()), document1.id)
		val page3 = database.addPage(Page(-1, "imagePath3", "groundTruth3", List()), document1.id)
		val page4 = database.addPage(Page(-1, "imagePath4", "groundTruth4", List()), document2.id)
		val page5 = database.addPage(Page(-1, "imagePath5", "groundTruth5", List()), document2.id)
		val page6 = database.addPage(Page(-1, "imagePath6", "groundTruth6", List()), document3.id)
		val page7 = database.addPage(Page(-1, "imagePath7", "groundTruth7", List()), document3.id)
		val page8 = database.addPage(Page(-1, "imagePath8", "groundTruth8", List()), document3.id)

		val example1 = database.addExample(Example(-1, "example1/imgPath", None, true, false), page1.id)
		val example2 = database.addExample(Example(-1, "example2/imgPath", Some("pouark"), true, false), page1.id)
		val example3 = database.addExample(Example(-1, "example3/imgPath", None, true, false), page1.id)
		val example4 = database.addExample(Example(-1, "example4/imgPath", None, true, false), page2.id)
		val example5 = database.addExample(Example(-1, "example5/imgPath", None, true, false), page3.id)
		val example6 = database.addExample(Example(-1, "example6/imgPath", None, true, false), page3.id)
		val example7 = database.addExample(Example(-1, "example7/imgPath", Some("couclaclou"), true, false), page3.id)
		val example8 = database.addExample(Example(-1, "example8/imgPath", Some("biyour missieur"), true, false), page3.id)
		val example9 = database.addExample(Example(-1, "example9/imgPath", None, true, false), page4.id)
		val example10 = database.addExample(Example(-1, "example10/imgPath", None, true, false), page5.id)
		val example11 = database.addExample(Example(-1, "example11/imgPath", Some("bouark"), true, false), page5.id)
		val example12 = database.addExample(Example(-1, "example12/imgPath", None, true, false), page6.id)
		val example13 = database.addExample(Example(-1, "example13/imgPath", None, true, false), page7.id)
		val example14 = database.addExample(Example(-1, "example14/imgPath", Some("plougastel"), true, false), page7.id)
		val example15 = database.addExample(Example(-1, "example15/imgPath", None, true, false), page8.id)
		val example16 = database.addExample(Example(-1, "example16/imgPath", None, true, false), page8.id)
		val example17 = database.addExample(Example(-1, "example17/imgPath", Some("baba ba alors,"), true, false), page8.id)

		database.disconnect
	}
	/*
	def main(args: Array[String]) : Unit = {
		val bld = new BlurLineDetector("10.132.11.85", "10.132.9.2", 7007)
		val _ = bld.detectLines("/Users/cloudyhug/Desktop/DetectLignes-v1.0/Images/19180116_05.jpg")
	}*/
}