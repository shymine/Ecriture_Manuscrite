package model.recogniser

import model.common.Example

abstract class ConverterRecogniser extends Recogniser {

  protected var converter: Converter

  override def export(samples: Iterable[Example]): Unit

}

