package model.common

import java.io.File
import java.util.Base64

import model.preparation.input.PiFFReader
import org.apache.commons.io.FileUtils
import org.json.JSONObject

import scala.io.Source

/**
  * Represent the association of a physical page and its associated describing file
  * along with the examples based on those files
  * @param id The id of the Page in the database, set when stored, not takken in account when first created
  * @param groundTruth
  * @param examples
  */
case class Page(id : Long, groundTruth : String, examples : Iterable[Example]) {

	def toJSON: JSONObject = {
		val json = new JSONObject()
		json.put("id", id)
		val vtFile = Source.fromFile(globalDataFolder + "/" + groundTruth)
		val content = vtFile.getLines().toList.reduce(_+" "+_)

		val piff = PiFFReader.fromString(content)
		val imgPath = piff.get.page.src

		val fileContent = FileUtils.readFileToByteArray(new File(globalDataFolder+"/"+imgPath))
		val image64 = Base64.getEncoder.encodeToString(fileContent)

		val vtName = groundTruth
		json.put("imgName", imgPath)
		json.put("vtName", vtName)
		json.put("image64", image64)
		json.put("vtText", content)
		json
	}

}
