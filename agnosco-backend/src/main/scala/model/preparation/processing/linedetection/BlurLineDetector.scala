package model.preparation.processing.linedetection

import java.io._
import java.net.Socket
import java.nio.file.Files
import java.util.Base64

import model.preparation.types.{Line, Point, Polygon}

import scala.collection.mutable.{ArrayBuffer, ListBuffer}

class BlurLineDetector(detectorIp : String, filePort : Int, answerPort : Int) extends LineDetector {
  private def map3[A, B](l: List[A], f1 : A => B, f: (A, A, A) => B, fn : A => B): List[B] = {
    def mapEnd(l: List[A]): List[B] = {
      l match {
        case List(x) => List.empty
        case List(_, y) => List(fn(y))
        case x :: y :: z :: r =>
          f(x, y, z) :: mapEnd(y :: z :: r)
        case _ => throw new IllegalArgumentException("map3")
      }
    }

    def mapBegin(l: List[A]) : List[B] = {
      l match {
        case List.empty => List.empty
        case List(x, y, z) => List(f1(x), f(x, y, z), fn(z))
        case x :: _ => f1(x) :: mapEnd(l)
      }
    }

    mapBegin(l)
  }
  /*
  *  x => f1(x)
  *  x, y => f1(x), fn(y)
  *  x, y, z => f1(x), f(x, y, z), fn(z)
  *  x, y, z, t => f1(x), f(x, y, z), f(y, z, t), fn(t)
  * */

  override def detectLines(src : String) : List[Line] = {
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
        .map(_.split(':')(1)) // Stream[String]

        // get a list of "x,y" points from a "x1,y1;x2,y2;x3,y3..." String
        .map(_.split(';')) // Stream[Array[String]]

        // in each list, split "x,y" into List("x", "y")
        .map(_.map(_.split(';'))) // Stream[Array[Array[String]]]

        // get a Point object from a "x,y" String
        .map(_.map(coords => {
          new Point(coords(0).toInt, coords(1).toInt)
        })) // Stream[Array[Point]]

        // from data representing [up, down, up, down, up, down] coordinates for lines,
        // build a [(up, down), (up, down), (up, down)] list, one tuple per line of text in the image
        .foldLeft(None[Array[Point]])((memory: Option[Array[Point]], pointList) => {
          memory match {
            case None => Some(pointList)
            case Some(memPointList) =>
              lines += ((memPointList, pointList))
              None
          }
        })
      lines.toList // List[(Array[Point], Array[Point])]
    }

    // TODO : (rectangle definining the area in the image for each line)
    val means = {
      def f1(up: Array[Point], down: Array[Point]): (Int, Int, Int, Int) = {
        (1, 1, 1, 1)
      }

      lines.map((up, down) => {
        f1(up, down)
      })
    }

    val rects = {
      val rects = ArrayBuffer.empty[Polygon]
      for (i <- 1 until lines.length - 1) {
        val lineI = lines(i)

      }
    }

    println(answer)

    List.empty[Line]
  }
}