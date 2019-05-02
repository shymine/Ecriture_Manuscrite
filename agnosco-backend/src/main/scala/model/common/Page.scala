package model.common

import javax.imageio.ImageIO
import model.preparation.input.PiFFReader
import org.eclipse.persistence.tools.file.FileUtil
import org.json.{JSONArray, JSONObject}

import scala.io.Source

case class Page(id : Long, /*image64 : String,*/ groundTruth : String, examples : Iterable[Example]) {

	/*def toJSON: JSONObject = {
		val json = new JSONObject()
		json.put("id", id)
		// json.put("image64", image64)
		json.put("groundTruth", groundTruth)
		val jsonExamples = new JSONArray()
		examples.foreach(example => jsonExamples.put(example.toJSON))
		json.put("examples", jsonExamples)
	}*/

	def toJSON: JSONObject = {
		val json = new JSONObject()
		json.put("id", id)
		val vtFile = Source.fromFile(globalDataFolder + "/" + groundTruth)
		val content = vtFile.getLines().toList.reduce(_+" "+_)
		val piff = PiFFReader.fromString(content)
		val imgPath = piff.get.
		val vtText = ""
		val image64 = ""
	}

}
