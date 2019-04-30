package model.preparation

import model.ImplFactory
import model.common.Example

class ProcessingConnector {

	private var impl = ImplFactory.processingImpl

	def prepareData(groundTruthFiles: Iterable[String], images64: Iterable[String]): Iterable[Example] =
		impl.prepareData(groundTruthFiles, images64)
}
