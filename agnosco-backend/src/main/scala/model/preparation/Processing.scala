package model.preparation

import model.common.Example

trait Processing {

	def prepareData(groundTruthFiles: Iterable[String]): Iterable[Example]
}
