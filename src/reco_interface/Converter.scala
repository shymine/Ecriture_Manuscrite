package reco_interface

import java.util
import utils.Sample

trait Converter {

	def convertData(samples: util.Collection[Sample])
}
