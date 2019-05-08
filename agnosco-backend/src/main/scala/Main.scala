import java.io.{File, FileNotFoundException}
import java.net.URI
import java.util.logging.Logger

import model.ImplFactory
import model.preparation.PreparatorImpl
import org.glassfish.grizzly.http.server.HttpServer
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory
import org.glassfish.jersey.server.ResourceConfig
import resource.AgnoscoResource

import scala.collection.mutable.ArrayBuffer
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
		//environmentSetup()
		val server = startServer()
		System.in.read()
		server.shutdownNow()
	}

	/*
	def main(args: Array[String]) : Unit = {
		val bld = new BlurLineDetector("10.132.11.85", "10.132.9.2", 7007)
		val _ = bld.detectLines("/Users/cloudyhug/Desktop/DetectLignes-v1.0/Images/19180116_05.jpg")
	}*/
}
