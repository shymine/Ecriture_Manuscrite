package model.recogniser

import utils.Sample

/**
  * The trait of a recognizer and the actions it can perform
  */
trait Recogniser {
  /**
    * Train the Recognizer using the samples given in parameter
    * @param samples The samples the Recognizer trains on
    */
  def train(samples: Iterable[Sample])

  /**
    * Evaluate the Recognizer performance on a test set of Sample
    * @param samples The set to test the Recognizer
    */
  def evaluate(samples: Iterable[Sample])

  /**
    * Production mode of the Recognizer
    * @param samples The samples the Recognizer must "translate".
    *                It's format as a list to ease the process of
    *                knowing the correct translation to the image
    * @return Returns the List of the sample corresponding to the
    *         image and the translation. If the input format is a
    *         paragraph then there may be n samples extracted from
    *         it depending if the Recognizer output for a paragraph
    *         is an output per line or one output for the paragraph.
    */
  def recognize(samples: List[Sample]) : List[Sample]

}
