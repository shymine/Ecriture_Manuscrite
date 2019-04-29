package model.common

import org.json.{JSONArray, JSONObject}

case class Page(id : Long, image64 : String, groundTruth : String, examples : Iterable[Example]) {
	def toJSON = {
		val json = new JSONObject()
		json.put("id", id)
		json.put("image64", image64)
		json.put("groundTruth", groundTruth)
		val jsonExamples = new JSONArray()
		examples.foreach(example => jsonExamples.put(example.toJSON))
		json.put("examples", jsonExamples)
	}

}
