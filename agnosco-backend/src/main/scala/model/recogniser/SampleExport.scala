package model.recogniser
import java.io.{File, FileInputStream, FileOutputStream}
import java.nio.file.{Files, Paths, StandardCopyOption}
import java.util.Calendar

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
	override def export(samples: Iterable[Example]): Unit = {
		val path: String = globalExportFolder+"/"+ "[ ]".r.replaceAllIn(Calendar.getInstance().getTime.toString, "_")
		new File(path).mkdir()
		samples.foreach(example => {
			val pathIm = path +"/"+example.imagePath

			val pathTr = path +"/"+getFileName(example.imagePath)+".txt"

			new File(pathIm)

			val pathTmp = Files.copy(
				Paths.get(globalDataFolder + "/" + example.imagePath),
				Paths.get(pathIm),
				StandardCopyOption.REPLACE_EXISTING
			)

			val fileTr = new FileOutputStream(pathTr)
			fileTr.write(example.transcript.get.toArray.map(_.toByte))
			fileTr.close()

		})
	}

}
