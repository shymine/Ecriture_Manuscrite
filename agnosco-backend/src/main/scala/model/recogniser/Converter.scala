package main.scala.model.recogniser

import model.common.Example

/**
  * A two way converter between PiFF format and the input format
  * of the Recognizer used
  */
trait Converter {
  /**
    * Convert the samples from PiFF to the format of the recognizer
    * @param samples The samples to convert
    * @return The converted samples
    */
  def convertData(samples: Iterable[Example]) : Iterable[Example]

  /**
    * Convert the samples from the format of the Recognizer to PiFF
    * @param samples The samples to convert
    * @return The converted samples
    */
  def unconvertData(samples: Iterable[Example]) : Iterable[Example]
}