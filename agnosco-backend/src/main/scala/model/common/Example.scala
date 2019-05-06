package model.common

import java.io.File
import java.util.Base64

import org.apache.commons.io.FileUtils
import org.json.JSONObject

case class Example(id : Long, imagePath : String, var transcript : Option[String], var enabled: Boolean = true, var validated: Boolean = false) {

	def toJSON: JSONObject = {
		val json = new JSONObject()
		json.put("id", id)
		json.put("transcipt", transcript.get)
		json.put("enabled", enabled)
		json.put("validated", validated)

		val fileContent = FileUtils.readFileToByteArray(new File(globalDataFolder+"/"+imagePath))
		val image64 = Base64.getEncoder.encodeToString(fileContent)

		json.put("image64", image64)
		json
	}

	// toJSON + image extension info
	def toRequestJSON: JSONObject = {
		val json = toJSON
		val extension = imagePath.split('.')(1)
		json.put("extension", extension)
		json
	}

}
