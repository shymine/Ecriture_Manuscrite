package model.preparation.processing

import sys.process._
import model.preparation.types.Polygon

object ImageProcessing {
  /*def loadImageFromFile(src : String) : Image = {
    Imgcodecs.imread(src)
  }

  def writeImageToFile(img : Image, src : String) : Unit = {
    Imgcodecs.imwrite(src, img)
  }

  def crop(image : Image, rect : Rect) : Image = {
    image.submat(rect)
  }*/

  def createThumbnail(imgPath : String, imageId : Int, polygon : Polygon) : String = {

    val pathToClass = getClass.getResource("../../../").getPath
    println(pathToClass)
    val pythonExecutablePath = "/home/timothee/Documents/Workspace/Ecriture_Manuscrite/Ecriture_Manuscrite/imagecropper.py" // /Users/cloudyhug/Documents/cours/projet/Ecriture_Manuscrite/imagecropper.py"

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

    println(s"debug: $pythonExecutablePath $imgPath $imageId $row $col $width $height")
    val exitCode = s"python $pythonExecutablePath $imgPath $imageId $row $col $width $height".!
    if (exitCode != 0) {
      println("ERROR: Python OpenCV script returned an error code")
    }

    val (file, extension) = {
      val s = imgPath.split('.')
      (s(0), s(1))
    }

    s"$file$imageId.$extension"
  }

  /*def getThumbnail(image : Image, line : Line, averageLineMargin : Double) : Image = {
    val (xmin, xmax, sumy) =
      line.foldLeft((image.size.width, 0.0, 0.0)) {
        (acc, p) =>
          val (xmin, xmax, sumy) = acc
          (xmin min p.x, xmax max p.x, sumy + p.y)
      }
    val ymean = sumy.toDouble / line.length
    val r = new Rect(xmin.toInt, (ymean - averageLineMargin).toInt, (xmax - xmin).toInt, (2 * averageLineMargin).toInt)
    image.submat(r)
  }*/
}
