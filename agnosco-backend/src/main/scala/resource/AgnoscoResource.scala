package resource


import java.awt.image.BufferedImage
import java.io.{ByteArrayInputStream, File, FileOutputStream, PrintWriter}
import java.util.Base64

import javax.imageio.ImageIO
import javax.inject.Singleton
import javax.ws.rs._
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import model.Controller
import model.common._
import org.json.{JSONArray, JSONObject}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer


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
		try {
			val projects = controller.getAllProject
			projects.foreach(it => {
				it.documents = controller.getDocumentOfProject(it.id)
			})
			val json = new JSONArray()
			projects.foreach(project => json.put(project.toJSON))
			Response.status(200).entity(json.toString).build()
		} catch {
			case e => e.printStackTrace()
				Response.status(500).build()
		}

	}

	@POST
	@Path("/test")
	@Consumes(Array(MediaType.APPLICATION_JSON))
	def test(json: String) = {
		// println(json)
		val j = new JSONObject(json)
		// println(j.getString("test"))

		try {
			val res = j.getString("test")
			val imgByte = javax.xml.bind.DatatypeConverter.parseBase64Binary(res)
			val image: BufferedImage = ImageIO.read(new ByteArrayInputStream(imgByte))
			val file = new File("image.png")
			ImageIO.write(image, "png", file)
			/*val out = new FileOutputStream("image.tiff")
			out.write(image)
			out.close()*/
		} catch {
			case e => e.printStackTrace()
		}
		// println(decoded)/**/
		Response.status(200).build()
	}


	@DELETE
	@Path("/deleteProject/{id}")
	def deleteProject(@PathParam("id") id: Long): Response = {
		controller.deleteProject(id)
		Response.status(200).entity(true).build()
	}

	/**
	  * Creates a new project from its name and the given list of documents
	  * The list of document is represented as a String of names such as "[ 'blabla','blibli' ]"
	  * @param name The name of the project
	  * @param list The list of the name of documents for the project
	  * @return
	  */
	@POST
	@Path("/createNewProject/{project_id}/{list_docs}")
	@Consumes(Array(MediaType.APPLICATION_JSON))
	@Produces(Array(MediaType.APPLICATION_JSON))
	def createProject(@PathParam("project_id") name: String, @PathParam("list_docs") list: String): Response = {
		val listTmp = list.split(",")
		val project = controller.createProject(name, listTmp)
		Response.status(200).entity(project.toJSON.toString()).build()
	}

	/**
	  * Delete the document with the given name along with its datas
	  *
	  * @param id The id of the document to delete
	  * @return
	  */
	@DELETE
	@Path("/deleteDocument/{id}")
	def deleteDocument(@PathParam("id") id: Long): Response = {
		controller.deleteDocument(id)
		Response.status(200).entity(true).build()
	}

	/**
	  * Returns the list of the existing recognisers within the base
	  * @return Returns the list of name of the existing recognisers
	  */
	@GET
	@Path("/availableRecogniser")
	@Produces(Array(MediaType.APPLICATION_JSON))
	def getAvailableRecogniser: Response = {
        val recos = controller.getAvailableRecognisers
		val json = new JSONArray()
		recos.foreach(reco => json.put(reco))
		Response.status(200).entity(json.toString).build()
	}

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
	/**
	  * Returns the list of pages in the database of the pages that compose a document which name is given as a parameter.
	  * @param id The id of the document from which the pages are extracted
	  * @return The list of the pages of the document
	  */
	@GET
	@Path("/documentPages/{id}")
	@Produces(Array(MediaType.APPLICATION_JSON))
	def getPagesOfDocuments(@PathParam("id") id: Long): Response = {
		val json = new JSONArray()
		val pages = controller.getPagesOfDocuments(id)
		pages.foreach(it => json.put(it.toJSON))
		Response.status(200).entity(json.toString()).build()
	}

	/**
	  * Return the picture associated with the page which id is given as a parameter, along with the list of example (image and transcription) of the page
	  * @param id The id of the page in the database
	  * @return The picture of the selected page and the list of its examples
	  */
	@GET
	@Path("pageData/{id}")
	@Produces(Array(MediaType.APPLICATION_JSON))
	def getPageData(@PathParam("id") id: Long): Response = {
		val json = new JSONArray()
		println(id)
		val examples = controller.getExamplesOfPage(id)
		examples.foreach(it => json.put(it.toJSON))
		println(json.toString)
		Response.status(200).entity(json.toString).build()
	}

	/**
	  * Save in the database the modifications of the transcription describes by the JSON associated with the request
	  * The examples are send using the body. Thus, we collect it with examples as a string
	  * @return
	  */
	@POST
	@Path("/saveExampleEdits")
	@Consumes(Array(MediaType.APPLICATION_JSON))
	def saveExamplesEdits(examples: String): Response = {
		val json = new JSONArray(examples)
		println(s"examples: ${json.toString()}, length: ${json.length()}")
		for(i <- 0 until json.length()) {
			try {
				val obj = json.getJSONObject(i)
				println(s"exemple $i: $obj")
				val example = Example(obj.getLong("id"), obj.getString("imagePath"), Some(obj.getString("transcript")))
				controller.modifyTranscription(example)
			}catch {
				case e: Exception => e.printStackTrace()
			}
		}
		Response.status(200).entity(true).build()
	}

	/**
	  * Put the selected examples as Disabled
	  * @param id The id of the example to disable
	  * @return
	  */Example
	@PUT
	@Path("disableExample/{id}")
	def disableExample(@PathParam("id") id: Long): Response = {
		controller.disableExample(id)
		println("c est bien disable")
		Response.status(200).entity(true).build()
	}

	/**
	  * Put the selected examples as Enable
	  * @param id The id of the example to enable
	  * @return
	  */
	@PUT
	@Path("enableExample/{id}")
	def enableExample(@PathParam("id") id: Long): Response = {
		controller.enableExample(id)
		println("c est bien enable")
		Response.status(200).build()
	}

	/**
	  * Validate the examples given in the JSON associated with the request
	  * @return
	  */
	@POST
	@Path("validateExamples")
	@Consumes(Array(MediaType.APPLICATION_JSON))
	def validateExamples(samples: String): Response = {
		val array = new JSONArray(samples)
		var examples = new mutable.MutableList[Example]
		array.forEach(obj => {
			val json = obj.asInstanceOf[JSONObject]
			examples += Example(json.getLong("id"), json.getString("imagePath"), Some(json.getString("transcript")), validated = true)
		})
		println(s"examples: $examples")
	    controller.validateTranscriptions(examples)
		Response.status(200).build()
	}

	/*
	 * Processing
	 */

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
