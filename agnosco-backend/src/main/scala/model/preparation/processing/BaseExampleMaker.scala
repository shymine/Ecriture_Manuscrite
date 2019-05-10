package model.preparation.processing

import model.common
import model.common.{Example, globalDataFolder}
import model.preparation.input.piff.PiFF
import model.preparation.processing.linedetection.BlurLineDetector

import scala.collection.mutable.ListBuffer

/** An implementation of [[ExampleMaker]]. */
object BaseExampleMaker extends ExampleMaker {
	// connects to the Intuidoc line detector that must run in a Linux 32-bit VM
	val lineDetector = new BlurLineDetector(common.detectorIp, common.filePort, common.answerPort)

	override def makeExamples(p : PiFF): List[Example] = {
		val examples = new ListBuffer[Example]

		/*
		* The example id will be -1 so that the database automatically generates a unique id, but
		* we need a way to make sure the examples are associated with a unique image filename.
		* imageId is a way to ensure that. It is just an integer being incremented as we create examples.
		*/
		var imageId = 0

		val page = p.page

		page.elements.foreach(element => {
			val transcript =
				element.contents match {
					case "" => None
					case c => Some(c)
				}

			// create paragraph image from the polygon
			val imgPath = s"$globalDataFolder/${page.src}"
			println("img path : " + imgPath)
			val paragraphImgPath = ImageProcessing.createThumbnail(imgPath, imageId, element.polygon)

			// calling the line detector on the newly created image
			lazy val polygons = lineDetector.detectLines(paragraphImgPath)

			if (common.disableLineDetector) {
				// line detector is disabled, adding the paragraph into the examples
				examples += Example(-1, removeDataFolderPath(paragraphImgPath), transcript)
			} else if (polygons.isEmpty) {
				// enabled but no answer (means no lines detected)
				examples += Example(-1, removeDataFolderPath(paragraphImgPath), transcript, enabled = false)
			} else {
				// get the line lengths
				val lineLengths =
					polygons.map(polygon => {
						val xs = polygon.points.map(_.x)
						xs.max - xs.min
					}) // List[Int]

				// get the line lengths as percentages of the total length
				val lineLengthRatios = {
					val totalLength = lineLengths.sum
					lineLengths.map(len => len.toDouble / totalLength)
				}

				// cut the transcript into subtranscripts according to the ratios
				val transcripts = {
					transcript match {
						case None => List.fill(lineLengthRatios.length)(None)
						case Some(t) =>
							lineLengthRatios.foldLeft((List.empty[Option[String]], 0))((m, ratio) => {
								val (acc, offset) = m
								val endOffset = {
									val o = (offset + ratio * t.length).toInt
									if (o >= t.length) t.length - 1 else o
								}
								val subtranscript = t.substring(offset, endOffset)
								(Some(subtranscript) :: acc, endOffset)
							})._1.reverse
					}
				}

				// generating images for each line in the paragraph
				val images = {
					val listBuffer = new ListBuffer[String]
					for (i <- polygons.indices) {
						listBuffer += ImageProcessing.createThumbnail(paragraphImgPath, i + 1, polygons(i))
					}
					listBuffer.toList
				}

				// creating the examples
				val newExamples = images.zip(transcripts).map(z => Example(-1, removeDataFolderPath(z._1), z._2))

				// adding the newly created examples to the list
				examples ++= newExamples
			}

			imageId += 1
		})
		examples.toList
	}

	// the path stored in the example must not include "data/"
	private def removeDataFolderPath(path: String): String = {
		"data/".r.replaceAllIn(path, "")
	}
}
