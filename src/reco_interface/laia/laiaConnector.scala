package reco_interface.laia

import reco_interface.source.ConverterRecognizer
import reco_interface.source.Converter
import utils.Sample

/**
  * The laia connector will be in charge of using the Laia recognizer to perform recognition
  * @param converter The converter from the Sample to the laia input
  */
class laiaConnector(converter: Converter) extends ConverterRecognizer(converter){

	override def train(samples: Iterable[Sample]): Unit = ???

	override def evaluate(samples: Iterable[Sample]): Unit = ???

	override def recognize(samples: List[Sample]): List[Sample] = ???

	override def changeRecognizer(recognizerPath: String, converter: Converter): Unit = ???
}
