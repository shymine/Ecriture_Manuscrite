package model

import model.common.{Example, Project, RecogniserType}
import model.database.DatabaseSqlite
import org.scalatest.FlatSpec

class DatabaseSqliteTest extends FlatSpec {

	"It" should "be possible to add a project" in {
		val database = new DatabaseSqlite()
		database.connect
		val project = Project(3, "super projet de la mort", RecogniserType.None, List())
		database.addProject(project)
		val projects = database.getAllProject
		assert(projects.exists(it => it.name == "super projet de la mort"))
		database.disconnect
	}

/*
	"Getting all the project" should "work i think" in {
		val database = new DatabaseSqlite()
		database.connect
		val projects1 = database.getAllProject
		projects1.foreach(it => print(s"project: $it, "))
		println("fin first")
		val project = Project(3, "super projet de la mort", RecogniserType.None, List())
		database.addProject(project)
		val projects2 = database.getAllProject
		projects2.foreach(it => print(s"project: $it, "))
		println("fin second")
		database.disconnect
	}
	*/
}
