package model.recogniser

import model.common.Example
import org.json.JSONObject

abstract class ConverterRecogniser extends Recogniser {

  protected var converter: Converter

  /**
    * Train the Recognizer using the samples given in parameter
    *
    * @param samples The samples the Recognizer trains on
    */
  override def train(samples: Iterable[Example]): Unit

  /**
    * Evaluate the Recognizer performance on a test set of Sample
    *
    * @param samples The set to test the Recognizer
    */
  override def evaluate(samples: Iterable[Example]): JSONObject

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
  override def recognize(samples: Iterable[Example]): List[Example]

  /**
    * Allow the user to change the type of his recogniser
    * @param recognizerPath The path of the recognizer to use
    * @param converter The converter refering to the right recogniser
    */
  def changeRecogniser(recognizerPath: String, converter: Converter): Unit
}

