package model.preparation
import model.common.Example
import model.preparation.input.PiFFReader
import model.preparation.processing.BaseExampleMaker

import scala.collection.mutable.ListBuffer

class ProcessingImpl extends Processing {
	override def prepareData(groundTruthFiles: Iterable[String], images64: Iterable[String]): Iterable[Example] = {
		val examples = new ListBuffer[Example]

		// ground truth to PiFF
		val piffs =
			groundTruthFiles
				.map(filename => PiFFReader.fromFile(filename))
				.map(o => o.get)

		piffs.foreach(piff => {
			// PiFF to examples
			examples ++= BaseExampleMaker.makeExamples(piff)
		})

		examples.toList
	}
}
