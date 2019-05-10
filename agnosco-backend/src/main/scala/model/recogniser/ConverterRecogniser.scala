package model.recogniser

import model.common.Example

/**
  * Conbine the Recogniser Trait but convert the samples using a Converter
  */
abstract class ConverterRecogniser extends Recogniser {

  protected val converter: Converter

  override def export(samples: Iterable[Example]): Unit

}

