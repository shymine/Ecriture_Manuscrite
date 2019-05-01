package model

import model.common._
import model.database.{DatabaseConnector, DatabaseSqlite}
import org.scalatest.FlatSpec

import scala.collection.mutable.ArrayBuffer


// When testing, please comment the database.commit line in disconnect
class DatabaseSqliteTest extends FlatSpec {
	/*
	"Project" should "be gettable" in {
		val database = new DatabaseSqlite()
		database.connect
		val project = Project(3, "super projet de la mort", RecogniserType.None, List())
		database.addProject(project)
		val dataProject = database.getAllProject
		assert(dataProject.exists(p => p.name == project.name))
		database.disconnect
	}

	"Project" should "be addable" in {
		val database = new DatabaseSqlite()
		database.connect
		val project = Project(3, "super projet de la mort", RecogniserType.None, List())
		database.addProject(project)
		val projects = database.getAllProject
		assert(projects.exists(it => it.name == "super projet de la mort"))
		database.disconnect
	}


	"Project" should "be able to be all gathered" in {
		val database = new DatabaseSqlite()
		database.connect
		val projects1 = database.getAllProject
		val project = Project(3, "super projet de la mort", RecogniserType.None, List())
		database.addProject(project)
		val projects2 = database.getAllProject
		database.disconnect
	}

	"Project" should "be deletable" in {
		val database = new DatabaseSqlite()
		database.connect
		val project = Project(3, "super projet de la mort", RecogniserType.None, List())
		database.addProject(project)
		val projects = database.getAllProject.find(it => it.name == project.name).get
		database.deleteProject(projects.id)
		val afterDelet = database.getAllProject
		assert(afterDelet.isEmpty)
		database.disconnect
	}

	"Document" should "be gettable" in {
		val database = new DatabaseSqlite()
		database.connect
		val project = Project(-1, "super projet de la mort", RecogniserType.None, List())
		database.addProject(project)
		val document = Document(-1, "docuperbe", List(), false)
		val dataProject = database.getAllProject.find(it => it.name == project.name)
		database.addDocument(document, dataProject.get.id)
		val docu = database.getDocumentsOfProject(dataProject.get.id)
		val dataDocu = database.getDocument(docu.find(it => it.name == document.name).get.id)
		database.disconnect
		assert(dataDocu.get.name == document.name)
	}

	"Document" should "be addable" in {
		val database = new DatabaseSqlite()
		database.connect
		val project = Project(-1, "super projet de la mort", RecogniserType.None, List())
		database.addProject(project)
		val document = Document(-1, "docu de la mort", List(), false)
		val dataProject = database.getAllProject.find(it => it.name == project.name)
		database.addDocument(document, dataProject.get.id)
		val dataDocument = database.getDocumentsOfProject(dataProject.get.id)
		database.disconnect
		assert(dataDocument.exists(it => it.name==document.name))
	}

	"Document" should "be all gathered" in {
		val database = new DatabaseSqlite()
		database.connect
		val project = Project(-1, "super projet de la mort", RecogniserType.None, List())
		val document = Document(-1, "docu de la mort", List(), false)


		database.addProject(project)
		val dataProject = database.getAllProject.find(it => it.name == project.name).get
		database.addDocument(document, dataProject.id)
		val dataDocumentSet = database.getDocumentsOfProject(dataProject.id)

		database.disconnect

		assert(dataDocumentSet.nonEmpty)

	}

	"Document" should "be deletable" in {
		val database = new DatabaseSqlite()
		database.connect
		val project = Project(-1, "super projet de la mort", RecogniserType.None, List())
		database.addProject(project)
		val document = Document(-1, "docu de la mort", List(), false)
		val dataProject = database.getAllProject.find(it => it.name == project.name)
		database.addDocument(document, dataProject.get.id)
		val dataDocument = database.getDocumentsOfProject(dataProject.get.id)
		database.deleteDocument(dataDocument.find(it => it.name == document.name).get.id)
		assert(database.getDocumentsOfProject(dataProject.get.id).isEmpty)
		database.disconnect
	}

	"Document" should "be prepared" in {
		val database = new DatabaseSqlite()
		database.connect
		val project = Project(-1, "super projet de la mort", RecogniserType.None, List())
		val document = Document(-1, "docu de la mort", List(), false)

		database.addProject(project)
		val dataProject = database.getAllProject.find(it => it.name == project.name).get
		database.addDocument(document, dataProject.id)
		val dataDocument = database.getDocumentsOfProject(dataProject.id).find(it => it.name == document.name).get

		database.documentArePrepared(List(dataDocument.id))

		val updatedDocument = database.getDocumentsOfProject(dataProject.id).find(it => it.name == document.name).get

		database.disconnect

		assert(updatedDocument.prepared)
	}

	"Page" should "be gettable" in {
		val database = new DatabaseSqlite()
		database.connect
		val project = Project(-1, "super projet de la mort", RecogniserType.None, List())
		val document = Document(-1, "docu de la mort", List(), false)
		val page = Page(-1, "assets/img", "assets/gt", List())

		database.addProject(project)
		val dataProject = database.getAllProject.find(it => it.name == project.name).get
		database.addDocument(document, dataProject.id)
		val dataDocument = database.getDocumentsOfProject(dataProject.id).find(it => it.name == document.name).get
		database.addPage(page, dataDocument.id)

		val retrieveDocument = database.getDocument(dataDocument.id)

		database.disconnect
		assert(retrieveDocument.get.name == document.name)
	}

	"Page" should "be addable" in {
		val database = new DatabaseSqlite()
		database.connect
		val project = Project(-1, "super projet de la mort", RecogniserType.None, List())
		val document = Document(-1, "docu de la mort", List(), false)
		val page = Page(-1, "assets/img", "assets/gt", List())

		database.addProject(project)
		val dataProject = database.getAllProject.find(it => it.name == project.name).get
		database.addDocument(document, dataProject.id)
		val dataDocument = database.getDocumentsOfProject(dataProject.id).find(it => it.name == document.name).get
		database.addPage(page, dataDocument.id)
		val res = database.getPagesOfDocument(dataDocument.id).exists(it => it.imagePath == page.imagePath)
		database.disconnect
		assert(res)
	}

	"Page" should "be all gathered" in {
		val database = new DatabaseSqlite()
		database.connect
		val project = Project(-1, "super projet de la mort", RecogniserType.None, List())
		val document = Document(-1, "docu de la mort", List(), false)
		val page = Page(-1, "assets/img", "assets/gt", List())

		database.addProject(project)
		val dataProject = database.getAllProject.find(it => it.name == project.name).get
		database.addDocument(document, dataProject.id)
		val dataDocument = database.getDocumentsOfProject(dataProject.id).find(it => it.name == document.name).get
		database.addPage(page, dataDocument.id)
		val dataPageSet = database.getPagesOfDocument(dataDocument.id)

		database.disconnect

		assert(dataPageSet.nonEmpty)
	}

	"Page" should "be deletable" in {
		val database = new DatabaseSqlite()
		database.connect
		val project = Project(-1, "super projet de la mort", RecogniserType.None, List())
		val document = Document(-1, "docu de la mort", List(), false)
		val page = Page(-1, "assets/img", "assets/gt", List())

		database.addProject(project)
		val dataProject = database.getAllProject.find(it => it.name == project.name).get
		database.addDocument(document, dataProject.id)
		val dataDocument = database.getDocumentsOfProject(dataProject.id).find(it => it.name == document.name).get
		database.addPage(page, dataDocument.id)
		val dataPage = database.getPagesOfDocument(dataDocument.id).find(it => it.imagePath == page.imagePath).get
		database.deletePage(dataPage.id)

		val resultSet = database.getPagesOfDocument(dataDocument.id)
		database.disconnect

		assert(resultSet.isEmpty)
	}

	"Example" should "be gettable" in {
		val database = new DatabaseSqlite()
		database.connect
		val project = Project(-1, "super projet de la mort", RecogniserType.None, List())
		val document = Document(-1, "docu de la mort", List(), false)
		val page = Page(-1, "assets/img", "assets/gt", List())
		val example = Example(-1, "assets/example1", None, false, false)

		database.addProject(project)
		val dataProject = database.getAllProject.find(it => it.name == project.name).get
		database.addDocument(document, dataProject.id)
		val dataDocument = database.getDocumentsOfProject(dataProject.id).find(it => it.name == document.name).get
		database.addPage(page, dataDocument.id)
		val dataPage = database.getPagesOfDocument(dataDocument.id).find(it => it.imagePath == page.imagePath).get
		database.addExample(example, dataPage.id)
		val dataExample = database.getExamplesOfPage(dataPage.id).find(it => it.imagePath == example.imagePath).get

		val res = database.getExample(dataExample.id).nonEmpty
		database.disconnect

		assert(res)
	}

	"Example" should "be addable" in {
		val database = new DatabaseSqlite()
		database.connect
		val project = Project(-1, "super projet de la mort", RecogniserType.None, List())
		val document = Document(-1, "docu de la mort", List(), false)
		val page = Page(-1, "assets/img", "assets/gt", List())
		val example = Example(-1, "assets/example1", None, false, false)

		database.addProject(project)
		val dataProject = database.getAllProject.find(it => it.name == project.name).get
		database.addDocument(document, dataProject.id)
		val dataDocument = database.getDocumentsOfProject(dataProject.id).find(it => it.name == document.name).get
		database.addPage(page, dataDocument.id)
		val dataPage = database.getPagesOfDocument(dataDocument.id).find(it => it.imagePath == page.imagePath).get
		database.addExample(example, dataPage.id)
		val res = database.getExamplesOfPage(dataPage.id).exists(it => it.imagePath == example.imagePath)

		database.disconnect

		assert(res)
	}

	"Example" should "be all gathered" in {
		val database = new DatabaseSqlite()
		database.connect
		val project = Project(-1, "super projet de la mort", RecogniserType.None, List())
		val document = Document(-1, "docu de la mort", List(), false)
		val page = Page(-1, "assets/img", "assets/gt", List())
		val example = Example(-1, "assets/example1", None, false, false)

		database.addProject(project)
		val dataProject = database.getAllProject.find(it => it.name == project.name).get
		database.addDocument(document, dataProject.id)
		val dataDocument = database.getDocumentsOfProject(dataProject.id).find(it => it.name == document.name).get
		database.addPage(page, dataDocument.id)
		val dataPage = database.getPagesOfDocument(dataDocument.id).find(it => it.imagePath == page.imagePath).get
		database.addExample(example, dataPage.id)
		val dataExampleSet = database.getExamplesOfPage(dataPage.id)

		database.disconnect

		assert(dataExampleSet.nonEmpty)
	}

	"Example" should "be deletable" in {
		val database = new DatabaseSqlite()
		database.connect
		val project = Project(-1, "super projet de la mort", RecogniserType.None, List())
		val document = Document(-1, "docu de la mort", List(), false)
		val page = Page(-1, "assets/img", "assets/gt", List())
		val example = Example(-1, "assets/example1", None, false, false)

		database.addProject(project)
		val dataProject = database.getAllProject.find(it => it.name == project.name).get
		database.addDocument(document, dataProject.id)
		val dataDocument = database.getDocumentsOfProject(dataProject.id).find(it => it.name == document.name).get
		database.addPage(page, dataDocument.id)
		val dataPage = database.getPagesOfDocument(dataDocument.id).find(it => it.imagePath == page.imagePath).get
		database.addExample(example, dataPage.id)
		val dataExample = database.getExamplesOfPage(dataPage.id).find(it => it.imagePath == example.imagePath).get

		database.deleteExample(dataExample.id)
		val res = database.getExample(dataExample.id).isEmpty
		database.disconnect

		assert(res)
	}

	"Example" should "be updatable" in {
		val database = new DatabaseSqlite()
		database.connect
		val project = Project(-1, "super projet de la mort", RecogniserType.None, List())
		val document = Document(-1, "docu de la mort", List(), false)
		val page = Page(-1, "assets/img", "assets/gt", List())
		val example = Example(-1, "assets/example1", None, false, false)

		database.addProject(project)
		val dataProject = database.getAllProject.find(it => it.name == project.name).get
		database.addDocument(document, dataProject.id)
		val dataDocument = database.getDocumentsOfProject(dataProject.id).find(it => it.name == document.name).get
		database.addPage(page, dataDocument.id)
		val dataPage = database.getPagesOfDocument(dataDocument.id).find(it => it.imagePath == page.imagePath).get
		database.addExample(example, dataPage.id)
		val dataExample = database.getExamplesOfPage(dataPage.id).find(it => it.imagePath == example.imagePath).get

		val update = ("je suis mega simpa", "assets/example1Updated", false, true)

		val newExample = dataExample.copy(transcript = Some(update._1), imagePath = update._2, enabled = update._3, validated = update._4)

		database.saveExampleEdition(List(newExample))

		val updatedExample = database.getExamplesOfPage(dataPage.id).find(it => it.id == dataExample.id).get

		database.disconnect

		assert(updatedExample.imagePath == update._2 && updatedExample.transcript.get == update._1 && updatedExample.enabled == update._3 && updatedExample.validated == update._4)
	}


	*/
	"test" should "pass" in {
		//val database = new DatabaseSqlite()
		val database = new DatabaseConnector()
		database.connect

		val project1 = Project(-1, "coucou", RecogniserType.None, List())
		val project2 = Project(-1, "au revoir", RecogniserType.None, List())
		val project3 = Project(-1, "bijour", RecogniserType.None, List())
		var p = database.addProject(project1)
		println(p)
		p = database.addProject(project2)
		println(p)
		p = database.addProject(project3)
		println(p)

		val projectsData: Iterable[Project] = database.getAllProject

		val document1 = Document(-1, "super docu", List(), false)
		val document2 = Document(-1, "mega docu", List(), false)
		val idPr1 = projectsData.find(it => it.name == project1.name).get.id
		database.addDocument(document1, idPr1)
		database.addDocument(document2, idPr1)

		val docuOfProj1 = database.getDocumentsOfProject(idPr1)
		val projects = database.getAllProject
		database.disconnect

		println(projects)
		println(docuOfProj1)
	}
}
