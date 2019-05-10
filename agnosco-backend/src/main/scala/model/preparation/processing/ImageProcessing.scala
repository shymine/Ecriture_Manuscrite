package model.preparation.processing

import sys.process._
import model.preparation.types.Polygon
import model.common.pythonImageCropperExecutablePath

/** This object calls the OpenCV Python script to cut images. */
object ImageProcessing {
  /** Creates a thumbnail from an image and a polygon.
    * @param imgPath the path to the image we want to cut
    * @param imageId an ID that will be given to the created file
    * @param polygon the polygon defining the area we want to cut
    * @return the path to a newly created thumbnail
    */
  def createThumbnail(imgPath : String, imageId : Int, polygon : Polygon) : String = {
    // getting min and max X and Y coordinates to cut a rectangle
    // a better algorithm could be used here, but we lacked time to do it
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

    // calling the executable Python script
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
