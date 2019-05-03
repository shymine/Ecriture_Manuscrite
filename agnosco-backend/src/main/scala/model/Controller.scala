package model

import model.common._
import model.database.DatabaseConnector
import model.preparation.ProcessingConnector
import model.recogniser.{Recogniser, RecogniserConnector}
import org.json.JSONObject

import scala.collection.mutable.ArrayBuffer

class Controller {

	private val databaseConnector = new DatabaseConnector
	private val processingConnector = new ProcessingConnector
	private val recogniserConnector = new RecogniserConnector

	/* Database */

	def getProject(id: Long): Option[Project] = {
		databaseConnector.connect
		val project = databaseConnector.getProject(id)
		databaseConnector.disconnect
		project
	}

	def getAllProject: Iterable[Project] = {
		databaseConnector.connect
		val projects = databaseConnector.getAllProject
		databaseConnector.disconnect
		projects
	}

	def createProject(name: String, recogniser: String): Project = {
		val recoType = RecogniserType.withName(recogniser)
		println(name)
		val project = Project(-1, name, recoType, List())
		println(project)
		databaseConnector.connect
		databaseConnector.addProject(project)
		val res = databaseConnector.getAllProject.find(it => it.name == name).get
		databaseConnector.disconnect
		res
	}

	def addDocToProject(project_id : Long, document: Document): Document = {
		databaseConnector.connect
		databaseConnector.addDocument(document, project_id)
		val res = databaseConnector.getDocumentsOfProject(project_id).find(it => it.name == document.name).get
		document.pages.foreach(it => databaseConnector.addPage(it, res.id))
		databaseConnector.disconnect
		res
	}

	def addPageToDocument(id: Long, page: Page): Page = {
		databaseConnector.connect
		databaseConnector.addPage(page, id)
		val res = databaseConnector.getPagesOfDocument(id).find(it => it.groundTruth==page.groundTruth).get
		databaseConnector.disconnect
		res
	}

	def addExampleToPage(id: Long, example: Example): Unit = {
		databaseConnector.connect
		databaseConnector.addExample(example, id)
		databaseConnector.disconnect
	}

	def deleteProject(id: Long): Unit = {
		databaseConnector.connect
		databaseConnector.deleteProject(id)
		databaseConnector.disconnect
	}

	def deleteDocument(id: Long): Unit = {
		databaseConnector.connect
		databaseConnector.deleteDocument(id)
		databaseConnector.disconnect
	}

	def deletePage(id: Long): Unit = {
		databaseConnector.connect
		databaseConnector.deletePage(id)
		databaseConnector.disconnect
	}

	def deleteExample(id: Long): Unit = {
		databaseConnector.connect
		databaseConnector.deleteExample(id)
		databaseConnector.disconnect
	}

	def getDocumentOfProject(id: Long): Iterable[Document] = {
		databaseConnector.connect
		val res = databaseConnector.getDocumentsOfProject(id)
		databaseConnector.disconnect
		res
	}

	def getDocument(id: Long): Document = {
		databaseConnector.connect
		val doc = databaseConnector.getDocument(id).get
		databaseConnector.disconnect
		doc
	}

	def getAvailableRecognisers : Iterable[RecogniserType.Value] = RecogniserType.values


	def validateTranscriptions(samples: Iterable[Example]): Unit = {
		val array = new ArrayBuffer[Example]()
		databaseConnector.connect
		samples.foreach(it => {
			val sample = databaseConnector.getExample(it.id)
			if(sample.nonEmpty) {
				val newSample = sample.get.copy(validated = true)
				array += newSample
			}
		})
		databaseConnector.saveExampleEdition(array)
		databaseConnector.disconnect
	}

	def modifyTranscription(example: Example): Unit = {
		databaseConnector.connect
		val sample = databaseConnector.getExample(example.id)
		if(sample.nonEmpty) {
			val newSample = sample.get.copy(transcript = example.transcript)
			databaseConnector.saveExampleEdition(List(newSample))
		}
		databaseConnector.disconnect
	}

	def disableExample(id: Long): Unit = {
		databaseConnector.connect
		val example = databaseConnector.getExample(id)
		if(example.nonEmpty) {
			val newExample = example.get.copy(enabled = false)
		}
		databaseConnector.disconnect
	}

	def enableExample(id: Long): Unit = {
		databaseConnector.connect
		val example = databaseConnector.getExample(id)
		if(example.nonEmpty) {
			val newExample = example.get.copy(enabled = true)
		}
		databaseConnector.disconnect
	}

	def getPagesOfDocuments(id: Long): Iterable[Page] = {
		databaseConnector.connect
		val pages = databaseConnector.getPagesOfDocument(id)
		databaseConnector.disconnect
		pages
	}

	def getPage(id: Long): Page = {
		databaseConnector.connect
		val page = databaseConnector.getPage(id).get
		databaseConnector.disconnect
		page
	}

	def getExamplesOfPage(id: Long): Iterable[Example] = {
		databaseConnector.connect
		val examples = databaseConnector.getExamplesOfPage(id)
		databaseConnector.disconnect
		examples
	}

	/* Data Processing */

	def prepareData(page: Page): Iterable[Example] = {
		processingConnector.prepareData(page)
	}

	/* AI Interactions */

	def trainAI(samples: Iterable[Example]): Unit = recogniserConnector.trainAI(samples)

	def evaluateAI(samples: Iterable[Example]): JSONObject = recogniserConnector.evaluateAI(samples)

	def recognizeAI(samples: Iterable[Example]): Iterable[Example] = recogniserConnector.recognizeAI(samples)

	def changeRecogniser(name: String): Unit = {
		val classe = Class.forName(name)
		if(classe.isAssignableFrom(classOf[Recogniser])) {
			recogniserConnector.changeRecogniser(name)
		}
	}
}
