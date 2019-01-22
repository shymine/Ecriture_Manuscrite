package reco_interface.laia

import reco_interface.source.ConverterRecognizer
import reco_interface.source.Converter
import utils.Sample
import sys.process._

/**
  * The laia connector will be in charge of using the Laia recognizer to perform recognition
  * Laia should be installed beforehand using docker
  * @param converter The converter from the Sample to the laia input
  */
class laiaConnector(converter: Converter, val modelPath: String, val height: Integer) extends ConverterRecognizer(converter){

	/**
	  * Laia needs all images to be the same height
	  * need to store in different files:
	  * 	the list of image for training
	  * 	the list of training transcripts
	  * 	the list of images for validation
	  * 	the list of validation transcripts
	  * @param samples The samples the Recognizer trains on
	  */
	override def train(samples: Iterable[Sample]): Unit = {
		val convertedData = this.converter.convertData(samples)

	}

	/**
	  * Need to recognize and then compare the result it gives with the real transcriptions
	  * @param samples The set to test the Recognizer
	  */
	override def evaluate(samples: Iterable[Sample]): Unit = ???

	/**
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
	  * Concerning Laia, changing the recognizer while using laia
	  * only means to change the language laia will be recognizing
	  * @param recognizerPath
	  * @param converter
	  */
	override def changeRecognizer(recognizerPath: String, converter: Converter): Unit = ???

	def init(channel: Integer, symbol: Integer) = {
		val ret: Integer = s"laia-docker create-model $channel $height $symbol $modelPath" !
		if(ret != 0) {
			throw new RuntimeException("problem with creation of the model")
		}
	}

}
