package model.preparation.input

import java.io.{File, FileOutputStream}

import model.preparation.input.converters.PiFFConverter
import model.preparation.input.piff.{PiFF, PiFFElement, PiFFPage}
import model.preparation.types.{Point, Polygon}
import org.json.{JSONException, JSONObject}
import converters.PIFF_CONVERTERS

import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer
import scala.io.Source

/** This class builds PiFF objects from files, Strings, or JSON objects. */
object PiFFReader {
	// Reads a PiFFElement from a JSON object in both possible formats.
	private def readElement(json : JSONObject) : PiFFElement = {
		val contents = json.getString("contents")

		// The scriptType field's value is in a closed set, so we need to check this with a converter
		val scriptTypeStr = json.getString("scriptType")
		val scriptType =
			string2ScriptType(scriptTypeStr)
				.getOrElse(
					throw new JSONException(s"Wrong ScriptType value : $scriptTypeStr"))

		// Same for the language field
		val languageStr = json.getString("language")
		val language =
			string2Language(languageStr)
				.getOrElse(
					throw new JSONException(s"Wrong Language value : $languageStr"))

		// Now trying to read the polygon field
		try {
			val point = raw"\((\d+),(\d+)\)".r
			var points = new ListBuffer[Point]
			for (pointStr <- json.getString("polygon").split(';')) {
				pointStr match {
					case point(xStr, yStr) => points += new Point(xStr.toInt, yStr.toInt)
					case _ => throw new JSONException(s"Wrong point format : $pointStr")
				}
			}
			new PiFFElement(new Polygon(points.toList), contents, scriptType, language)
		} catch {
			case e : JSONException =>
				// If the polygon field is not present, we need to try to get the equivalent fields
				val col = json.getInt("col")
				val row = json.getInt("row")
				val width = json.getInt("width")
				val height = json.getInt("height")
				new PiFFElement(col, row, width, height, contents, scriptType, language)
		}
	}

	// Reads a PiFFPage from a JSON object.
	private def readPage(json : JSONObject) : PiFFPage = {
		val src = json.getString("src")
		val width = json.getInt("width")
		val height = json.getInt("height")
		val jsonElements = json.getJSONArray("elements")
		var elements = new ListBuffer[PiFFElement]
		for (i <- 0 until jsonElements.length()) {
			elements += readElement(jsonElements.getJSONObject(i))
		}
		new PiFFPage(src, width, height, elements.toList)
	}

	/** Builds an optional PiFF object from a JSON object.
		* @param json the JSON object
		* @return an optional PiFF object
		*/
	def fromJSON(json : JSONObject) : Option[PiFF] = {
		try {
			val date = json.getString("date")
			val page = readPage(json.getJSONObject("page"))
			Some(new PiFF(date, page))
		} catch {
			case e : JSONException =>
				e.printStackTrace()
				None
		}
	}

	// Builds an optional PiFF object from a PiFF file.
	private def fromPiFFFile(filename : String) : Option[PiFF] = {
		val content =
			Source.fromFile(filename)
				.getLines()
				.foldLeft("")((acc, line) => acc ++ line)
		try {
			fromJSON(new JSONObject(content))
		} catch {
			case e : JSONException => None
		}
	}

	// Tries every converter in the list in order to get a non empty optional PiFF object.
	@tailrec private def tryConverters(filename : String, converters : List[PiFFConverter])
	: Option[PiFF] = {
		converters match {
			case List() => None
			case converter :: t =>
				val piffFromConverter = converter.toPiFF(filename)
				if (piffFromConverter.isDefined) piffFromConverter
				else tryConverters(filename, t)
		}
	}

	/** Builds an optional PiFF object from a file.
	  * @param filename the name of the file
	  * @return an optional PiFF object
	  */
	def fromFile(filename : String) : Option[PiFF] = {
		// First, we try to directly read PiFF
		val piff = fromPiFFFile(filename)
		if (piff.isDefined) return piff

		// If it fails, we need to try every PiFFConverter
		tryConverters(filename, PIFF_CONVERTERS)
	}


  /** Builds an optional PiFF object from a string.
    * @param str the String
    * @return an optional PiFF object
    */
  def fromString(str : String) : Option[PiFF] = {
    try {
			// storing the string in a temporary file
			val outTmp = new FileOutputStream("fromString.tmp")
			outTmp.write(str.getBytes)
			outTmp.close()

			// calling a method defined above to try reading PiFF
			val piffOpt = fromFile("fromString.tmp")

			// deleting the temporary file and returning the option
			new File("fromString.tmp").delete()
			piffOpt
    } catch {
      case e : JSONException => None
    }
  }
}
