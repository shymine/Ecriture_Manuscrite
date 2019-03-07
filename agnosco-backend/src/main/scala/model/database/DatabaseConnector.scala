package model.database

import model.common.{Document, Example, Page, Project}


class DatabaseConnector {

	var impl: Database = new DatabaseSqlite

	def getProject(id: Int): Option[Project] = impl.getProject(id)
	def addProject(project: Project) = impl.addProject(project)
	def deleteProject(id: Int) = impl.deleteProject(id)

	def getDocument(id: Int): Option[Document] = impl.getDocument(id)
	def addDocument(document: Document) = impl.addDocument(document)
	def deleteDocument(id: Int) = impl.deleteDocument(id)

	def getPage(id: Int): Option[Page] = impl.getPage(id)
	def addPage(page: Page) = impl.addPage(page)
	def deletePage(id: Int) = impl.deletePage(id)

	def getExample(id: Int): Option[Example] = impl.getExample(id)
	def addExample(example: Example) = impl.addExample(example)
	def deleteExample(id: Int) = impl.deleteExample(id)

	def getAllProject(): Iterable[Project] = impl.getAllProject()
	def getDocumentsOfProject(id: Int): Iterable[Document] = impl.getDocumentsOfProject(id)
	def getPagesOfDocument(id: Int): Iterable[Page] = impl.getPagesOfDocument(id)
	def getExamplesOfPage(id: Int): Iterable[Example] = impl.getExamplesOfPage(id)
	def saveExampleEdition(examples: Iterable[Example]) = impl.saveExampleEdition(examples)

}
