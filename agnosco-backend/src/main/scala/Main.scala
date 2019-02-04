import java.net.URI
import org.glassfish.grizzly.http.server.HttpServer
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory
import org.glassfish.jersey.server.ResourceConfig
import resource.AgnoscoResource

object Main {
  // Base URI the Grizzly HTTP server will listen on
  // When building docker images, replace the address with http://0.0.0.0:4444/
  // http://localhost:4444/ is for testing purpose only.
  val HTTP_ADDRESS = "http://localhost:4444/"

  def startServer() : HttpServer = {
    val rc = new ResourceConfig(classOf[AgnoscoResource])

    GrizzlyHttpServerFactory.createHttpServer(URI.create(HTTP_ADDRESS), rc)
  }

  def main(args: Array[String]) : Unit = {
    val server = startServer()

    System.in.read()
    server.shutdownNow()
  }
}