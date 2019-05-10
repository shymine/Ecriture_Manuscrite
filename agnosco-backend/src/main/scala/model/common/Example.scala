package model.common

import java.io.File
import java.util.Base64

import org.apache.commons.io.FileUtils
import org.json.JSONObject

/**
  * Represent the association between the image of a line and its transcription
  * @param id The id in the database of the Example, not takken in account before the store in the database
  * @param imagePath The name of the image corresponding to the example
  * @param transcript The transcription of the text in the associated image
  * @param enabled Determine wether the example is relevant or not
  * @param validated Determine wether the example is checked and validated by the user or not
  */
case class Example(id : Long, imagePath : String, var transcript : Option[String], var enabled: Boolean = true, var validated: Boolean = false) {

	/**
	  * Create the JSONObject corresponding to the Example with the image as a base64 string
	  * @return The JSONObject corresponding to the Example
	  */
	def toJSON: JSONObject = {
		val json = new JSONObject()
		json.put("id", id)
		json.put("transcript", transcript.get)
		json.put("enabled", enabled)
		json.put("validated", validated)

		val fileContent = FileUtils.readFileToByteArray(new File(globalDataFolder+"/"+imagePath))
		val image64 = Base64.getEncoder.encodeToString(fileContent)

		json.put("image64", image64)
		json
	}

	// toJSON + image extension info
	/**
	  * The same as toJSON with the addition of the extension of the image as part of the json for information
	  * @return The JSONObject corresponding to the Example with the image extension
	  */
	def toRequestJSON: JSONObject = {
		val json = toJSON
		val extension = imagePath.split('.')(1)
		json.put("extension", extension)
		json
	}

}
