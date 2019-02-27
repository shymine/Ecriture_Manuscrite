package model.preparation.processing.linedetection

import java.io._
import java.net.{InetSocketAddress, Socket}
import java.nio.channels.{ServerSocketChannel, SocketChannel}

import model.preparation.types.Line

import scala.collection.mutable.ListBuffer
import scala.io.Source

class BlurLineDetector(backendIp : String, detectorIp : String, port : Int) extends LineDetector {
  override def detectLines(src : String) : List[Line] = {
    // send the file to the line detector
    val sc = SocketChannel.open(new InetSocketAddress(detectorIp, port))
    val fc = new FileInputStream(src).getChannel
    fc.transferTo(0, fc.size(), sc)
    fc.force(true)
    fc.close()
    sc.close()
    println("[INFO] File sent to the line detector")

    // read the answer from the server into a 'tmp' file
    val ss = ServerSocketChannel.open()
    ss.bind(new InetSocketAddress(backendIp, port))
    println("[INFO] Waiting for the answer...")

    val scAnswer = ss.accept()
    val fcAnswer = new FileOutputStream("tmp").getChannel
    fcAnswer.transferFrom(sc, 0, Long.MaxValue)
    fcAnswer.force(true)
    fcAnswer.close()
    scAnswer.close()
    println("[INFO] Answer received from the line detector")

    val answer = Source.fromFile("tmp").getLines()
    println(answer)

    // delete the temporary file
    new File("tmp").delete()

    /*// read file corresponding to 'src' path into a buffer
    val inputFile = new File(src)
    val buffer = new Array[Byte](inputFile.length().asInstanceOf[Int])
    val bis = new BufferedInputStream(new FileInputStream(inputFile))
    bis.read(buffer, 0, buffer.length)
    println("[INFO] File read into a buffer")

    val fileSizeAsArray = BigInt(buffer.length).toByteArray // buffer length as Array[Byte]

    // open socket and write data to it
    val socket = new Socket(host, port)
    val os = socket.getOutputStream
    os.write(fileSizeAsArray)
    os.write(buffer, 0, buffer.length)
    os.flush()
    println("[INFO] File sent to the line detector")*/

    /*// read server answer
    val is = new BufferedReader(new InputStreamReader(socket.getInputStream))
    val answerBuffer = new ListBuffer[String]

    var inputOk = true
    while (inputOk) {
      try {
        answerBuffer += is.readLine()
      } catch {
        case e : IOException => inputOk = false
      }
    }*/

    //println("[INFO] Answer received from the line detector")

    // operations on the answer to get Line objects
    // TODO

    List.empty[Line]
  }
}