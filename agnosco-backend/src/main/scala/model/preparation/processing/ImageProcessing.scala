package model.preparation.processing

import model.preparation.types.{Image, Line, Polygon}
import org.opencv.core.Rect

object ImageProcessing {
  def crop(image : Image, rect : Rect) : Image = {
    image.submat(rect)
  }

  def crop(image : Image, polygon : Polygon) : Image = {
    val s = image.size
    val (x1, y1, x2, y2) =
      polygon.points.foldLeft((s.width, 0.0, s.width, s.height)) {
        (acc, p) =>
          val (xmin, ymin, xmax, ymax) = acc
          (xmin min p.x, ymin min p.y, xmax max p.x, ymax max p.y)
      }
    val r = new Rect(x1.toInt, y1.toInt, (x2 - x1).toInt, (y2 - y1).toInt)
    image.submat(r)
  }

  def getThumbnail(image : Image, line : Line, averageLineMargin : Double) : Image = {
    val (xmin, xmax, sumy) =
      line.foldLeft((image.size.width, 0.0, 0.0)) {
        (acc, p) =>
          val (xmin, xmax, sumy) = acc
          (xmin min p.x, xmax max p.x, sumy + p.y)
      }
    val ymean = sumy.toDouble / line.length
    val r = new Rect(xmin.toInt, (ymean - averageLineMargin).toInt, (xmax - xmin).toInt, (2 * averageLineMargin).toInt)
    image.submat(r)
  }
}