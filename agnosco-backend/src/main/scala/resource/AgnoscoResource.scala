package resource


import javax.inject.Singleton
import javax.ws.rs._
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import model.Controller
import model.common.{Document, Page, Project, RecogniserType}
import org.json.{JSONArray, JSONObject}


@Singleton
@Path("agnosco/base")
class AgnoscoResource {

	/*@POST
	  @Path("/createGame/{p1}/{p2}/{name}/{ai}/{bt}")
	  @Produces(Array(MediaType.APPLICATION_JSON))
	  def gameParameter(@PathParam("p1") p1: String, @PathParam("p2") p2: String, @PathParam("name") name: String, @PathParam("ai") ai: String, @PathParam("bt") bt: String): Nothing = {}
	*/

	val controller: Controller = new Controller

	/**
	  * Returns the list of the name of the projects with the list of the name of the documents for each project.
	  *
	  * @return Returns an array of json describing the first level structure of the app: projects and documents
	  */
	@GET
	@Path("/projectsAndDocuments")
	@Produces(Array(MediaType.APPLICATION_JSON))
	def getProjectAndDocuments: Response = {

		val projects =  List(Project(3,"coucou", RecogniserType.None, List()),
			Project(4,"bisoux", RecogniserType.Laia, List(Document(5, "docu", List(), false)))) //controller.getAllProject

		val json = new JSONArray()
		projects.foreach(project => json.put(project.toJSON))
		Response.status(200).entity(json.toString).build()

	}

	/**
	  * Creates a new project from its name and the given list of documents
	  *
	  * @param id The ID of the project
	  * @param list The list of the name of documents for the project
	  * @return
	  */
	@POST
	@Path("/createNewProject/{project_id}/{list_docs}")
	@Consumes(Array(MediaType.APPLICATION_JSON))
	@Produces(Array(MediaType.APPLICATION_JSON))
	def createProject(@PathParam("project_id") name: String, @PathParam("list_docs") list: String): Response = {
		val listTmp = list.replace('[', ' ').replace(']', ' ').split(",")
		controller.createProject(name, listTmp)
	}
//
//	/**
//	  * Delete the document with the given name along with its datas
//	  *
//	  * @param name The name of the document to delete
//	  * @return
//	  */
//	@DELETE
//	@Path("/deleteDocument/{id}")
//	def deleteDocument(@PathParam("id") id: Long) = {
//	     controller.deleteDocument(id)
//	}
//
//	/**
//	  * Returns the list of the existing recognisers within the base
//	  * @return Returns the list of name of the existing recognisers
//	  */
//	@GET
//	@Path("/availableRecogniser")
//	@Produces(Array(MediaType.APPLICATION_JSON))
//	def getAvailableRecogniser = {
//        controller.getAvailableRecognisers
//		val json = new JSONObject()
//		Response.status(200).entity(json.toString).build()
//	}
//
//	/**
//	  * Groups every examples in the base that are contained in the projects using the recogniser wich name is given in parameter. The examples must be usable and validated. The examples are then exported as a training set to the named recogniser.
//	  * @param name The name of the Recogniser to export to
//	  * @return
//	  */
//	@POST
//	@Path("/exportRecogniserExamples/{name}")
//	def exportRecogniserExamples(@PathParam("name") name: String) = {
//      //check the recogniser and change if needed
//      //retrieve every examples where the project use this reco
//      //controller.getAllProject
//      //controller.trainAI(exampleSet)
//	}
//
//	/*
//	 * Annotation & Validation
//	 */
//
//	/**
//	  * Returns the list of id in the database of the pages that compose a document which name is given as a parameter.
//	  * @param name The name of the document from which the pages are extracted
//	  * @return The list of the id of the pages
//	  */
//	@GET
//	@Path("/documentPages/{name}")
//	@Produces(Array(MediaType.APPLICATION_JSON))
//	def getPagesOfDocuments(@PathParam("name") name: String) = {
//	      controller.getAllProject
//	      //filter correctly the data
//	}
//
//	/**
//	  * Return the picture associated with the page which id is given as a parameter, along with the list of example (image and transcription) of the page
//	  * @param id The id of the page in the database
//	  * @return The picture of the selected page and the list of its examples
//	  */
//	@GET
//	@Path("pageData/{id}")
//	@Produces(Array(MediaType.APPLICATION_JSON))
//	def getPageData(@PathParam("id") id: Int) = {
//	    controller.getAllProject
//		//filter correctly the data
//		val json = new JSONObject()
//
//		Response.status(200).entity(json.toString).build()
//	}
//
//	/**
//	  * Save in the database the modifications of the transcription describes by the JSON associated with the request
//	  * @return
//	  */
//	@POST
//	@Path("saveExampleEdits")
//	@Consumes(Array(MediaType.APPLICATION_JSON))
//	def saveExamplesEdits = {
//	      //controller.modifyTranscription(samples)
//
//		val json = new JSONObject()
//
//		Response.status(200).entity(json.toString).build()
//	}
//
//	/**
//	  * Put the selected examples as Disabled
//	  * @param id The id of the example to disable
//	  * @return
//	  */
//	@PUT
//	@Path("disableExample/{id}")
//	def disableExample(@PathParam("id") id: Int) = {
//		controller.disableExample(id)
//	}
//
//	/**
//	  * Put the selected examples as Enable
//	  * @param id The id of the example to enable
//	  * @return
//	  */
//	@PUT
//	@Path("enableExample/{id}")
//	def enableExample(@PathParam("id") id: Int) = {
//	     controller.enableExample(id)
//	}
//
//	/**
//	  * Validate the examples given in the JSON associated with the request
//	  * @return
//	  */
//	@POST
//	@Path("validateExamples")
//	@Consumes(Array(MediaType.APPLICATION_JSON))
//	def validateExamples = {
//	     //controller.validateTranscription(samples)
//	}
//
//	/*
//	 * Processing
//	 */
//
//	/**
//	  * Returns the list of id and images associated with the pages of the documents with the given name
//	  * @param name The name of the documents
//	  * @return The JSON of the list of id and images of the pages of the document
//	  */
//	@GET
//	@Path("documentPagesWithImages/{name}")
//	@Produces(Array(MediaType.APPLICATION_JSON))
//	def documentPagesWithImages(@PathParam("name") name: String) = {
//		controller.getAllProject
//		//filter the data
//	}
//
//	/**
//	  * Add to the database the groundtruth given as JSON along with the request and bind it to the page which name is given as a parameter
//	  * @param name The name of the document
//	  * @return
//	  */
//	@POST
//	@Path("addPageGroudtruth/{name}")
//	def addDocumentGroundTruth(@PathParam("name") name: String) = {
//		//controller.addGrounTruth(page, piff)
//	}
//
//	/**
//	  * Send the list of examples without transcription contained in the document to the recogniser associated with the project
//	  * @param name The name of the document
//	  * @return Returns the list of the examples that are transcripted by the recogniser (a copy of the one given to it)
//	  */
//	@GET
//	@Path("recogniseImages/{name}")
//	@Produces(Array(MediaType.APPLICATION_JSON))
//	def recogniseImages(@PathParam("name") name: String) = {
//		//samples = exmaples of the document
//		//controller.recognizeAI(samples)
//	}
//
//	/**
//	  * Prepare the examples from the document given in parameter
//	  * @param document The name of the document to prepare
//	  * @return
//	  */
//	@POST
//	@Path("prepareExamplesOfDocument/{document}")
//	def prepareExamplesOfDocument(@PathParam("document") document: String) = {
//		// controller.prepareData(vtFiles)
//	}
//
//	@POST
//	@Path("prepareExamplesOfPage/{page}")
//	def prepareExamplesOfPage(@PathParam("page") page: Int): Unit = {
//		//controller.prepareData(vtFile)
//	}

}
