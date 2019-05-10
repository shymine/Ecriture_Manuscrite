package model.database

import model.common.{Document, Example, Page, Project}

/**
  * Connects the controller to the selected implementation of the database
  */
class DatabaseConnector {
	/** The chosen implementation of the Database Trait */
	private val impl: Database = new DatabaseSqlite()
	/** @see Database#connect */
	def connect: Boolean = impl.connect
	/** @see Database#disconnect */
	def disconnect: Boolean = impl.disconnect
	/** @see Database#getProject */
	def getProject(id: Long): Option[Project] = impl.getProject(id)
	/** @see Database#addProject */
	def addProject(project: Project): Project = impl.addProject(project)
	/** @see Database#deleteProject */
	def deleteProject(id: Long): Unit = impl.deleteProject(id)
	/** @see Database#getDocument */
	def getDocument(id: Long): Option[Document] = impl.getDocument(id)
	/** @see Database#addDocument */
	def addDocument(document: Document, projectId: Long): Document = impl.addDocument(document, projectId)
	/** @see Database#deleteDocument */
	def deleteDocument(id: Long): Unit = impl.deleteDocument(id)
	/** @see Database#getPage */
	def getPage(id: Long): Option[Page] = impl.getPage(id)
	/** @see Database#addPage */
	def addPage(page: Page, documentId: Long): Page = impl.addPage(page, documentId)
	/** @see Database#deletePage */
	def deletePage(id: Long): Unit = impl.deletePage(id)
	/** @see Database#getExample */
	def getExample(id: Long): Option[Example] = impl.getExample(id)
	/** @see Database#addExample */
	def addExample(example: Example, pageId: Long): Example = impl.addExample(example, pageId)
	/** @see Database#deleteExample */
	def deleteExample(id: Long): Unit = impl.deleteExample(id)
	/** @see Database#getAllProject */
	def getAllProject: Iterable[Project] = impl.getAllProject
	/** @see Database#getDocumentsOfProject */
	def getDocumentsOfProject(id: Long): Iterable[Document] = impl.getDocumentsOfProject(id)
	/** @see Database#getPagesOfDocument */
	def getPagesOfDocument(id: Long): Iterable[Page] = impl.getPagesOfDocument(id)
	/** @see Database#getExamplesOfPage */
	def getExamplesOfPage(id: Long): Iterable[Example] = impl.getExamplesOfPage(id)
	/** @see Database#saveExampleEdition */
	def saveExampleEdition(examples: Iterable[Example]): Unit = impl.saveExampleEdition(examples)
	/** @see Database#documentArePrepared */
	def documentArePrepared(ids: Iterable[Long]): Unit = impl.documentArePrepared(ids)
}
