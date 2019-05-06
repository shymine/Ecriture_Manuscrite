package model.recogniser

import model.ImplFactory
import model.common.Example
import org.json.JSONObject
import model.recogniser._
import model.recogniser.laia.LaiaRecogniser

class RecogniserConnector {

	private var impl = ImplFactory.recogniserImpl

	def trainAI(samples: Iterable[Example]): Unit = impl.train(samples)

	def evaluateAI(samples: Iterable[Example]): JSONObject = impl.evaluate(samples)

	def recognizeAI(samples: Iterable[Example]) : Iterable[Example] = impl.recognize(samples)

	def changeRecogniser(name: String ) : Unit = {
		name match {
			case "None" => impl = new SampleExport
			case "LaiaRecogniser" => impl = new LaiaRecogniser
		}

//		val classe = Class.forName(name)
//		impl = classe.newInstance().asInstanceOf[Recogniser]
	}
}
