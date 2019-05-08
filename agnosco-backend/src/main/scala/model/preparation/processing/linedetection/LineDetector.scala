package model.preparation.processing.linedetection

import model.preparation.types.Polygon

trait LineDetector {
  def detectLines(src : String) : List[Polygon]
}