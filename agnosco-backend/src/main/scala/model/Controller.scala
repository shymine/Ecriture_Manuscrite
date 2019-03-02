package model

import model.common.{Example, Project, RecogniserType}

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

	def trainAI(samples: Iterable[Example]): Nothing = ???

	def evaluateAI(samples: Iterable[Example]): Any = ???

	def recognizeAI(samples: Iterable[Example]): Iterable[Example] = ???

	def changeRecogniser(name: String): Nothing = ???
}
