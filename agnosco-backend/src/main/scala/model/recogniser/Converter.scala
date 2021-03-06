package model.recogniser

import model.common.Example

/**
  * A two way converter between PiFF format and the input format
  * of the Recognizer used
  */
trait Converter {
  /**
    * Convert the samples from PiFF to the format of the recognizer
    * (if a certain height is needed or if jpeg is prefered instead of png, etc)
    * @param samples The samples to convert
    * @return The converted samples
    */
  def convertData(samples: Iterable[Example]) : Iterable[Example]
}
