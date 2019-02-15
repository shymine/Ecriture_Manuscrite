package model.data.input.piff

import org.json.JSONObject

/*
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
}
*/

class PiFFPage(val width : Int, val height : Int,
               val elements : List[PiFFElement]) {
  def toJSON : JSONObject = {
    val json = new JSONObject()
    json.put("width", width)
    json.put("height", height)
    elements.foldLeft(json)(
      (json, element) => json.append("elements", element.toJSON))
  }

  override def equals(obj : Any) : Boolean = {
    if (!obj.isInstanceOf[PiFFPage]) return false

    val pageObj = obj.asInstanceOf[PiFFPage]

    width == pageObj.width &&
      height == pageObj.height &&
      elements.equals(pageObj.elements)
  }

  override def toString : String = toJSON.toString
}