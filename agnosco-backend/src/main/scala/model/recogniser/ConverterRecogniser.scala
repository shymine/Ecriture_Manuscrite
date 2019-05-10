package model.recogniser

import model.common.Example

/**
  * Conbine the Recogniser Trait but convert the samples using a Converter
  */
abstract class ConverterRecogniser extends Recogniser {
  /**
    * The converter to use for this recogniser
    */
  protected val converter: Converter

  /**
    * Exports the samples to the correct format and with the correct configuration files
    * @param samples The examples to export
    */
  override def export(samples: Iterable[Example]): Unit

}

