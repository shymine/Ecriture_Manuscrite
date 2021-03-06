package model

import model.common._
import model.database.DatabaseConnector
import model.recogniser.SampleExport
import org.scalatest.FlatSpec

class ExportationTest extends FlatSpec {

  "test" should "pass" in {
    //val database = new DatabaseSqlite()


    val example = Example(-1, "AFGHJK10.tif", Some("transcription"), true, true)

    val export = new SampleExport()
    export.train(List(example))

  }
}
