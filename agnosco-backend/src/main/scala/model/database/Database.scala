package model.database

import model.common.{Document, Example, Page, Project}

/**
  * The Trait that needs to be implemented in order to interact between the Controller and the selected SGBD
  */
trait Database {
	/** Connects to the choosen database */
	def connect: Boolean
	/** Disconnects from the database*/
	def disconnect: Boolean
	/** Returns the project with the given id */
	def getProject(id: Long): Option[Project]
	/** Adds the given project in the database and returns the stored project corresponding with the right id */
	def addProject(project: Project): Project
	/** Removes the project with the given id from the database*/
	def deleteProject(id: Long)
	/** Returns the document with the given id */
	def getDocument(id: Long): Option[Document]
	/** Adds the given document in the database and returns the stored document corresponding with the right id */
	def addDocument(document: Document, projectId: Long): Document
	/** Removes the project with the given id from the database */
	def deleteDocument(id: Long)
	/** Returns the page with the given id */
	def getPage(id: Long): Option[Page]
	/** Adds the given page in the database and returns the stored project corresponding with the right id */
	def addPage(page: Page, documentId: Long): Page
	/** Removes the page with the given id from the database*/
	def deletePage(id: Long)
	/** Returns the example with the given id */
	def getExample(id: Long): Option[Example]
	/** Adds the given example in the database and returns the stored project corresponding with the right id */
	def addExample(example: Example, pageId: Long): Example
	/** Removes the example with the given id from the database*/
	def deleteExample(id: Long)
	/** Returns every project in the database */
	def getAllProject: Iterable[Project]
	/** Returns every documents owned by the project with the given id */
	def getDocumentsOfProject(id: Long): Iterable[Document]
	/** Returns every pages owned by the document with the given id */
	def getPagesOfDocument(id: Long): Iterable[Page]
	/** Returns every examples owned by the page with the given id */
	def getExamplesOfPage(id: Long): Iterable[Example]
	/** Saves in the database the modification of the given examples */
	def saveExampleEdition(examples: Iterable[Example])
	/** Set the documents which ids are given to prepared */
	def documentArePrepared(documents: Iterable[Long])
}
