package model.common

import org.json.JSONObject

case class Example(id : Long, image64 : String, var transcript : Option[String], var enabled: Boolean = true, var validated: Boolean = false) {
	def toJSON: JSONObject = {
		val json = new JSONObject()
		json.put("id", id)
		json.put("image64", image64)
		json.put("transcript", transcript.get)
		json.put("enabled", enabled)
		json.put("validated", validated)
	}

}
