package model.database
import model.common.{Document, Example, Page, Project}

class DatabaseSqlite extends Database {
	override def connect: Boolean = ???

	override def disconnect: Boolean = ???

	override def getProject(id: Int): Project = ???

	override def addProject(project: Project): Unit = ???

	override def deleteProject(id: Int): Unit = ???

	override def getDocument(id: Int): Document = ???

	override def addDocument(document: Document): Unit = ???

	override def deleteDocument(id: Int): Unit = ???

	override def getPage(id: Int): Page = ???

	override def addPage(page: Page): Unit = ???

	override def deletePage(id: Int): Unit = ???

	override def getExample(id: Int): Example = ???

	override def addExample(example: Example): Unit = ???

	override def deleteExample(id: Int): Unit = ???

	override def getAllProject(): Iterable[Project] = ???

	override def getDocumentsOfProject(id: Int): Iterable[Document] = ???

	override def getPagesOfDocument(id: Int): Iterable[Page] = ???

	override def getExamplesOfPage(id: Int): Iterable[Example] = ???

	override def saveExampleEdition(examples: Iterable[Example]): Unit = ???
}
