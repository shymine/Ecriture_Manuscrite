package model.recogniser.laia

import model.recogniser.{Converter, ConverterRecogniser}
import model.common.Example

/**
  * The laia connector will be in charge of using the Laia recognizer to perform recognition
  * Laia should be installed beforehand using docker
  */
class LaiaRecogniser extends ConverterRecogniser{

	override protected val converter: Converter = new LaiaConverter

	override def export(samples: Iterable[Example]): Unit = throw new NotImplementedError()

}
