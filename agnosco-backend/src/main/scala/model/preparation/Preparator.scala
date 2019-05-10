package model.preparation

import model.common.{Example, Page}

/** This trait defines a class able to build examples from document pages. */
trait Preparator {
	/** Builds examples from a document page stored in memory.
		* @param page the document page
		* @return a list of examples
		*/
	def prepareData(page: Page): Iterable[Example]
}
