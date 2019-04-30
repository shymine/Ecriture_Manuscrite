package model.preparation.processing

import model.common.{Example, globalDataFolder}
import model.preparation.input.piff.PiFF

import scala.collection.mutable.ListBuffer

object BaseExampleMaker extends ExampleMaker {
  override def makeExamples(p : PiFF): List[Example] = {
    val examples = new ListBuffer[Example]

    /*
    * The example id will be -1 so that the database automatically generates a unique id, but
    * we need a way to make sure the examples are associated with a unique image filename.
    * imageId is a way to ensure that. It is just an integer being incremented as we create examples.
    */
    var imageId = 0

    p.pages.foreach(page => {
      page.elements.foreach(element => {
        val transcript =
          element.contents match {
            case "" => None
            case c => Some(c)
          }

        // new image from the polygon
        val imgPath = s"$globalDataFolder/${page.src}"
        println("img path : " + imgPath)
        val newExampleImgPath = ImageProcessing.createThumbnail(imgPath, imageId, element.polygon)

        // adding the newly created example to the list
        examples += Example(-1, newExampleImgPath, transcript)

        imageId += 1
      })
    })

    examples.toList
  }
}
