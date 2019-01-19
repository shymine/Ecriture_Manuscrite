package reco_interface.laia

import reco_interface.source.Converter
import utils.Sample

/**
  * Laia constraints:
  * - 	must know the number of channel of the input images
  * - 	must know the height of the input images and all images
  *   		must have the same height for the creation of the model
  * -	must know the number of output symbol
  */
class laiaConverter extends Converter {

	override def convertData(samples: Iterable[Sample]): Iterable[Sample] = ???

	override def unconvertData(samples: Iterable[Sample]): Iterable[Sample] = ???
}
