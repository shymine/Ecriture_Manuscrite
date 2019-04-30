package model.preparation
import model.common.{Example, Page}
import model.preparation.input.PiFFReader
import model.preparation.processing.BaseExampleMaker

class ProcessingImpl extends Processing {
	override def prepareData(page: Page): Iterable[Example] = {
		// ground truth to PiFF
		val piffOpt = PiFFReader.fromFile(page.groundTruth)
		// PiFF to examples
		piffOpt match {
			case Some(piff) => BaseExampleMaker.makeExamples(piff)
			case _ => List.empty
		}
	}
}
