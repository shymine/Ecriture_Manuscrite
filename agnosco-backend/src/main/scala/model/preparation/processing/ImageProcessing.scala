package model.preparation.processing

import sys.process._
import model.preparation.types.Polygon
import model.common.pythonImageCropperExecutablePath

object ImageProcessing {
  def createThumbnail(imgPath : String, imageId : Int, polygon : Polygon) : String = {
    val (x1, y1, x2, y2) =
      polygon.points.foldLeft((Int.MaxValue, Int.MaxValue, Int.MinValue, Int.MinValue)) {
        (acc, p) =>
          val (xmin, ymin, xmax, ymax) = acc
          (xmin min p.x, ymin min p.y, xmax max p.x, ymax max p.y)
      }

    val col = x1
    val row = y1
    val width = x2 - x1
    val height = y2 - y1

    val exitCode = s"python3 $pythonImageCropperExecutablePath $imgPath $imageId $row $col $width $height".!
    if (exitCode != 0) {
      println("ERROR: Python OpenCV script returned an error code")
    }

    val (file, extension) = {
      val s = imgPath.split('.')
      (s(0), s(1))
    }

    s"$file$imageId.$extension"
  }
}
