package model.preparation.input.piff

import model.preparation.input.{Language, ScriptType}
import model.preparation.types.{Point, Polygon}
import org.json.JSONObject

/* example of an element
{
  "polygon": "(4,6);(7,5)",
  "contents": "hello",
  "scriptType": "hand",
  "language": "english"
}
*/

/** This class represents some text in a document with additional metadata. */
class PiFFElement(val polygon : Polygon, val contents : String,
                  val scriptType : ScriptType, val language : Language) {
  /** Other constructor from the direct values of column, row, width, and height. */
  def this(c : Int, r : Int, w : Int, h : Int, cont : String, st : ScriptType,
           l : Language) =
    this(
      new Polygon(List(new Point(c, r), new Point(c + w, r + h))), cont, st, l)
  /** Builds a [[JSONObject]] from the data in this PiFFElement object. */
  def toJSON : JSONObject = {
    val json = new JSONObject()
    json.put("polygon", polygon.toString)
    json.put("contents", contents)
    json.put("scriptType", scriptType.toString)
    json.put("language", language.toString)
    json
  }

  override def equals(obj : Any) : Boolean = {
    if (!obj.isInstanceOf[PiFFElement]) return false

    val elementObj = obj.asInstanceOf[PiFFElement]

    polygon.equals(elementObj.polygon) &&
      contents.equals(elementObj.contents) &&
      scriptType == elementObj.scriptType &&
      language == elementObj.language
  }

  override def toString : String = toJSON.toString
}
