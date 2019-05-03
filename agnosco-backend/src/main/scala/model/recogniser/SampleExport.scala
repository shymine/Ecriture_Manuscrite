package model.recogniser
import java.io.{File, FileInputStream, FileOutputStream}
import java.nio.file.{Files, Paths, StandardCopyOption}

import model.common.Example
import org.json.JSONObject
import model.common.{globalDataFolder, globalExportFolder}

class SampleExport extends ConverterRecogniser {
	override protected var converter: Converter = _

	def getFileName(str: String): String = {
		val regexp = "[.][a-zA-Z]+".r
		regexp.replaceAllIn(str,"")
	}

	/**
	  * Train the Recognizer using the samples given in parameter
	  *
	  * @param samples The samples the Recognizer trains on
	  */
	override def train(samples: Iterable[Example]): Unit = {
		samples.foreach(example => {
			val pathIm = globalExportFolder+"/"+example.imagePath

			val pathTr = globalExportFolder+"/"+getFileName(example.imagePath)+".txt"

			new File(pathIm)

			val path = Files.copy(
				Paths.get(globalDataFolder + "/" + example.imagePath),
				Paths.get(pathIm),
				StandardCopyOption.REPLACE_EXISTING
			)

			val fileTr = new FileOutputStream(pathTr)
			fileTr.write(example.transcript.get.toArray.map(_.toByte))
			fileTr.close()

		})
	}


	/**
	  * Evaluate the Recognizer performance on a test set of Sample
	  *
	  * @param samples The set to test the Recognizer
	  */
	override def evaluate(samples: Iterable[Example]): JSONObject = throw new NotImplementedError()

	/**
	  * Production mode of the Recognizer
	  *
	  * @param samples The samples the Recognizer must "translate".
	  *                It's format as a list to ease the process of
	  *                knowing the correct translation to the image
	  * @return Returns the List of the sample corresponding to the
	  *         image and the translation. If the input format is a
	  *         paragraph then there may be n samples extracted from
	  *         it depending if the Recognizer output for a paragraph
	  *         is an output per line or one output for the paragraph.
	  */
	override def recognize(samples: Iterable[Example]): Iterable[Example] = throw new NotImplementedError()
}
