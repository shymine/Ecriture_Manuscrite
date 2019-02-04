package resource

import javax.inject.Singleton
import javax.naming.InvalidNameException
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.DELETE
import javax.ws.rs.PUT
import javax.ws.rs.core.MediaType

@Singleton
@Path("agnosco")
@Api(value = "agnosco", description = "Agnosco")
class AgnoscoResource {

  @POST
  @Path("/createGame/{p1}/{p2}/{name}/{ai}/{bt}")
  @Produces(Array(MediaType.APPLICATION_JSON))
  def gameParameter(@PathParam("p1") p1: String, @PathParam("p2") p2: String, @PathParam("name") name: String, @PathParam("ai") ai: String, @PathParam("bt") bt: String): Nothing = {

  }
}
