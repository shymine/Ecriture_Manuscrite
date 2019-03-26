package model

import model.common._
import model.database.DatabaseConnector
import model.preparation.ProcessingConnector
import model.recogniser.RecogniserConnector
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

	//TODO think of how it works
	def createProject(name: String, list: Iterable[String]): Project = {
		val documents = ArrayBuffer[Document]()
		list.foreach(doc => documents += Document(-1, doc, List(), false))
		val project = Project(-1, name, RecogniserType.None, documents)
		project
	}

	def deleteDocument(id: Long): Unit = {
		databaseConnector.connect
		databaseConnector.deleteDocument(id)
		databaseConnector.disconnect
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

	//TODO WTF is this
	/*
	def deleteTranscription(example: Example): Unit = {
		databaseConnector.connect



		databaseConnector.disconnect
	}
	*/

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

	/* Data Processing */

	def prepareData(vtFiles: Iterable[String]): Iterable[Example] = {
		processingConnector.prepareData(vtFiles)
	}

	def addGroundTruth(page: Page, groundTruth: JSONObject): Nothing = ???

	/* AI Interactions */

	def trainAI(samples: Iterable[Example]): Nothing = ???

	def evaluateAI(samples: Iterable[Example]): Any = ???

	def recognizeAI(samples: Iterable[Example]): Iterable[Example] = ???

	def changeRecogniser(name: String): Nothing = ???
}
