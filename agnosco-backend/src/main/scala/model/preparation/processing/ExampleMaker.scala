package model.preparation.processing

import model.common.Example

trait ExampleMaker {
  def makeExamples() : List[Example]
}
