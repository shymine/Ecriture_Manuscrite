package resource

import java.util

import javax.inject.Singleton
import javax.naming.InvalidNameException
import javax.ws.rs._
import javax.ws.rs.core.MediaType
import org.json.{JSONArray, JSONObject}

@Singleton
@Path("agnosco/base")
class AgnoscoResource {

	/*@POST
	  @Path("/createGame/{p1}/{p2}/{name}/{ai}/{bt}")
	  @Produces(Array(MediaType.APPLICATION_JSON))
	  def gameParameter(@PathParam("p1") p1: String, @PathParam("p2") p2: String, @PathParam("name") name: String, @PathParam("ai") ai: String, @PathParam("bt") bt: String): Nothing = {}
	*/

	/*
	* General
	*/

	/**
	  * Returns the list of the name of the projects with the list of the name of the documents for each project.
	  * @return Returns an array of json describing the first level structure of the app: projects and documents
	  */
	@GET
	@Path("/projectsAndDocuments")
	@Produces(Array(MediaType.APPLICATION_JSON))
	def getProjectAndDocuments: JSONArray = {
		val res = new JSONArray()
		val project1 = new JSONObject()
		val project2 = new JSONObject()

		project1.put("name", "Project1")
		project1.put("documents", List("document1.1", "document1.2"))
		project2.put("name", "Project2")
		project2.put("documents", List("document2.1", "document2.2"))

		res.put(project1)
		res.put(project2)

		res
	}

	/**
	  * Creates a new project from its name and the given list of documents
	  * @param name The name of the project
	  * @param list The list of the name of documents for the project
	  * @return
	  */
	@POST
	@Path("/createNewProject/{project_name}/{list_docs}")
	def createProject(@PathParam("project_name") name: String, @PathParam("list_docs") list: String) = ???

	/**
	  * Delete the document with the given name along with its datas
	  * @param name The name of the document to delete
	  * @return
	  */
	@DELETE
	@Path("/deleteDocument/{name}")
	def deleteDocument(@PathParam("name") name: String) = ???

	/**
	  * Returns the list of the existing recognisers within the base
	  * @return Returns the list of name of the existing recognisers
	  */
	@GET
	@Path("/availableRecogniser")
	@Produces(MediaType.APPLICATION_JSON)
	def getAvailableRecogniser: JSONObject = {
		val res = new JSONObject()

		res.put("recognisers",List("GROS RECO DE LA MORT", "YO SOY ESPANOL"))
	}

	/**
	  * Groups every examples in the base that are contained in the projects using the recogniser wich name is given in parameter. The examples must be usable and validated. The examples are then exported as a training set to the named recogniser.
	  * @param name The name of the Recogniser to export to
	  * @return
	  */
	@POST
	@Path("/exportRecogniserExamples/{name}")
	def exportRecogniserExample(@PathParam("name") name: String) = ???

	/*
	 * Annotation & Validation
	 */

	/**
	  * Returns the list of id in the database of the pages that compose a document which name is given as a parameter.
	  * @param name The name of the document from which the pages are extracted
	  * @return The list of the id of the pages
	  */
	@GET
	@Path("/documentPages/{name}")
	@Produces(Array(MediaType.APPLICATION_JSON))
	def getPagesOfDocuments(@PathParam("name") name: String) = ???

	/**
	  * Return the picture associated with the page which id is given as a parameter, along with the list of example (image and transcription) of the page
	  * @param id The id of the page in the database
	  * @return The picture of the selected page and the list of its examples
	  */
	@GET
	@Path("pageData/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	def getPageData(@PathParam("id") id: Int) = ???

	/**
	  * Save in the database the modifications of the transcription describes by the JSON associated with the request
	  * @return
	  */
	@POST
	@Path("saveExampleEdits")
	@Consumes(MediaType.APPLICATION_JSON)
	def saveExamplesEdits = ???

	/**
	  * Put the selected examples as Disabled
	  * @param id The id of the example to disable
	  * @return
	  */
	@PUT
	@Path("disableExample/{id}")
	def disableExample(@PathParam("id") id: Int) = ???

	/**
	  * Put the selected examples as Enable
	  * @param id The id of the exmaple to enable
	  * @return
	  */
	@PUT
	@Path("enableExample/{id}")
	def enableExample(@PathParam("id") id: Int) = ???

	/**
	  * Validate the examples given in the JSON associated with the request
	  * @return
	  */
	/*@POST
	@Path("validateExamples")
	@Consumes(Array(MediaType.APPLICATION_JSON))
	def validateExamples = ???*/

	/*
	 * Processing
	 */

	/**
	  * Returns the list of id and images associated with the pages of the documents with the given name
	  * @param name The name of the documents
	  * @return The JSON of the list of id and images of the pages of the document
	  */
	@GET
	@Path("documentPagesWithImages/{name}")
	@Produces(Array(MediaType.APPLICATION_JSON))
	def documentPagesWithImages(@PathParam("name") name: String) = ???

	/**
	  * Add to the database the groundtruth given as JSON along with the request and bind it to the document which name is given as a parameter
	  * @param name The name of the document
	  * @return
	  */
	@POST
	@Path("addDocumentGroudtruth/{name}")
	def addCocumentGroundTruth(@PathParam("name") name: String) = ???

	/**
	  * Send the list of examples without transcription contained in the document to the recogniser associated with the project
	  * @param name The name of the document
	  * @return Returns the list of the examples that are transcripted by the recogniser (a copy of the one given to it)
	  */
	@GET
	@Path("recogniseImages/{name}")
	@Produces(Array(MediaType.APPLICATION_JSON))
	def recogniseImages(@PathParam("name") name: String) = ???
}
