package model.preparation

import model.ImplFactory
import model.common.{Example, Page}

class PreparatorConnector {

	private var impl = ImplFactory.preparatorImpl

	def prepareData(page: Page): Iterable[Example] =
		impl.prepareData(page)
}