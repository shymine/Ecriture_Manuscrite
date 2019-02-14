package model.data.processing.linedetection

import java.io.{BufferedReader, DataOutputStream, IOException, InputStreamReader}
import java.net.Socket

import model.data.types.Line

import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer
import scala.io.Source

object BlurLineDetector extends LineDetector {
  private val socket = new Socket("localhost", 7007)
  private val input = new BufferedReader(new InputStreamReader(socket.getInputStream))
  private val output = new DataOutputStream(socket.getOutputStream)

  private def readServerAnswer() : List[String] = {
    val buf = new ListBuffer[String]
    @tailrec def f() : List[String] = {
      try {
        buf += input.readLine()
        f()
      } catch {
        case e : IOException => buf.toList
      }
    }
    f()
  }

  override def detectLines(src : String) : List[Line] = {
    val fileContent = Source.fromFile(src).getLines().foldLeft("")((acc, line) => acc ++ line)
    output.writeBytes(fileContent)
    val answer = readServerAnswer()
    // TODO : parse answer
  }
}
