package model.data.input.converters

import model.data.input.piff.PiFF

trait PiFFConverter {
  /** Converts the object into an optional PiFF object. */
  def toPiFF(filename : String) : Option[PiFF]
}