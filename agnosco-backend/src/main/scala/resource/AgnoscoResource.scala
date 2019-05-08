package resource


import java.io.{File, FileOutputStream, PrintWriter}
import java.util

import javax.inject.Singleton
import javax.ws.rs._
import javax.ws.rs.core.{MediaType, Response, Variant}
import model.Controller
import model.common._
import model.preparation.input.PiFFReader
import org.json.{JSONArray, JSONObject}

import scala.util.control.Breaks._
import scala.collection.mutable
import scala.collection.mutable.{ArrayBuffer, ListBuffer}


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
			println(json)
			Response.status(200).entity(json.toString).build()
		} catch {
			case e: Exception => e.printStackTrace()
				Response.status(500).build()
		}

	}

	@POST
	@Path("/test")
	@Consumes(Array(MediaType.APPLICATION_JSON))
	def test(json: String): Response = {
		println(json)
		val j = new JSONObject(json)
		// println(j.getString("test"))

		try {
			val res = j.getString("test")
			val imgByte = javax.xml.bind.DatatypeConverter.parseBase64Binary(res)
			val out = new FileOutputStream("data/image.tiff")
			out.write(imgByte)
			out.close()
		} catch {
			case e: Exception => e.printStackTrace()
		}
		Response.status(200).build()
	}


	@DELETE
	@Path("/deleteProject/{id}")
	def deleteProject(@PathParam("id") id: Long): Response = {
		try {
			controller.deleteProject(id)
			Response.status(200).entity(true).build()
		}catch {
			case e: Exception => e.printStackTrace()
				Response.status(500).build()
		}
	}

	/**
	  * Creates a new project from its name and the given list of documents
	  * The list of document is represented as a String of names such as "[ 'blabla','blibli' ]"
	  * @param name The name of the project
	  * @param recogniser The type of the recogniser
	  * @return
	  */
	@POST
	@Path("/createNewProject/{project_name}/{selected_reco}")
	// @Consumes(Array(MediaType.APPLICATION_JSON))
	@Produces(Array(MediaType.APPLICATION_JSON))
	def createProject(@PathParam("project_name") name: String, @PathParam("selected_reco") recogniser: String): Response = {
		try {
			val project = controller.createProject(name, recogniser)
			Response.status(200).entity(project.toJSON.toString()).build()
		}catch {
			case e: Exception => e.printStackTrace()
				Response.status(500).build()
		}
	}

	/**
	  * Add a document to the project with the given id
	  * @param id The id of the project
	  * @param document The json of the document to add
	  */
	/*
		{
			'name':'truc', -> le nom de l'image
			// 'pages':[[img64,vt],[img64,vt]]
			'pages':[{name:'truc',image64:'ezrgrgz',vtText:'eofigzpieguh'},..]
		}
	 */
	@POST
	@Path("/addDocToProject/{project_id}")
	@Consumes(Array(MediaType.APPLICATION_JSON))
	@Produces(Array(MediaType.APPLICATION_JSON))
	def addDocToProject(@PathParam("project_id") id: Long, document: String): Response = {
		try {
			val json = new JSONObject(document)
			val arr = json.getJSONArray("pages")
			val pageList = new ArrayBuffer[Page]()
			var ok = true
			var correctVT = true

			for (i <- 0 until arr.length()) {
				breakable {
					val obj = arr.getJSONObject(i)
					val name = getFileName(obj.getString("name"))

					val vt = PiFFReader.fromString(obj.getString("vtText"))
					if (vt.isDefined) {
						val piff = vt.get

						if (piff.page.src != obj.getString("name")) {
							ok = false
							break()
						}

						// écriture image
						val imgByte = javax.xml.bind.DatatypeConverter.parseBase64Binary(obj.getString("image64"))

						val out = new FileOutputStream(globalDataFolder + "/" + obj.getString("name"))
						out.write(imgByte)
						out.close()

						// écriture de la vt
						val pw = new PrintWriter(new File(globalDataFolder + "/" + name + ".piff"))
						pw.write(piff.toJSON.toString())
						pw.close()

						pageList += Page(-1, name + ".piff", List())
					} else {
						correctVT = false
						//return Response.status(200).entity("{'error':'unhandled file format'}").build()
					}
				}
			}


			val doc = Document(-1, json.getString("name"), pageList, false)
			val res = controller.addDocToProject(id, doc)

			if(ok&&correctVT) {
				Response.status(200).entity(res.toJSON.toString()).build()
			}else if(ok&&(!correctVT)){
				Response.notAcceptable(new util.ArrayList[Variant]()).entity("{\"error\":0}").build() // vt incorrecte -> unhandled file format
			}else if((!ok)&&correctVT){
				Response.notAcceptable(new util.ArrayList[Variant]()).entity("{\"error\":1}").build() // nom incorrect
			}else {
				Response.notAcceptable(new util.ArrayList[Variant]()).entity("{\"error\":2}").build() // nom et vt incorrect
			}

		}catch {
			case e: Exception => e.printStackTrace()
				Response.status(500).build()
		}
	}

	def getFileName(str: String): String = {
		val regexp = "[.][a-zA-Z]+".r
		regexp.replaceAllIn(str,"")
	}

	def replaceImgFormat(str: String): String = {
		val regexp = "[.][a-zA-Z]+".r
		regexp.replaceAllIn(str,".png")
	}

	/**
	  * {name:'truc',image64:'ezrgrgz',vtText:'eofigzpieguh'}
	  * @param id
	  * @param page
	  * @return
	  */
	@POST
	@Path("/addPageToDocument/{doc_id}")
	@Consumes(Array(MediaType.APPLICATION_JSON))
	@Produces(Array(MediaType.APPLICATION_JSON))
	def addPageToDocument(@PathParam("doc_id") id: Long, page: String): Response = {
		try {
			val json = new JSONObject(page)

			val name = getFileName(json.getString("name"))
			// écriture vt
			val vt = PiFFReader.fromString(json.getString("vtText"))

			if (vt.isDefined) {
				val piff = vt.get

				if (piff.page.src != json.getString("name")) {
					return Response.notAcceptable(new util.ArrayList[Variant]()).entity("{\"error\":1}").build()
				}

				val pw = new PrintWriter(new File(globalDataFolder + "/" + name + ".piff"))
				pw.write(piff.toJSON.toString())
				pw.close()

				val page = Page(-1, name + ".piff", List())
				val res = controller.addPageToDocument(id, page)

				// écriture image
				val imgByte = javax.xml.bind.DatatypeConverter.parseBase64Binary(json.getString("image64"))

				val out = new FileOutputStream(globalDataFolder + "/" + json.getString("name"))
				out.write(imgByte)
				out.close()

				Response.status(200).entity(res.toJSON.toString).build()
			} else {
				Response.notAcceptable(new util.ArrayList[Variant]()).entity("{\"error\":0}").build()
			}
		}catch {
			case e: Exception => e.printStackTrace()
				Response.status(500).build()
		}
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
		try {
			controller.deleteDocument(id)
			Response.status(200).entity(true).build()
		}catch {
			case e: Exception => e.printStackTrace()
				Response.status(500).build()
		}
	}

	@DELETE
	@Path("/deletePage/{id}")
	def deletePage(@PathParam("id") id: Long): Response = {
		try {
			controller.deletePage(id)
			Response.status(200).build()
		}catch {
			case e: Exception => e.printStackTrace()
				Response.status(500).build()
		}
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

	/**
	  * Groups every examples in the base that are contained in the projects using the recogniser wich name is given in parameter. The examples must be usable and validated. The examples are then exported as a training set to the named recogniser.
	  * @param id The id of the Doc to export
	  * @return
	  */
	@POST
	@Path("/exportDocument/{id}")
	def exportDocument(@PathParam("id") id: Long): Response = {
		try {
			val rec = controller.getAllProject.find(proj => {
				val docs = controller.getDocumentOfProject(proj.id)
				docs.exists(doc => doc.id == id)
			}).get.recogniser
			controller.changeRecogniser(rec.toString)

			val examples = controller.getPagesOfDocuments(id)
				.flatMap(page => controller.getExamplesOfPage(page.id))
				.filter(example => example.validated&&example.enabled)
			println(examples)

			controller.exportExamples(examples)
			Response.status(200).entity(true).build()
		}catch  {
			case e: Exception => e.printStackTrace()
				Response.status(500).build()
		}
	}

	@POST
	@Path("/exportProject/{id}")
	def exportProject(@PathParam("id") id: Long): Response = {
		try {
			val rec = controller.getProject(id).get.recogniser
			controller.changeRecogniser(rec.toString)

			val examples = controller.getDocumentOfProject(id)
				.flatMap(doc => controller.getPagesOfDocuments(doc.id))
				.flatMap(page => controller.getExamplesOfPage(page.id))
				.filter(example => example.validated&&example.enabled)
			println("export",examples)

			controller.exportExamples(examples)
			Response.status(200).entity(true).build()
		}catch {
			case e: Exception => e.printStackTrace()
				Response.status(500).build()
		}
	}

	/*
	 * Annotation & Validation
	 */

	/**
	  * Returns the list of pages in the database that compose a document which id is given as a parameter.
	  * @param id The id of the document from which the pages are extracted
	  * @return The list of the pages of the document
	  */
	@GET
	@Path("/documentPages/{id}")
	@Produces(Array(MediaType.APPLICATION_JSON))
	def getPagesOfDocuments(@PathParam("id") id: Long): Response = {
		try {
			val json = new JSONArray()
			val pages = controller.getPagesOfDocuments(id)
			pages.foreach(it => json.put(it.toJSON))
			Response.status(200).entity(json.toString()).build()
		}catch {
			case e: Exception => e.printStackTrace()
				Response.status(500).build()
		}
	}

	/**
	  * Return the picture associated with the page which id is given as a parameter, along with the list of example (image and transcription) of the page
	  * @param id The id of the page in the database
	  * @return The picture of the selected page and the list of its examples
	  */
	@GET
	@Path("/pageData/{id}")
	@Produces(Array(MediaType.APPLICATION_JSON))
	def getPageData(@PathParam("id") id: Long): Response = {
		try{
			val jsonA = new JSONArray()
			println(id)
			val examples = controller.getExamplesOfPage(id)
			println(examples)
			val page = controller.getPage(id)
			examples.foreach(it => jsonA.put(it.toRequestJSON))
			println(jsonA.toString)
			Response.status(200).entity(jsonA.toString).build()
		}catch{
			case e: Exception=> e.printStackTrace()
				Response.status(500).build()
		}
	}

	/**
	  * Save in the database the modifications of the transcription describes by the JSON associated with the request
	  * The examples are send using the body. Thus, we collect it with examples as a string
	  * @param examples An array of tuple with id and transcript corresponding to the example modification
	  * @return
	  */
	@POST
	@Path("/saveExampleEdits")
	@Consumes(Array(MediaType.APPLICATION_JSON))
	def saveExamplesEdits(examples: String): Response = {
		try {
			val json = new JSONArray(examples)
			for (i <- 0 until json.length()) {
				try {
					val obj = json.getJSONObject(i)
					println(s"exemple $i: $obj")
					val example = controller.getExample(obj.getLong("id"))
						.copy(transcript = Some(obj.getString("transcript")))
					controller.modifyTranscription(example)
				} catch {
					case e: Exception => e.printStackTrace()
				}
			}
			Response.status(200).build()
		}catch {
			case e: Exception => e.printStackTrace()
				Response.status(500).build()
		}
	}

	/**
	  * Put the selected examples as Disabled
	  * @param id The id of the example to disable
	  * @return
	  */
	@PUT
	@Path("/disableExample/{id}")
	def disableExample(@PathParam("id") id: Long): Response = {
		try{
			controller.disableExample(id)
			println("c est bien disable")
			Response.status(200).entity(true).build()
		}catch {
			case e: Exception => e.printStackTrace()
			Response.status(500).build()
		}

	}

	/**
	  * Put the selected examples as Enable
	  * @param id The id of the example to enable
	  * @return
	  */
	@PUT
	@Path("/enableExample/{id}")
	def enableExample(@PathParam("id") id: Long): Response = {
		try{
			controller.enableExample(id)
			println("c est bien enable")
			Response.status(200).build()
		}catch {
			case e: Exception => e.printStackTrace()
				Response.status(500).build()
		}
	}

	/**
	  * Validate the examples given in the JSON associated with the request
	  * @param samples The array of example id
	  * @return
	  */
	@POST
	@Path("/validateExamples")
	@Consumes(Array(MediaType.APPLICATION_JSON))
	def validateExamples(samples: String): Response = {
		try {
			val array = new JSONArray(samples)
			val examples = new ListBuffer[Example]()
			for(i <- 0 until array.length()) {
				val id = array.getLong(i)
				examples += controller.getExample(id)
			}
			controller.validateTranscriptions(examples)

			Response.status(200).build()
		}catch {
			case e: Exception => e.printStackTrace()
				Response.status(500).build()
		}
	}

	/*
	 * Processing
	 */


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
	/**
	  * Prepare the examples from the document given in parameter
	  * @param id The id of the document to prepare
	  * @return
	  */
	@POST
	@Path("/prepareExamplesOfDocument/{doc_id}")
	def prepareExamplesOfDocument(@PathParam("doc_id") id: Long): Response = {
		// controller.prepareData(vtFiles)
		try {
			val pages = controller.getPagesOfDocuments(id)
			pages.foreach(it => controller.prepareData(it))
			controller.documentArePrepared(List(id))
			Response.status(200).build()
		}catch {
			case e: Exception => e.printStackTrace()
				Response.status(500).build()
		}
	}



}
