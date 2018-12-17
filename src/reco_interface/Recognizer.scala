package reco_interface
import scala.collection.Iterable

import utils.Sample

/**
  * The trait of a recognizer and the actions it can perform
  */
trait Recognizer {
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
	  * @return Returns the translation format as a list to link the
	  *         result to the samples given
	  */
	def recognize(samples: List[Sample]) : List[String]

	/**
	  * Allows to change the recognizer by another
	  * @param path The path where the Recognizer to use is
	  */
	def changeRecognizer(path: String)
}
