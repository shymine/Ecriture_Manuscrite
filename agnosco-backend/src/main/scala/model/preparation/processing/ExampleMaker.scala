package model.preparation.processing

import model.common.Example
import model.preparation.input.piff.PiFF

trait ExampleMaker {
  def makeExamples(p : PiFF) : List[Example]
}
