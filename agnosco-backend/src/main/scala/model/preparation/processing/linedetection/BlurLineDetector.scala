package model.preparation.processing.linedetection

import java.io._
import java.net.{InetSocketAddress, Socket}
import java.nio.channels.SocketChannel

import model.preparation.types.Line

class BlurLineDetector(detectorIp : String, filePort : Int, answerPort : Int) extends LineDetector {
  override def detectLines(src : String) : List[Line] = {

    val fileSocketChannel = SocketChannel.open(new InetSocketAddress(detectorIp, filePort))
    val answerSocket = new Socket(detectorIp, answerPort)

    // send the file to the line detector
    val fileChannel = new FileInputStream(src).getChannel
    fileChannel.transferTo(0, fileChannel.size(), fileSocketChannel)
    fileChannel.force(true)
    fileChannel.close()
    fileSocketChannel.close()
    println("[INFO] File sent to the line detector")

    // read the answer from the server
    val answerReader = new BufferedReader(new InputStreamReader(answerSocket.getInputStream))
    val answer = answerReader.readLine() + answerReader.readLine()
    answerSocket.close()
    println("[INFO] Answer received from the line detector")

    // operations on the answer to get Line objects
    // TODO

    List.empty[Line]
  }
}