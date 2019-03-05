package model.preparation
import model.common.Example

class ProcessingImpl extends Processing {
	override def prepareData(imgFiles: Iterable[String], vtFiles: Iterable[String]): Iterable[Example] = ???
}
