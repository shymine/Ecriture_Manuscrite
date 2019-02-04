package model.data.types

class Point(val x : Int, val y : Int) {
  override def toString: String = {
    "(" + x.toString + "," + y.toString + ")"
  }

  override def equals(obj : Any) : Boolean = {
    if (!obj.isInstanceOf[Point]) return false

    val pointObj = obj.asInstanceOf[Point]

    x == pointObj.x && y == pointObj.y
  }
}