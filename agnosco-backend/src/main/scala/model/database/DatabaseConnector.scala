package model.database

import model.common.{Document, Example, Page, Project}


class DatabaseConnector {

	var impl: Database = new DatabaseSqlite

	def getProject(id: Int): Option[Project] = impl.getProject(id)
	def addProject(project: Project): Unit = impl.addProject(project)
	def deleteProject(id: Int): Unit = impl.deleteProject(id)

	def getDocument(id: Int): Option[Document] = impl.getDocument(id)
	def addDocument(document: Document, projectId: Long): Unit = impl.addDocument(document, projectId)
	def deleteDocument(id: Int): Unit = impl.deleteDocument(id)

	def getPage(id: Int): Option[Page] = impl.getPage(id)
	def addPage(page: Page, documentId: Long): Unit = impl.addPage(page, documentId)
	def deletePage(id: Int): Unit = impl.deletePage(id)

	def getExample(id: Int): Option[Example] = impl.getExample(id)
	def addExample(example: Example, pageId: Long): Unit = impl.addExample(example, pageId)
	def deleteExample(id: Int): Unit = impl.deleteExample(id)

	def getAllProject: Iterable[Project] = impl.getAllProject
	def getDocumentsOfProject(id: Int): Iterable[Document] = impl.getDocumentsOfProject(id)
	def getPagesOfDocument(id: Int): Iterable[Page] = impl.getPagesOfDocument(id)
	def getExamplesOfPage(id: Int): Iterable[Example] = impl.getExamplesOfPage(id)
	def saveExampleEdition(examples: Iterable[Example]): Unit = impl.saveExampleEdition(examples)

}
