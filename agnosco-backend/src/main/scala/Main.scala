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
		/*
		// target/scala-2.12/classes/
		// ../../../../maurdor/AFGHJK.xml
		// ../../../../maurdor/AFGIJM.xml
		val pathToMain = getClass.getResource("").getPath
		val examples = new ProcessingImpl().prepareData(
			List(pathToMain+"../../../../maurdor/AFGHJK.xml", pathToMain+"../../../../maurdor/AFGIJM.xml"))
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

		projects += Project(-1, "project test", RecogniserType.None, List())
		documents += Document(-1, "coucou", List(), false)
		pages += Page(-1, "imagepath", "groundtruthpage", List())
		examples += Example(-1, "example image path", Some("super transcript"))

		database.connect

		projects.foreach(database.addProject)
		documents.foreach(database.addDocument(_, 1))
		pages.foreach(database.addPage(_,1))
		examples.foreach(database.addExample(_,1))

		projects.foreach(database.addProject)

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
