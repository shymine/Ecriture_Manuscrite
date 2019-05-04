package model.common

import java.io.{ByteArrayOutputStream, File}
import java.util.Base64

import javax.imageio.ImageIO
import model.preparation.input.PiFFReader
import org.apache.commons.io.FileUtils
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
		val imgPath = piff.get.page.src
		/*val image = ImageIO.read(new File(globalDataFolder+"/"+imgPath))
		val baos = new ByteArrayOutputStream()
		ImageIO.write(image,"png",baos)
		baos.flush()
		val image64 = Base64.getEncoder.encodeToString(baos.toByteArray)*/
		val fileContent = FileUtils.readFileToByteArray(new File(globalDataFolder+"/"+imgPath))
		val image64 = Base64.getEncoder.encodeToString(fileContent)

		val name = "[.][a-zA-Z]+".r.replaceAllIn(groundTruth, "")
		json.put("name", name)
		json.put("image64", image64)
		json.put("vtText", content)
		json
	}

}
