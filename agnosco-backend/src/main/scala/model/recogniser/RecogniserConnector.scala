package model.recogniser

import model.ImplFactory
import model.common.Example
import model.recogniser.laia.LaiaRecogniser

class RecogniserConnector {

	private var impl = ImplFactory.recogniserImpl

	def export(samples: Iterable[Example]): Unit = impl.export(samples)

	def changeRecogniser(name: String ) : Unit = {
		name match {
			case "Default" => impl = new SampleExport
			case "LaiaRecogniser" => impl = new LaiaRecogniser
		}
//		val classe = Class.forName(name)
//		impl = classe.newInstance().asInstanceOf[Recogniser]
	}
}
