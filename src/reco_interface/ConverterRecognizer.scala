package reco_interface
import utils.Sample

class ConverterRecognizer(val converter: Converter) extends Recognizer {
	/**
	  * Train the Recognizer using the samples given in parameter
	  *
	  * @param samples The samples the Recognizer trains on
	  */
	override def train(samples: Iterable[Sample]): Unit = {
		val convertedSamples = converter.convertData(samples)

	}

	/**
	  * Evaluate the Recognizer performance on a test set of Sample
	  *
	  * @param samples The set to test the Recognizer
	  */
	override def evaluate(samples: Iterable[Sample]): Unit = ???

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
	override def recognize(samples: List[Sample]): List[Sample] = ???

	/**
	  * Allows to change the recognizer by another
	  *
	  * @param path The path where the Recognizer to use is
	  */
	override def changeRecognizer(path: String): Unit = ???
}
