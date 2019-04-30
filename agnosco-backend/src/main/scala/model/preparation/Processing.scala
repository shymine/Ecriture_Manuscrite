package model.preparation

import model.common.{Example, Page}

trait Processing {

	def prepareData(page: Page): Iterable[Example]
}
