package model.data.processing

import model.common.Example
import model.data.input.piff.PiFF

abstract class ExampleMaker(p : PiFF) {
  private val piff = p

  def makeExamples() : List[Example]
}
