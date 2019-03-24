package scala.model

import java.io.{File, PrintWriter}

import model.preparation.input.{ArabicLanguage, TypedScriptType}
import model.preparation.input.converters.GEDIToPiFFConverter
import model.preparation.input.piff.{PiFF, PiFFElement, PiFFPage}
import org.scalatest.FlatSpec

class InputTest extends FlatSpec {
  private val gediTest =
    <GEDI xmlns="http://lamp.cfar.umd.edu/media/projects/GEDI/" GEDI_version="2.3.19" GEDI_date="12/23/2011">
      <USER name="mohamed salah" date="random useless date" dateFormat="mm/dd/yyyy hh:mm">	</USER>
      <DL_DOCUMENT src="image.tif" docTag="xml" NrOfPages="1">
        <DL_PAGE gedi_type="DL_PAGE" src="useless.tif" pageID="1" width="1700" height="2338" NumPage="1" Language_4="" Receiver_Name="" Language_3="" Receiver_Company="" Receiver_Address="" Sender_Company="" Receiver_Fax="" Document_Location="" Receiver_Phone="" Sender_Fax="" Category="C1" Language_1="arabic" Language_2="english" Script_1="typed" Script_2="hand" Document_Date="07/01/2012">
          <DL_ZONE gedi_type="NoiseRegion" id="1" col="659" row="94" width="61" height="76"> </DL_ZONE>
          <DL_ZONE gedi_type="GraphicRegion" id="2" col="722" row="149" width="312" height="138" function="logo"> </DL_ZONE>
          <DL_ZONE gedi_type="TextRegion" id="4" col="414" row="189" width="253" height="57" contents="سلام" script="typed" language="arabic"> </DL_ZONE>
          <DL_ZONE gedi_type="TextRegion" id="127" col="793" row="1855" width="155" height="53" contents="سلام" script="typed" language="arabic"> </DL_ZONE>
          <DL_ZONE gedi_type="GraphicRegion" id="180" col="135" row="2177" width="429" height="13" function="underlined-field"> </DL_ZONE>
          <DL_ZONE gedi_type="NoiseRegion" id="411" polygon="(832,6);(1693,23);(1691,3);(864,1)"> </DL_ZONE>
        </DL_PAGE>
      </DL_DOCUMENT>
    </GEDI>

  private val expectedPiFF =
    new PiFF("12/23/2011", "image.tif",
      List(
        new PiFFPage(1700, 2338,
          List(
            new PiFFElement(414, 189, 253, 57, "سلام", TypedScriptType, ArabicLanguage),
            new PiFFElement(793, 1855, 155, 53, "سلام", TypedScriptType, ArabicLanguage)
          ))
      ))

  "GEDI -> PiFF converter" should "produce the expected PiFF file" in {
    val pw = new PrintWriter(new File("gediTest.xml"))
    pw.println(gediTest.toString())
    pw.close()

    val piff = GEDIToPiFFConverter.toPiFF("gediTest.xml")
    assert(piff.isDefined)

    assert(piff.get.equals(expectedPiFF))

    new File("gediTest.xml").delete()
  }

}
