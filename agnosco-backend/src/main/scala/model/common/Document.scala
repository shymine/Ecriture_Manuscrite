package model.common

import org.json.{JSONArray, JSONObject}

/**
  * The Document class represents a set of pages that are linked by their meanings
  * @param id The id in the database, set when stored, not takken in account when the document is first created
  * @param name The name you want to give to the document
  * @param pages The set of pages that constitute the document
  * @param prepared Determine wether the pages of the document are prepared, cut in examples
  */
case class Document(id : Long, name : String, pages : Iterable[Page], prepared: Boolean) {
	/**
	  * Create a JSONObject composed of the differents membres of the Document
	  * @return The JSONObject corresponding to the Document
	  */
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
