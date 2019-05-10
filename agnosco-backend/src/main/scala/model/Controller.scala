package model

import java.io.File

import model.common._
import model.database.DatabaseConnector
import model.preparation.PreparatorConnector
import model.preparation.input.PiFFReader
import model.recogniser.{Recogniser, RecogniserConnector}
import org.json.JSONObject

import scala.collection.mutable.ArrayBuffer

/**
  * Manages the calls to the differents blocks of the project
  */
class Controller {

	// The differents Connectors of the blocks
	private val databaseConnector = new DatabaseConnector
	private val processingConnector = new PreparatorConnector
	private val recogniserConnector = new RecogniserConnector

	/* Database */
	/**
	  * Returns the project corresponding to the given id
	  * @param id The id of the project to retrieve
	  * @return The project stored in the database
	  * @see DatabaseConnector#getProject
	  */
	def getProject(id: Long): Option[Project] = {
		databaseConnector.connect
		val project = databaseConnector.getProject(id)
		databaseConnector.disconnect
		project
	}
	/**
	  * Returns every projects in the database
	  * @return The list of the projects stored in the database
	  * @see DatabaseConnector#getAllProject
	  */
	def getAllProject: Iterable[Project] = {
		databaseConnector.connect
		val projects = databaseConnector.getAllProject
		databaseConnector.disconnect
		projects
	}
	/**
	  * Create a new project in the database
	  * @param name The name of the project to create
	  * @param recogniser The name of the recogniser format
	  * @return The project with the correct database id
	  * @see DatabaseConnector#addProject
	  */
	def createProject(name: String, recogniser: String): Project = {
		val recoType = RecogniserType.withName(recogniser)
		val project = Project(-1, name, recoType, List())
		databaseConnector.connect
		databaseConnector.addProject(project)
		val res = databaseConnector.getAllProject.find(it => it.name == name).get
		databaseConnector.disconnect
		res
	}
	/**
	  * Add a document to a project
	  * @param project_id The id of the project associated to the document
	  * @param document The document to add
	  * @return Return the Document with the correct database id
	  * @see DatabaseConnector#addDocument
	  */
	def addDocToProject(project_id : Long, document: Document): Document = {
		databaseConnector.connect
		databaseConnector.addDocument(document, project_id)
		val res = databaseConnector.getDocumentsOfProject(project_id).find(it => it.name == document.name).get
		document.pages.foreach(it => databaseConnector.addPage(it, res.id))
		databaseConnector.disconnect
		res
	}
	/**
	  * Add a page to a document
	  * @param id The id of the document associated to the page
	  * @param page The page to add
	  * @return Returns the page with the correct database id
	  * @see DatabaseConnector#addPage
	  */
	def addPageToDocument(id: Long, page: Page): Page = {
		databaseConnector.connect
		databaseConnector.addPage(page, id)
		val res = databaseConnector.getPagesOfDocument(id).find(it => it.groundTruth==page.groundTruth).get
		databaseConnector.disconnect
		res
	}
	/**
	  * Add an example to a page but doesn t return the stored example
	  * @param id The id of the page associated to the example
	  * @param example the example to add
	  * @see DatabaseConnector#addExample
	  */
	def addExampleToPage(id: Long, example: Example): Unit = {
		databaseConnector.connect
		databaseConnector.addExample(example, id)
		databaseConnector.disconnect
	}
	/**
	  * Delete the project with the given id and the structure under it (documents, pages, examples)
	  * @param id The id of the project to delete
	  */
	def deleteProject(id: Long): Unit = {
		databaseConnector.connect
		val docs = databaseConnector.getDocumentsOfProject(id)
		val pages = docs.flatMap(doc => databaseConnector.getPagesOfDocument(doc.id))
		val examples = pages.flatMap(page => databaseConnector.getExamplesOfPage(page.id))
		databaseConnector.deleteProject(id)
		docs.foreach(doc => databaseConnector.deleteDocument(doc.id))
		pages.foreach(page => {
			databaseConnector.deletePage(id)
			val piff = PiFFReader.fromFile(globalDataFolder+"/"+page.groundTruth).get
			new File(globalDataFolder+"/"+piff.page.src).delete()
			new File(globalDataFolder+"/"+page.groundTruth).delete()
		})
		examples.foreach(example => {
			databaseConnector.deleteExample(example.id)
			new File(globalDataFolder+"/"+example.imagePath).delete()
		})
		databaseConnector.disconnect
	}
	/**
	  * Delete the document with the given id and the structure under it (pages, examples)
	  * @param id The id of the document to delete
	  */
	def deleteDocument(id: Long): Unit = {
		databaseConnector.connect
		val pages = databaseConnector.getPagesOfDocument(id)
		val examples = pages.flatMap(page => databaseConnector.getExamplesOfPage(page.id))
		databaseConnector.deleteDocument(id)
		pages.foreach(page => {
			databaseConnector.deletePage(page.id)
			val piff = PiFFReader.fromFile(globalDataFolder+"/"+page.groundTruth).get
			new File(globalDataFolder+"/"+piff.page.src).delete()
			new File(globalDataFolder+"/"+page.groundTruth).delete()
		})
		examples.foreach(example => {
			databaseConnector.deleteExample(example.id)
			new File(globalDataFolder+"/"+example.imagePath).delete()
		})
		databaseConnector.disconnect
	}
	/**
	  * Delete the page with the given id and the structure under it (examples)
	  * @param id The id of the page to delete
	  */
	def deletePage(id: Long): Unit = {
		databaseConnector.connect
		val page = databaseConnector.getPage(id).get
		val examples = databaseConnector.getExamplesOfPage(id)
		databaseConnector.deletePage(id)
		val piff = PiFFReader.fromFile(globalDataFolder+"/"+page.groundTruth).get
		new File(globalDataFolder+"/"+piff.page.src).delete()
		new File(globalDataFolder+"/"+page.groundTruth).delete()
		examples.foreach(example => {
			databaseConnector.deleteExample(example.id)
			new File(globalDataFolder+"/"+example.imagePath).delete()
		})
		databaseConnector.disconnect
	}
	/**
	  * Delete the example with the given id
	  * @param id the id of the example to delete
	  */
	def deleteExample(id: Long): Unit = {
		databaseConnector.connect
		val example = databaseConnector.getExample(id).get
		databaseConnector.deleteExample(id)
		new File(globalDataFolder+"/"+example.imagePath).delete()
		databaseConnector.disconnect
	}
	/**
	  * Retrieve the example with the given id
	  * @param id The id of the example to retrieve
	  */
	def getExample(id: Long): Example = {
		databaseConnector.connect
		val ex = databaseConnector.getExample(id).get
		databaseConnector.disconnect
		ex
	}
	/**
	  * Retrieve every document of a project
	  * @param id The id of the project
	  * @return Returns the documents of the project with the given id
	  * @see DatabaseConnector#getDocumentsOfProject
	  */
	def getDocumentOfProject(id: Long): Iterable[Document] = {
		databaseConnector.connect
		val res = databaseConnector.getDocumentsOfProject(id)
		databaseConnector.disconnect
		res
	}

	/**
	  * Retrieves the document with the given id
	  * @param id The id of the document to retrieves
	  * @return The document that correspond to the id
	  * @see DatabaseConnector#getDocument
	  */
	def getDocument(id: Long): Document = {
		databaseConnector.connect
		val doc = databaseConnector.getDocument(id).get
		databaseConnector.disconnect
		doc
	}

	/**
	  * Return the list of the available format for the recogniser export
	  * @return Returns the list of the available format (class names) for the recogniser export
	  */
	def getAvailableRecognisers : Iterable[RecogniserType.Value] = RecogniserType.values

	/**
	  * Validate the examples given as a parameter
	  * @param samples The list of the examples to set as validated
	  */
	def validateTranscriptions(samples: Iterable[Example]): Unit = {
		val array = new ArrayBuffer[Example]()
		databaseConnector.connect
		samples.foreach(it => {
			val newSample = it.copy(validated = true)
			array += newSample
		})
		databaseConnector.saveExampleEdition(array)
		databaseConnector.disconnect
	}

	/**
	  * Modify the transcription of the example stored in the database by the new transcription it is given
	  * using its id
	  * @param example The example we want to edit the transcription
	  */
	def modifyTranscription(example: Example): Unit = {
		databaseConnector.connect
		val sample = databaseConnector.getExample(example.id)
		if(sample.nonEmpty) {
			val newSample = sample.get.copy(transcript = example.transcript)
			databaseConnector.saveExampleEdition(List(newSample))
		}
		databaseConnector.disconnect
	}

	/**
	  * Set the example linked to the given id to disable
	  * @param id The id of the example to disable
	  * @see Example
	  */
	def disableExample(id: Long): Unit = {
		databaseConnector.connect
		val example = databaseConnector.getExample(id)
		if(example.nonEmpty) {
			val newExample = example.get.copy(enabled = false)
			databaseConnector.saveExampleEdition(List(newExample))
		}
		databaseConnector.disconnect
	}

	/**
	  * Set the example linked to the given id to enable (default state)
	  * @param id the id of the example to enable
	  * @see Example
	  * @see #disableExample
	  */
	def enableExample(id: Long): Unit = {
		databaseConnector.connect
		val example = databaseConnector.getExample(id)
		if(example.nonEmpty) {
			val newExample = example.get.copy(enabled = true)
			databaseConnector.saveExampleEdition(List(newExample))
		}
		databaseConnector.disconnect
	}

	/**
	  * Retrieves the pages of the document which id is given
	  * @param id the id of the document
	  * @return Returns the pages owned by the document
	  * @see DatabaseConnector#getPagesOfDocument
	  */
	def getPagesOfDocuments(id: Long): Iterable[Page] = {
		databaseConnector.connect
		val pages = databaseConnector.getPagesOfDocument(id)
		databaseConnector.disconnect
		pages
	}

	/**
	  * Retrieve the page with the given id
	  * @param id The id of the page to retrieve
	  * @return The page associated to the id
	  * @see DatabaseConnector#getPage
	  */
	def getPage(id: Long): Page = {
		databaseConnector.connect
		val page = databaseConnector.getPage(id).get
		databaseConnector.disconnect
		page
	}

	/**
	 * Retrieve the examples owned by the page which id is given as a parameter
	  * @param id The id of the page
	  * @return The examples owned by the page
	  * @see DatabaseConnector#getExamplesOfPage
	  */
	def getExamplesOfPage(id: Long): Iterable[Example] = {
		databaseConnector.connect
		val examples = databaseConnector.getExamplesOfPage(id)
		databaseConnector.disconnect
		examples
	}

	/* Data Processing */

	/**
	  * Prepare the page given in parameter by cutting the examples in the image using the groundtruth
	  * @param page The page to prepare
	  * @return The examples generated by the preparation
	  * @see PreparatorConnector#prepareData
	  */
	def prepareData(page: Page): Iterable[Example] = {
		val examples: Iterable[Example] = processingConnector.prepareData(page)
		databaseConnector.connect
		examples.foreach(it => databaseConnector.addExample(it, page.id))
		databaseConnector.disconnect
		examples
	}

	/**
	  * Set the documents which ids are given as a parameter to prepared (when their pages have beed cut in examples)
	  * @param ids The list of id of the prepared documents
	  * @see DatabaseConnector#documentArePrepared
	  */
	def documentArePrepared(ids: Iterable[Long]): Unit = {
		databaseConnector.connect
		databaseConnector.documentArePrepared(ids)
		databaseConnector.disconnect
	}


	/* AI Interactions */

	/**
	  * Export the examples given the current recogniser format
	  * @param samples The examples to export
	  * @see RecogniserConnector#export
	  */
	def exportExamples(samples: Iterable[Example]): Unit = recogniserConnector.export(samples)
	/**
	  * Change the current recogniser format to the named format
	  * @param name The name of the class to export the example with
	  * @see RecogniserConnector#changeRecogniser
	  */
	def changeRecogniser(name: String): Unit = recogniserConnector.changeRecogniser(name)
}
