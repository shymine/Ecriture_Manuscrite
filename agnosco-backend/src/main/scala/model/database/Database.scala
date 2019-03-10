package model.database

import model.common.{Document, Example, Page, Project}

trait Database {
	def connect: Boolean
	def disconnect: Boolean

	def getProject(id: Int): Option[Project]
	def addProject(project: Project)
	def deleteProject(id: Int)

	def getDocument(id: Int): Option[Document]
	def addDocument(document: Document, projectId: Long)
	def deleteDocument(id: Int)

	def getPage(id: Int): Option[Page]
	def addPage(page: Page, documentId: Long)
	def deletePage(id: Int)

	def getExample(id: Int): Option[Example]
	def addExample(example: Example, pageId: Long)
	def deleteExample(id: Int)

	def getAllProject: Iterable[Project]
	def getDocumentsOfProject(id: Int): Iterable[Document]
	def getPagesOfDocument(id: Int): Iterable[Page]
	def getExamplesOfPage(id: Int): Iterable[Example]
	def saveExampleEdition(examples: Iterable[Example])
}
