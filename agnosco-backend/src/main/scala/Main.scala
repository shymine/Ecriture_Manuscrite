import java.net.URI
import java.util.logging.Logger

import org.glassfish.grizzly.http.server.HttpServer
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory
import org.glassfish.jersey.server.ResourceConfig
import resource.AgnoscoResource

import scala.collection.mutable.ArrayBuffer

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

	def launch(): Unit = {
		import model.common._
		import model.database.DatabaseConnector

		val database: DatabaseConnector = new DatabaseConnector()

		val projects = new ArrayBuffer[Project]()
		val documents = new ArrayBuffer[Document]()
		val pages = new ArrayBuffer[Page]()
		val examples = new ArrayBuffer[Example]()

		database.connect

		projects += database.addProject(Project(-1, "super projet", RecogniserType.Laia, List()))
		projects += database.addProject(Project(-1, "project de la vida", RecogniserType.None, List()))

		documents += database.addDocument(Document(-1, "docu numero uno", List(), false), projects(0).id)
		documents += database.addDocument(Document(-1, "docu numero dos", List(), false), projects(0).id)
		documents += database.addDocument(Document(-1, "Glouglou", List(), false), projects(1).id)

		pages += database.addPage(Page(-1, "imagePath1", "groundTruth1", List()), documents(0).id)
		pages += database.addPage(Page(-1, "imagePath2", "groundTruth2", List()), documents(0).id)
		pages += database.addPage(Page(-1, "imagePath3", "groundTruth3", List()), documents(0).id)
		pages += database.addPage(Page(-1, "imagePath4", "groundTruth4", List()), documents(1).id)
		pages += database.addPage(Page(-1, "imagePath5", "groundTruth5", List()), documents(1).id)
		pages += database.addPage(Page(-1, "imagePath6", "groundTruth6", List()), documents(2).id)
		pages += database.addPage(Page(-1, "imagePath7", "groundTruth7", List()), documents(2).id)
		pages += database.addPage(Page(-1, "imagePath8", "groundTruth8", List()), documents(2).id)

		examples += database.addExample(Example(-1, "example1/imgPath", None, true, false), pages(0).id)
		examples += database.addExample(Example(-1, "example2/imgPath", Some("pouark"), true, false), pages(0).id)
		examples += database.addExample(Example(-1, "example3/imgPath", None, true, false), pages(0).id)
		examples += database.addExample(Example(-1, "example4/imgPath", None, true, false), pages(1).id)
		examples += database.addExample(Example(-1, "example5/imgPath", None, true, false), pages(2).id)
		examples += database.addExample(Example(-1, "example6/imgPath", None, true, false), pages(2).id)
		examples += database.addExample(Example(-1, "example7/imgPath", Some("couclaclou"), true, false), pages(2).id)
		examples += database.addExample(Example(-1, "example8/imgPath", Some("biyour missieur"), true, false), pages(2).id)
		examples += database.addExample(Example(-1, "example9/imgPath", None, true, false), pages(3).id)
		examples += database.addExample(Example(-1, "example10/imgPath", None, true, false), pages(4).id)
		examples += database.addExample(Example(-1, "example11/imgPath", Some("bouark"), true, false), pages(4).id)
		examples += database.addExample(Example(-1, "example12/imgPath", None, true, false), pages(5).id)
		examples += database.addExample(Example(-1, "example13/imgPath", None, true, false), pages(6).id)
		examples += database.addExample(Example(-1, "example14/imgPath", Some("plougastel"), true, false), pages(6).id)
		examples += database.addExample(Example(-1, "example15/imgPath", None, true, false), pages(7).id)
		examples += database.addExample(Example(-1, "example16/imgPath", None, true, false), pages(7).id)
		examples += database.addExample(Example(-1, "example17/imgPath", Some("baba ba alors,"), true, false), pages(7).id)

		database.disconnect
		println("projects")
		projects.foreach(p => print(s"id: ${p.id},"))
		println()
		println("documents")
		documents.foreach(p => print(s"id: ${p.id},"))
		println()
		println("pages")
		pages.foreach(p => print(s"id: ${p.id},"))
		println()
		println("examples")
		examples.foreach(p => print(s"id: ${p.id},"))
		println()
		println()

	}
	/*
	def main(args: Array[String]) : Unit = {
		val bld = new BlurLineDetector("10.132.11.85", "10.132.9.2", 7007)
		val _ = bld.detectLines("/Users/cloudyhug/Desktop/DetectLignes-v1.0/Images/19180116_05.jpg")
	}*/
}