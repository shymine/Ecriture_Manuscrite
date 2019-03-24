package model.database

import model.common.{Document, Example, Page, Project}


class DatabaseConnector {

	var impl: Database = new DatabaseSqlite

	def connect: Boolean = impl.connect
	def disconnect: Boolean = impl.disconnect

	def getProject(id: Long): Option[Project] = impl.getProject(id)
	def addProject(project: Project): Unit = impl.addProject(project)
	def deleteProject(id: Long): Unit = impl.deleteProject(id)

	def getDocument(id: Long): Option[Document] = impl.getDocument(id)
	def addDocument(document: Document, projectId: Long): Unit = impl.addDocument(document, projectId)
	def deleteDocument(id: Long): Unit = impl.deleteDocument(id)

	def getPage(id: Long): Option[Page] = impl.getPage(id)
	def addPage(page: Page, documentId: Long): Unit = impl.addPage(page, documentId)
	def deletePage(id: Long): Unit = impl.deletePage(id)

	def getExample(id: Long): Option[Example] = impl.getExample(id)
	def addExample(example: Example, pageId: Long): Unit = impl.addExample(example, pageId)
	def deleteExample(id: Long): Unit = impl.deleteExample(id)

	def getAllProject: Iterable[Project] = impl.getAllProject
	def getDocumentsOfProject(id: Long): Iterable[Document] = impl.getDocumentsOfProject(id)
	def getPagesOfDocument(id: Long): Iterable[Page] = impl.getPagesOfDocument(id)
	def getExamplesOfPage(id: Long): Iterable[Example] = impl.getExamplesOfPage(id)
	def saveExampleEdition(examples: Iterable[Example]): Unit = impl.saveExampleEdition(examples)

}
