package model.common

import org.json.{JSONArray, JSONObject}

/**
  * Represents the highest class in the datastructure, regroups the documents that are linked together  by their meanings
  * @param id The id of the Project in the database, set when stored, not takken in account when first created
  * @param name The name given to thr project
  * @param recogniser The recogniser format that the exported examples must match
  * @param documents The documents that composed the project
  */
case class Project(id : Long, name : String, var recogniser : RecogniserType.Value, var documents : Iterable[Document]) {
	/**
	  * Creates the JSONObject corresponding to the Project
	  * @return The JSONObject corresponding to the Project with its documents
	  */
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
