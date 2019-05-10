import java.io.{File, FileNotFoundException}
import java.net.URI
import java.util.logging.Logger

import org.glassfish.grizzly.http.server.HttpServer
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory
import org.glassfish.jersey.server.ResourceConfig
import resource.AgnoscoResource

import model.common.{globalDataFolder, globalExportFolder, pythonImageCropperExecutablePath}

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

	/**
	  * Set up the folders data and export
	  * data store the images for the pages and the examples
	  * export store the images and transcripts for the examples that are exported
	  */
	def environmentSetup() : Unit = {
		if (!new File(pythonImageCropperExecutablePath).exists()) {
			throw new FileNotFoundException(s"missing file : $pythonImageCropperExecutablePath")
		}

		val dataFolder = new File(globalDataFolder)
		if(!dataFolder.exists()) {
			dataFolder.mkdir()
		}
		val exportFolder = new File(globalExportFolder)
		if(!exportFolder.exists()) {
			exportFolder.mkdir()
		}
	}

	def main(args: Array[String]) : Unit = {
		environmentSetup()
		val server = startServer()
		System.in.read()
		server.shutdownNow()
	}
}
