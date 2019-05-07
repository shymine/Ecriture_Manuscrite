package model.recogniser

import model.ImplFactory
import model.common.Example
import org.json.JSONObject
import model.recogniser._
import model.recogniser.laia.LaiaRecogniser

class RecogniserConnector {

	private var impl = ImplFactory.recogniserImpl

	def export(samples: Iterable[Example]): Unit = impl.export(samples)

	def changeRecogniser(name: String ) : Unit = {
		name match {
			case "None" => impl = new SampleExport
			case "LaiaRecogniser" => impl = new LaiaRecogniser
		}

//		val classe = Class.forName(name)
//		impl = classe.newInstance().asInstanceOf[Recogniser]
	}
}
