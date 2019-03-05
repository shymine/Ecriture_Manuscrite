package model.preparation

import model.ImplFactory
import model.common.Example

class ProcessingConnector {

	private var impl = ImplFactory.processingImpl

	def prepareData(imgFiles: Iterable[String], vtFiles: Iterable[String]): Iterable[Example] = impl.prepareData(imgFiles, vtFiles)
}
