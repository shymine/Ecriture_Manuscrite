package model.preparation.processing

import model.common.Example
import model.preparation.input.piff.PiFF

import scala.collection.mutable.ListBuffer

object BaseExampleMaker extends ExampleMaker {
  val elementId = 0 // TODO : compute id dynamically

  override def makeExamples(p : PiFF): List[Example] = {
    val imgPath = p.src
    val img = ImageProcessing.loadImageFromFile(imgPath)

    val (imgPathPrefix, imgPathSuffix) = {
      val s = imgPath.split('.')
      (s(0), s(1))
    }

    val examples = new ListBuffer[Example]

    p.pages.foreach(page => {
      page.elements.foreach(element => {
        val transcript =
          element.contents match {
            case "" => None
            case c => Some(c)
          }

        // new image from the polygon
        val exampleImg = ImageProcessing.crop(img, element.polygon)

        // creating the actual image file
        ImageProcessing.writeImageToFile(exampleImg, s"$imgPathPrefix-$elementId-$imgPathSuffix")

        // adding the newly created example to the list
        examples += Example(elementId, imgPath, transcript)
      })
    })

    examples.toList
  }
}
