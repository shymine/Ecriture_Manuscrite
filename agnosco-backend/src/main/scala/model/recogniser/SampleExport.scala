package model.recogniser
import java.io.{File, PrintWriter}
import java.nio.file.{Files, Paths, StandardCopyOption}
import java.util.Calendar

import model.common.Example
import model.common.{globalDataFolder, globalExportFolder}

/**
  * Default export
  */
class SampleExport extends Recogniser {

	// get the name of the file without extension
	private def getFileName(str: String): String = {
		val regexp = "[.][a-zA-Z]+".r
		regexp.replaceAllIn(str,"")
	}

	/**
	  * Export the examples to the given format
	  * @param samples The samples to export
	  */
	override def export(samples: Iterable[Example]): Unit = {
		val path: String = globalExportFolder+"/"+ "[ ]".r.replaceAllIn(Calendar.getInstance().getTime.toString, "_")
		new File(path).mkdir()
		samples.foreach(example => {
			val pathIm = path +"/"+example.imagePath
			val pathTr = path +"/"+getFileName(example.imagePath)+".txt"
			new File(pathIm)
			Files.copy(
				Paths.get(globalDataFolder + "/" + example.imagePath),
				Paths.get(pathIm),
				StandardCopyOption.REPLACE_EXISTING
			)
			val pw = new PrintWriter(new File(pathTr), "UTF-8")
			pw.write(example.transcript.get)
			pw.close()
		})
	}

}
