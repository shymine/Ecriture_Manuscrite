package model.preparation.types

class Polygon(val points : List[Point]) {
  override def toString : String = {
    points.map(p => p.toString).mkString(";")
  }

  override def equals(obj : Any) : Boolean = {
    if (!obj.isInstanceOf[Polygon]) return false
    points.equals(obj.asInstanceOf[Polygon].points)
  }
}