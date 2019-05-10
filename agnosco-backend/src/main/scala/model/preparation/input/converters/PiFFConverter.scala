package model.preparation.input.converters

import model.preparation.input.piff.PiFF

/** Defines a converter from an arbitrary format into the PiFF format. */
trait PiFFConverter {
  /** Converts the provided file into an optional PiFF object.
    * @param filename the file path
    * @return an optional PiFF object
    */
  def toPiFF(filename : String) : Option[PiFF]
}