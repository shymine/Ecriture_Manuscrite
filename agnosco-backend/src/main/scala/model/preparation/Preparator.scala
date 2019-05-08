package model.preparation

import model.common.{Example, Page}

trait Preparator {
	def prepareData(page: Page): Iterable[Example]
}
