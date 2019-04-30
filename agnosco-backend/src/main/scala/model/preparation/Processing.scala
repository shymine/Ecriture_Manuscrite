package model.preparation

import model.common.Example

trait Processing {

	def prepareData(groundTruthFiles: Iterable[String], images64: Iterable[String]): Iterable[Example]
}
