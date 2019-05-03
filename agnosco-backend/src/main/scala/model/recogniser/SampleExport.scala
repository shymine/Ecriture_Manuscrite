package model.recogniser
import java.io.FileOutputStream

import model.common.Example
import org.json.JSONObject

class SampleExport extends ConverterRecogniser {
  override protected var converter: Converter = _

  /**
    * Train the Recognizer using the samples given in parameter
    *
    * @param samples The samples the Recognizer trains on
    */
  override def train(samples: Iterable[Example]): Unit = {
    /*
    ecrire dans un fichier, l'image et dans un autre la transcription et cela pour chaque exemple (Exemple) de samples
    dans le repertoire export
  */

    samples.foreach(example => {
      val exName = example.id
      val fileIm = new FileOutputStream("export/image.png")
      val fileTr = new FileOutputStream("export/image.png")

    })
//    try {
//      val imgByte = javax.xml.bind.DatatypeConverter.parseBase64Binary(res)
//
//      val out = new FileOutputStream("data/image.tiff")
//      out.write(imgByte)
//      out.close()
//
//    } catch {
//      case e: Exception => e.printStackTrace()
//    }
  }


  /**
    * Evaluate the Recognizer performance on a test set of Sample
    *
    * @param samples The set to test the Recognizer
    */
  override def evaluate(samples: Iterable[Example]): JSONObject = throw new NotImplementedError()

  /**
    * Production mode of the Recognizer
    *
    * @param samples The samples the Recognizer must "translate".
    *                It's format as a list to ease the process of
    *                knowing the correct translation to the image
    * @return Returns the List of the sample corresponding to the
    *         image and the translation. If the input format is a
    *         paragraph then there may be n samples extracted from
    *         it depending if the Recognizer output for a paragraph
    *         is an output per line or one output for the paragraph.
    */
  override def recognize(samples: Iterable[Example]): Iterable[Example] = throw new NotImplementedError()
}
