package model.recogniser.laia

import model.recogniser.Converter
import model.common.Example
import sun.reflect.generics.reflectiveObjects.NotImplementedException

/**
  * Laia constraints:
  * - 	must know the number of channel of the input images -> 3 if RGB, 1 if greyscale
  * - 	must know the height of the input images and all images
  * - 	must have the same height for the creation of the model
  * -	must know the number of output symbol
  */
class LaiaConverter extends Converter {
  var height : Int = -1

  /**
    * This should convert the images into images off the defined height
    * Put everything in png
    * Construct the correct transcription waned by laia
    * @param samples The samples to convert
    * @return The converted samples
    */
  override def convertData(samples: Iterable[Example]): Iterable[Example] = throw new NotImplementedException()

  def setHeight(h: Integer) : Unit = this.height = h
}
