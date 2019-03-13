package model.common

import org.json.{JSONArray, JSONObject}

case class Page(id : Long, imagePath : String, groundTruthPath : String, examples : Iterable[Example]) {
	def toJSON = {
		val json = new JSONObject()
		json.put("id", id)
		json.put("imagePath", imagePath)
		json.put("groundTruthPath", groundTruthPath)
		val jsonExamples = new JSONArray()
		examples.foreach(example => jsonExamples.put(example.toJSON))
		json.put("examples", jsonExamples)
	}

}