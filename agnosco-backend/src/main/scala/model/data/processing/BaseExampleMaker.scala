package model.data.processing

import model.common.Example
import model.data.input.piff.PiFF

class BaseExampleMaker(p : PiFF) extends ExampleMaker {
  override def makeExamples(): List[Example] = ???
}
