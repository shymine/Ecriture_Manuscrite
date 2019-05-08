package model.preparation

import model.common.{Example, Page, globalDataFolder}
import model.preparation.input.PiFFReader
import model.preparation.processing.{BaseExampleMaker, ExampleMaker}

class PreparatorImpl extends Preparator {
	var exampleMaker: ExampleMaker = _

	override def setConnectionData(ip: String, port1: Int, port2: Int): Unit = {
		exampleMaker = new BaseExampleMaker(ip, port1, port2)
	}

	override def prepareData(page: Page): Iterable[Example] = {
		// ground truth to PiFF
		val piffOpt = PiFFReader.fromFile(globalDataFolder+"/"+page.groundTruth)
		// PiFF to examples
		piffOpt match {
			case Some(piff) => exampleMaker.makeExamples(piff)
			case _ => List.empty
		}
	}
}
