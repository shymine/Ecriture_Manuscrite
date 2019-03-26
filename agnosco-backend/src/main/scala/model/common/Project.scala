package model.common

import org.json.{JSONArray, JSONObject}


case class Project(id : Long, name : String, var recogniser : RecogniserType.Value, var documents : Iterable[Document]) {
	def toJSON = {
		val json = new JSONObject()
		json.put("id", id)
		json.put("name", name)
		json.put("recogniser", recogniser.toString)
		val jsonDoc = new JSONArray()
		documents.foreach(doc => jsonDoc.put(doc.toJSON))
		json.put("documents", jsonDoc)
	}
}
