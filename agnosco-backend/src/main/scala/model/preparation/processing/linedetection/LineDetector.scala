package model.preparation.processing.linedetection

import model.preparation.types.Polygon

/** This trait defines a line detector. */
trait LineDetector {
  /** Detects lines in a paragraph, and gets the polygons surrounding them.
    * @param src the filename of the paragraph image
    * @return the list of polygons
    */
  def detectLines(src : String) : List[Polygon]
}