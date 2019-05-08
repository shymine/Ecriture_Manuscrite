package model.preparation.processing.linedetection

import java.io._
import java.net.Socket
import java.nio.file.Files
import java.util.Base64

import model.preparation.types.{Point, Polygon}

import scala.collection.mutable.ListBuffer

class BlurLineDetector(detectorIp : String, filePort : Int, answerPort : Int) extends LineDetector {
  /*private def map3[A, B](l: List[A], f1 : (A, A) => B, f: (A, A, A) => B, fn : (A, A) => B): List[B] = {
    def mapEnd(l: List[A]): List[B] = {
      l match {
        case List(_) => List.empty
        case List(x, y) => List(fn(x, y))
        case x :: y :: z :: r =>
          f(x, y, z) :: mapEnd(y :: z :: r)
        case _ => throw new IllegalArgumentException("map3")
      }
    }

    def mapBegin(l: List[A]) : List[B] = {
      l match {
        case List(x, y, z) => List(f1(x, y), f(x, y, z), fn(z))
        case x :: y :: _ => f1(x, y) :: mapEnd(l)
        case _ => throw new IllegalArgumentException("map3")
      }
    }

    mapBegin(l)
  }*/
  /*
  *  x, y => f1(x, y), fn(x, y)
  *  x, y, z => f1(x, y), f(x, y, z), fn(y, z)
  *  x, y, z, t => f1(x, y), f(x, y, z), f(y, z, t), fn(z, t)
  * */
  // use map3 for a more advanced margin calculation method when detecting lines :
  // an f function called with f(lineAbove, currentLine, lineBelow) would be able to calculate distances between lines
  // f1 would check only the next line, and fn would check only the line before
  // the subfunctions (mapEnd and mapBegin) should also be made tail-recursive for performance reasons

  override def detectLines(src : String) : List[Polygon] = {
    val fileSocket = new Socket(detectorIp, filePort)
    val answerSocket = new Socket(detectorIp, answerPort)

    // send the file to the line detector
    val fileBytes = Files.readAllBytes(new File(src).toPath)

    val encodedBytes = Base64.getEncoder.encode(fileBytes)

    val out = new DataOutputStream(fileSocket.getOutputStream)
    out.writeInt(encodedBytes.length)
    out.write(encodedBytes)
    println("[INFO] File sent to the line detector")

    // read the answer from the server
    val in = new DataInputStream(answerSocket.getInputStream)
    val length = in.readInt
    val answerBytes = new Array[Byte](length)
    in.readFully(answerBytes)
    answerSocket.close()
    println("[INFO] Answer received from the line detector")
    val answer = new String(answerBytes)

    val lines = {
      val lines = ListBuffer.empty[(Array[Point], Array[Point])]
      answer
        .split('\n') // Array[String]
        .toStream // Stream[String] : lazy evaluation

        // remove "Haut:", "Bas:" at the beginning
        .map(_.split(':').last) // Stream[String]

        // get a list of "x,y" points from a "x1,y1;x2,y2;x3,y3..." String
        .map(_.split(';')) // Stream[Array[String]]

        // in each list, split "x,y" into List("x", "y")
        .map(_.map(_.split(','))) // Stream[Array[Array[String]]]

        // get a Point object from a "x,y" String
        .map(_.map(coords => {
          new Point(coords(0).toInt, coords(1).toInt)
        })) // Stream[Array[Point]]

        // from data representing [up, down, up, down, up, down] coordinates for lines,
        // build a [(up, down), (up, down), (up, down)] list, one tuple per line of text in the image
        .foldLeft(None.asInstanceOf[Option[Array[Point]]])((memory: Option[Array[Point]], pointList) => {
          memory match {
            case None => Some(pointList)
            case Some(memPointList) =>
              lines += ((memPointList, pointList))
              None
          }
        })
      lines.toList // List[(Array[Point], Array[Point])]
    }

    // building min and max X and Y values from the lines
    val rects = {
      def foldLeft2[A, T](a1: Array[T], a2: Array[T], initAcc: A, f: (A, T) => A): A = {
        a2.foldLeft(a1.foldLeft(initAcc)(f))(f)
      }

      def f(acc: (Int, Int, Int, Int), p: Point): (Int, Int, Int, Int) = {
        val (minX, minY, maxX, maxY) = acc
        val minX2 = if (minX == -1 || p.x < minX) p.x else minX
        val minY2 = if (minY == -1 || p.y < minY) p.x else minY
        val maxX2 = if (maxX == -1 || p.x > maxX) p.x else maxX
        val maxY2 = if (maxY == -1 || p.y > maxY) p.x else maxY
        (minX2, minY2, maxX2, maxY2)
      }

      lines.map(line => {
        val (up, down) = line
        foldLeft2(up, down, (-1, -1, -1, -1), f)
      })
    } // List[(Int, Int, Int, Int)]

    // making polygons from 4 values (5% margin)
    val polygons = rects.map(rect => {
      val marginRate = 0.05
      val (minX, minY, maxX, maxY) = rect
      val (height, width) = (maxX - minX, maxY - minY)
      val minX2 = (minX - marginRate * height).toInt
      val minY2 = (minY - marginRate * width).toInt
      val maxX2 = (maxX + marginRate * height).toInt
      val maxY2 = (maxY + marginRate * width).toInt
      new Polygon(List(new Point(minX2, minY2), new Point(maxX2, maxY2)))
    }) // List[Polygon]

    println(answer)

    polygons
  }
}
