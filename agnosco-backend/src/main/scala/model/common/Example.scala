package model.common

import java.io.{ByteArrayOutputStream, File}
import java.util.Base64

import javax.imageio.ImageIO
import org.apache.commons.io.FileUtils
import org.json.JSONObject

case class Example(id : Long, imagePath : String, var transcript : Option[String], var enabled: Boolean = true, var validated: Boolean = false) {

	/*def toJSON: JSONObject = {
		val json = new JSONObject()
		json.put("id", id)
		json.put("imagePath", imagePath)
		json.put("transcript", transcript.getOrElse(""))
		json.put("enabled", enabled)
		json.put("validated", validated)
		json
	}*/

	def toJSON: JSONObject = {
		val json = new JSONObject()
		json.put("id", id)
		json.put("transcipt", transcript.get)
		json.put("enabled", enabled)
		json.put("validated", validated)

		/*val image = ImageIO.read(new File(globalDataFolder+"/"+imagePath))
		val baos = new ByteArrayOutputStream()
		ImageIO.write(image, "png", baos)
		baos.flush()
		val image64 = Base64.getEncoder.encodeToString(baos.toByteArray)*/
		val fileContent = FileUtils.readFileToByteArray(new File(globalDataFolder+"/"+imagePath))
		val image64 = Base64.getEncoder.encodeToString(fileContent)

		json.put("image64", image64)
		json
	}

}
