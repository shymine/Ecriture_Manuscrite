package model.data.input.piff

import org.json.JSONObject

/*
{
  "date": "01-01-2000",
  "src": "src.jpg",
  "pages: [
    {
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
    },
    ...
  ]
}
 */

/** This class represents a PiFF file as an object. */
class PiFF(val date : String, val src : String,
           val pages : List[PiFFPage]) {
  def toJSON : JSONObject = {
    val json = new JSONObject()
    json.put("date", date)
    json.put("src", src)
    pages.foldLeft(json)((json, page) => json.append("pages", page.toJSON))
  }

  override def equals(obj : Any) : Boolean = {
    if (!obj.isInstanceOf[PiFF]) return false

    val piffObj = obj.asInstanceOf[PiFF]

    date.equals(piffObj.date) &&
      src.equals(piffObj.src) &&
      pages.equals(piffObj.pages)
  }

  override def toString : String = toJSON.toString
}
