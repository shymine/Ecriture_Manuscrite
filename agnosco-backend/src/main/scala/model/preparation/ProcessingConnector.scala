package model.preparation

import model.ImplFactory
import model.common.{Example, Page}

class ProcessingConnector {

	private var impl = ImplFactory.processingImpl

	def prepareData(page: Page): Iterable[Example] =
		impl.prepareData(page)
}
