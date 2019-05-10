package model.preparation.input.piff

import org.json.JSONObject

/* example of PiFF file
{
  "date": "01-01-2000",
  "page":
    {
      "src": "src.jpg",
      "width": 34,
      "height": 55,
      "elements": [
        {
          "polygon": "(4,6);(7,5)",
          "contents": "hello",
          "scriptType": "hand",
          "language": "english"
        },
        ...
      ]
    }
}
*/

/** This class represents a PiFF file as an object.
  * It contains a date and a page.
  */
class PiFF(val date : String, val page : PiFFPage) {
  /** Builds a [[JSONObject]] from the data in this PiFF object. */
  def toJSON : JSONObject = {
    val json = new JSONObject()
    json.put("date", date)
    json.put("page", page.toJSON)
  }

  override def equals(obj : Any) : Boolean = {
    if (!obj.isInstanceOf[PiFF]) return false

    val piffObj = obj.asInstanceOf[PiFF]

    date.equals(piffObj.date) && page.equals(piffObj.page)
  }

  override def toString : String = toJSON.toString
}
