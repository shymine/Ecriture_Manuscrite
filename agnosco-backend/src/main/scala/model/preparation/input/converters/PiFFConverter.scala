package model.preparation.input.converters

import model.preparation.input.piff.PiFF

trait PiFFConverter {
  /** Converts the object into an optional PiFF object. */
  def toPiFF(filename : String) : Option[PiFF]
}