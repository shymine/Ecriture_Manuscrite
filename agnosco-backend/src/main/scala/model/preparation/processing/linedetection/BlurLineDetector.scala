package model.preparation.processing.linedetection

import java.io._
import java.net.Socket
import java.nio.file.Files
import java.util.Base64

import model.preparation.types.Line

class BlurLineDetector(detectorIp : String, filePort : Int, answerPort : Int) extends LineDetector {
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

    // operations on the answer to get Line objects
    // TODO : answerBytes

    // TODO : peut-être passer par le controller pour demander la base64 directement au lieu de lire un fichier

    List.empty[Line]
  }
}