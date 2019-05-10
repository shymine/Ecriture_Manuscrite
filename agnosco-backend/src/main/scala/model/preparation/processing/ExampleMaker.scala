package model.preparation.processing

import model.common.Example
import model.preparation.input.piff.PiFF

/** This trait represents a class building training examples. */
trait ExampleMaker {
  /** Makes examples from a PiFF object.
    * @param p the PiFF object we want to export
    * @return a list of examples made from it
    */
  def makeExamples(p : PiFF) : List[Example]
}
