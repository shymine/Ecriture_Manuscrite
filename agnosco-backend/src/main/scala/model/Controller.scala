package model

import model.common.{Example, Page, Project, RecogniserType}
import org.json.JSONObject

class Controller {

	var currentRecogniser: RecogniserType.Value = RecogniserType.Laia
	var allProject: Iterable[Project] = List[Project]()


	def prepareData(imgFiles: Iterable[String], vtFiles: Iterable[String]): Iterable[Example] = ???

	def getProject(name: String): Project = ???

	def getAllProject: Iterable[Project] = ???

	def createProject(name: String, list: Iterable[String]): Nothing = ???

	def getAvailableRecognisers : Iterable[RecogniserType.Value] = ???

	def validateTranscriptions(samples: Iterable[Example]): Nothing = ???

	def modifyTranscription(example: Example): Nothing = ???

	def deleteTranscription(example: Example): Nothing = ???

	def disableExample(id: Int): Nothing = ???

	def enableExample(id: Int): Nothing = ???

	def addGroundTruth(page: Page, groundTruth: JSONObject): Nothing = ???

	def trainAI(samples: Iterable[Example]): Nothing = ???

	def evaluateAI(samples: Iterable[Example]): Any = ???

	def recognizeAI(samples: Iterable[Example]): Iterable[Example] = ???

	def changeRecogniser(name: String): Nothing = ???
}
