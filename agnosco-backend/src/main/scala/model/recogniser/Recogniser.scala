package model.recogniser

import model.common.Example

/**
  * The trait of a recognizer and the actions it can perform
  */
trait Recogniser {
  /**
    * Export the examples to a given format
    * @param samples The examples to export
    */
  def export(samples: Iterable[Example]): Unit
}
