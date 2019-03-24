package model.common

import org.json.JSONObject

case class Example(id : Long, imagePath : String, var transcript : Option[String], var enabled: Boolean = true, var validated: Boolean = false) {
	def toJSON: JSONObject = {
		val json = new JSONObject()
		json.put("id", id)
		json.put("imagePath", imagePath)
		json.put("transcript", transcript)
		json.put("enabled", enabled)
		json.put("validated", validated)
	}

}