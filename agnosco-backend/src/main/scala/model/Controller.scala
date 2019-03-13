package model

import model.common.{Example, Page, Project, RecogniserType}
import model.database.DatabaseConnector
import model.preparation.ProcessingConnector
import model.recogniser.RecogniserConnector
import org.json.JSONObject

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

	def createProject(name: String, list: Iterable[String]): Nothing = ???

	def deleteDocument(id: Long) = ???

	def getAvailableRecognisers : Iterable[RecogniserType.Value] = ???


	def validateTranscriptions(samples: Iterable[Example]): Nothing = ???

	def modifyTranscription(example: Example): Nothing = ???

	def deleteTranscription(example: Example): Nothing = ???

	def disableExample(id: Int): Nothing = ???

	def enableExample(id: Int): Nothing = ???


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
