package model.preparation

import model.common.Example

trait Processing {

	def prepareData(imgFiles: Iterable[String], vtFiles: Iterable[String]): Iterable[Example]
}
