package model.common

import org.json.JSONObject

case class Example(id : Long, imagePath : String, var transcript : Option[String]) {
	def toJSON: JSONObject = {
		val json = new JSONObject()
		json.put("id", id)
		json.put("imagePath", imagePath)
		json.put("transcript", transcript)
	}

}