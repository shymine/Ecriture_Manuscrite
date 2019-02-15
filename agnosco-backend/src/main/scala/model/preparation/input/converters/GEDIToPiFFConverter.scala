package model.data.input.converters

import java.io.FileNotFoundException

import model.data.input.FormatException
import model.data.input.piff.{PiFF, PiFFElement, PiFFPage}
import model.data.input.{string2Language, string2ScriptType}
import org.xml.sax.SAXParseException

import scala.collection.mutable.ListBuffer
import scala.xml.{Node, NodeSeq, XML}

/** This class can read a GEDI file (an XML format) and convert it into a PiFF object. */
object GEDIToPiFFConverter extends PiFFConverter {
  // Finds a date in the document and adds it to the builder.
  private def findDate(xml : Node) : String = {
    val date = raw"(\d{1,2})/(\d{1,2})/(\d+)".r
    val value = xml \@ "GEDI_date"

    value match {
      case date(month, day, year) => value
      case _ => throw new FormatException("No date found")
    }
  }

  // Finds a source image filename in the document and adds it to the builder.
  private def findSourceImageFilename(xml : Node) : String = {
    val nodeSeq = xml \ "DL_DOCUMENT"

    val nslen = nodeSeq.length
    if (nslen == 0) throw new FormatException("No document found")
    if (nslen > 1) throw new FormatException("Several documents in one file")

    val xmldoc = nodeSeq.head

    val src = xmldoc \@ "src"
    if (src == "")
      throw new FormatException("No image linked to the file")

    src
  }

  // Finds the DL_PAGE XML objects in the document.
  private def findPages(xml : Node) : NodeSeq = {
    val pages = (xml \ "DL_DOCUMENT").head \ "DL_PAGE"
    if (pages.length == 0)
      throw new FormatException("No pages found in the document")

    pages
  }

  // Reads PiFFElement objects from DL_ZONE XML objects in the document.
  private def elementsFromZones(zones : NodeSeq)
  : List[PiFFElement] = {
    val elements = new ListBuffer[PiFFElement]
    zones.filter(z => (z \@ "gedi_type").equals("TextRegion")).foreach(z => {
      val contents = z \@ "contents"

      val scriptTypeStr = z \@ "script"
      val scriptType =
        string2ScriptType(scriptTypeStr)
          .getOrElse(
            throw new FormatException(
              s"Wrong ScriptType value : $scriptTypeStr"))

      val languageStr = z \@ "language"
      val language =
        string2Language(languageStr)
          .getOrElse(
            throw new FormatException(s"Wrong language value : $languageStr"))

      z \@ "polygon" match {
        case "" =>
          val col = (z \@ "col").toInt
          val row = (z \@ "row").toInt
          val width = (z \@ "width").toInt
          val height = (z \@ "height").toInt
          elements +=
            new PiFFElement(
              col, row, width, height, contents, scriptType, language)
        case polygon =>

      }
    })
    elements.toList
  }

  // Gets all the interesting data from a page in the XML document.
  private def readPage(page : Node) : PiFFPage = {
    val width = (page \@ "width").toInt
    val height = (page \@ "height").toInt

    val zones = page \ "DL_ZONE"
    if (zones.length == 0)
      throw new FormatException("No zones found in the page")

    new PiFFPage(width, height, elementsFromZones(zones))
  }

  override def toPiFF(filename : String) : Option[PiFF] = {
    try {
      val xml = XML.loadFile(filename)
      val date = findDate(xml)
      val src = findSourceImageFilename(xml)
      val pages = findPages(xml).map(readPage)
      Some(new PiFF(date, src, pages.toList))
    } catch {
      case e @ (_ : FileNotFoundException | _ : SAXParseException
                | _ : NullPointerException | _ : NumberFormatException
                | _ : FormatException) =>
        e.printStackTrace()
        None
    }
  }
}
