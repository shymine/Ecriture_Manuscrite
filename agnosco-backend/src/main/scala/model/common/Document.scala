package model.common

import org.json.{JSONArray, JSONObject}


case class Document(id : Long, name : String, pages : Iterable[Page], prepared: Boolean) {
	def toJSON = {
		val json = new JSONObject()
		json.put("id", id)
		json.put("name", name)
		json.put("prepared", prepared)
		val jsonPages = new JSONArray()
		pages.foreach(page => jsonPages.put(page.toJSON))
		json.put("pages", jsonPages)
	}
}