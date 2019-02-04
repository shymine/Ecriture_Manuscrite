package model.recogniser

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
  def convertData(samples: Iterable[Sample]) : Iterable[Sample]

  /**
    * Convert the samples from the format of the Recognizer to PiFF
    * @param samples The samples to convert
    * @return The converted samples
    */
  def unconvertData(samples: Iterable[Sample]) : Iterable[Sample]
}