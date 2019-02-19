package model.preparation.processing.linedetection

import model.preparation.types.Line

trait LineDetector {
  def detectLines(src : String) : List[Line]
}