package model

import model.common._
import model.database.DatabaseConnector
import model.preparation.ProcessingConnector
import model.recogniser.RecogniserConnector
import org.json.JSONObject

import scala.collection.mutable.ArrayBuffer

class Controller {

	val databaseConnector = new DatabaseConnector
	val processingConnector = new ProcessingConnector
	val recogniserConnector = new RecogniserConnector

	/* Database */

	def getProject(id: Int): Project = {
		val project = databaseConnector.getProject(id)
		if(project.nonEmpty) {
			project.get
		}else {
			Project(-1, "", RecogniserType.None, List())
		}
	}

	def getAllProject: Iterable[Project] = databaseConnector.getAllProject


	/*
	 * Il faut faire attention lors qu'on connectera avec la base de données à gérer l'ID et la création des pages et tout
	 */
	def createProject(name: String, list: Iterable[String]): Project = {
		val documents = ArrayBuffer[Document]()
		list.foreach(doc => documents += Document(-1, doc, List(), false))
		val project = Project(-1, name, RecogniserType.None, documents)
		project
	}

	def deleteDocument(id: Long): Unit = databaseConnector.deleteDocument(id)

	def getAvailableRecognisers : Iterable[RecogniserType.Value] = RecogniserType.values


	def validateTranscriptions(samples: Iterable[Example]): Nothing = ???

	def modifyTranscription(example: Example): Nothing = ???

	def deleteTranscription(example: Example): Nothing = ???

	def disableExample(id: Long): Unit = {
		var example: Option[Example] = databaseConnector.getExample(id)
		if(example.nonEmpty) {
			example = Some (example.get.copy(enabled = false))
			databaseConnector.saveExampleEdition(List(example.get))
		}
	}

	def enableExample(id: Long): Unit = {
		var example = databaseConnector.getExample(id)
		if(example.nonEmpty) {
			example = Some(example.get.copy(enabled = true))
			databaseConnector.saveExampleEdition(List(example.get))
		}
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
