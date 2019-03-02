package model.database

import model.common.{Document, Example, Page, Project}

trait Database {
	def connect: Boolean
	def disconnect: Boolean

	def getProject(id: Int): Project
	def addProject(project: Project)
	def deleteProject(id: Int)

	def getDocument(id: Int): Document
	def addDocument(document: Document)
	def deleteDocument(id: Int)

	def getPage(id: Int): Page
	def addPage(page: Page)
	def deletePage(id: Int)

	def getExample(id: Int): Example
	def addExample(example: Example)
	def deleteExample(id: Int)

	def getAllProject(): Iterable[Project]
	def getDocumentsOfProject(id: Int): Iterable[Document]
	def getPagesOfDocument(id: Int): Iterable[Page]
	def getExamplesOfPage(id: Int): Iterable[Example]
	def saveExampleEdition(examples: Iterable[Example])
}
