package model.data.processing

import model.common.Example

trait ExampleMaker {
  def makeExamples() : List[Example]
}
