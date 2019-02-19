package model.preparation.processing

import model.common.Example
import model.preparation.input.piff.PiFF

class BaseExampleMaker(p : PiFF) extends ExampleMaker {
  override def makeExamples(): List[Example] = ???
}
