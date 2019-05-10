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
import scala.collection.mutable.{ArrayBuffer, ListBuffer}

/**
  * The API Rest of the program, receive the request from the front
  */
@Singleton
@Path("agnosco/base")
class AgnoscoResource {
	/** the link to the controller */
	val controller: Controller = new Controller

	/**
	  * Returns the list of the name of the projects with the list of the name of the documents for each project.
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

	/**
	  * Delete the project which id is given
	  * @param id The id of the project to delete
	  */
	@DELETE
	@Path("/deleteProject/{id}")
	def deleteProject(@PathParam("id") id: Long): Response = {
		try {
			controller.deleteProject(id)
			Response.status(200).build()
		}catch {
			case e: Exception => e.printStackTrace()
				Response.status(500).build()
		}
	}

	/**
	  * Creates a new project from its name and the recogniser format
	  * @param name The name of the project
	  * @param recogniser The type of the recogniser
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
	  * {
	  * 'name':'truc', -> le nom de l'image
	  * 'pages':[{name:'truc',image64:'ezrgrgz',vtText:'eofigzpieguh'},..]
	  * }
	  * @param id The id of the project
	  * @param document The json of the document to add in the request body
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
				// the break() go to the end of the breakable block
				breakable {
					val obj = arr.getJSONObject(i)
					val name = getFileName(obj.getString("name"))
					println(obj.getString("vtText"))
					val vt = PiFFReader.fromString(obj.getString("vtText"))
					if (vt.isDefined) {
						val piff = vt.get
						println(piff)
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
	/** extract the name of the file without the extension */
	def getFileName(str: String): String = {
		val regexp = "[.][a-zA-Z]+".r
		regexp.replaceAllIn(str,"")
	}

	/**
	  * Handle the page deletion and addition from the page gestio nin the frontend
	  * {
	  *     "deletedPages": [1,2,6],
	  *     "addedPages": [
	  *         { "name": "truc.png", "vtText":"zergbz", "image64":"image in base64 string"}
	  *     ]
	  * }
	  * @param doc_id The id of the document concerned by the modifications
	  * @param gestion The JSON containing the pages to delete and the pages to add
	  * @return
	  */
	@POST
	@Path("/pagesGestion/{doc_id}")
	@Consumes(Array(MediaType.APPLICATION_JSON))
	@Produces(Array(MediaType.APPLICATION_JSON))
	def pagesGestion(@PathParam("doc_id") doc_id: Long, gestion: String): Response = {
		try {
			val json = new JSONObject(gestion)
			val deletedPages = json.getJSONArray("deletedPages")
			val addedPages = json.getJSONArray("addedPages")
			println(deletedPages)
//			println("added",addedPages.getJSONObject(0))
			val pageList = new ListBuffer[Page]()
			var ok = true
			var correctVT = true

			for(i <- 0 until deletedPages.length()) {
				controller.deletePage(deletedPages.getLong(i))
			}
			for(i <- 0 until addedPages.length()) {
				breakable {
					val page = addedPages.getJSONObject(i)
					val name = getFileName(page.getString("name"))
					val vt = PiFFReader.fromString(page.getString("vtText"))
					if(vt.isDefined) {
						val piff = vt.get
						if(piff.page.src != page.getString("name")) {
							ok = false
							break()
						}
						val imgByte = javax.xml.bind.DatatypeConverter.parseBase64Binary(page.getString("image64"))
						val out = new FileOutputStream(globalDataFolder+"/"+page.getString("name"))
						out.write(imgByte)
						out.close()

						val pw = new PrintWriter(new File(globalDataFolder+"/"+name+".piff"))
						pw.write(piff.toJSON.toString())
						pw.close()

						pageList+= Page(-1, name+".piff", List())
					}else {
						correctVT = false
					}
				}
			}

			pageList.foreach(p => controller.addPageToDocument(doc_id, p))

			if(ok&&correctVT) {
				Response.status(200).build()
			}else if(ok&&(!correctVT)) {
				Response.notAcceptable(new util.ArrayList[Variant]()).entity("{'error':0}").build()
			}else if((!ok)&&correctVT){
				Response.notAcceptable(new util.ArrayList[Variant]()).entity("{'error':1}").build()
			}else {
				Response.notAcceptable(new util.ArrayList[Variant]()).entity("{'error':2}").build()
			}
		}catch {
			case e: Exception => e.printStackTrace()
				Response.status(500).build()
		}
	}

	/**
	  * Delete the document with the given id along with its dati
	  * @param id The id of the document to delete
	  * @return
	  */
	@DELETE
	@Path("/deleteDocument/{id}")
	def deleteDocument(@PathParam("id") id: Long): Response = {
		try {
			controller.deleteDocument(id)
			Response.status(200).build()
		}catch {
			case e: Exception => e.printStackTrace()
				Response.status(500).build()
		}
	}

	/**
	  * Delete the page with the given id along with its dati
	  * @param id The id of the document to delete
	  * @return
	  */
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
	  * Returns the list of the existing recogniser formats within the base
	  * @return Returns the list of name of the existing recogniser formats
	  */
	@GET
	@Path("/availableRecogniser")
	@Produces(Array(MediaType.APPLICATION_JSON))
	def getAvailableRecogniser: Response = {
		try {
			val recos = controller.getAvailableRecognisers
			val json = new JSONArray()
			recos.foreach(reco => json.put(reco))
			Response.status(200).entity(json.toString).build()
		}catch {
			case e: Exception => e.printStackTrace()
				Response.status(500).build()
		}
	}

	/**
	  * Exports the examples of the document with the given id in its project recogniser format
	  * The examples must be enabled and validated
	  * @param id The id of the document to export
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

	/**
	  * Export the examples of the project with the given id in its recogniser format
	  * The examples must be enabled and validated
	  * @param id The id of the project
	  * @return
	  */
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
			Response.status(200).build()
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
	  * Return the picture associated with the page which id is given as a parameter
	  * along with the list of example (image and transcription) of the page
	  * @param id The id of the page in the database
	  * @return The picture of the selected page and the list of its examples
	  */
	@GET
	@Path("/pageData/{id}")
	@Produces(Array(MediaType.APPLICATION_JSON))
	def getPageData(@PathParam("id") id: Long): Response = {
		try{
			val jsonA = new JSONArray()
			val examples = controller.getExamplesOfPage(id)
			val page = controller.getPage(id)
			examples.foreach(it => jsonA.put(it.toRequestJSON))
			val json = new JSONObject()
			json.put("examples", jsonA)
			val pjson = page.toJSON
			json.put("pageImage", pjson.getString("image64"))
			Response.status(200).entity(json.toString).build()
		}catch{
			case e: Exception=> e.printStackTrace()
				Response.status(500).build()
		}
	}

	/**
	  * Save in the database the modifications of the transcription describes by the JSON associated with the request
	  * The examples are send using the body. Thus, we collect it with examples as a string
	  * {
	  *     "str":"[{'id':3,'transcript':'coucou'}]"
	  * }
	  * @param examples An array of tuple with id and transcript corresponding to the example modification
	  * @return
	  */
	@POST
	@Path("/saveExampleEdits")
	@Consumes(Array(MediaType.APPLICATION_JSON))
	def saveExamplesEdits(examples: String): Response = {
		try {
			val obj = new JSONObject(examples)
			val json = new JSONArray(obj.getString("str"))
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
	  * Set the selected examples as disabled
	  * @param id The id of the example to disable
	  * @return
	  */
	@PUT
	@Path("/disableExample/{id}")
	def disableExample(@PathParam("id") id: Long): Response = {
		try{
			controller.disableExample(id)
			Response.status(200).build()
		}catch {
			case e: Exception => e.printStackTrace()
			Response.status(500).build()
		}

	}

	/**
	  * Set the selected examples as enable
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
	  * {
	  *     "valid":"[1,2,3,6]"
	  * }
	  * @param samples The array of example id
	  * @return
	  */
	@POST
	@Path("/validateExamples")
	@Consumes(Array(MediaType.APPLICATION_JSON))
	def validateExamples(samples: String): Response = {
		try {
			val obj = new JSONObject(samples)
			val array = new JSONArray(obj.getString("valid"))
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
