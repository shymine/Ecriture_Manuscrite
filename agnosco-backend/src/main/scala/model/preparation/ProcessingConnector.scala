package model.preparation

import model.ImplFactory
import model.common.Example

class ProcessingConnector {

	private var impl = ImplFactory.processingImpl

	def prepareData(groundTruthFiles: Iterable[String]): Iterable[Example] =
		impl.prepareData(groundTruthFiles)
}
