package model.database

import model.common.{Document, Example, Page, Project}

trait Database {
	def connect: Boolean
	def disconnect: Boolean

	def getProject(id: Long): Option[Project]
	def addProject(project: Project): Project
	def deleteProject(id: Long)

	def getDocument(id: Long): Option[Document]
	def addDocument(document: Document, projectId: Long): Document
	def deleteDocument(id: Long)

	def getPage(id: Long): Option[Page]
	def addPage(page: Page, documentId: Long): Page
	def deletePage(id: Long)

	def getExample(id: Long): Option[Example]
	def addExample(example: Example, pageId: Long): Example
	def deleteExample(id: Long)

	def getAllProject: Iterable[Project]
	def getDocumentsOfProject(id: Long): Iterable[Document]
	def getPagesOfDocument(id: Long): Iterable[Page]
	def getExamplesOfPage(id: Long): Iterable[Example]
	def saveExampleEdition(examples: Iterable[Example])

	def documentArePrepared(documents: Iterable[Long])
}
