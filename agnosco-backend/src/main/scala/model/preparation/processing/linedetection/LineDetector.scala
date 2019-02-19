package model.preparation.processing.linedetection

import model.preparation.types.Line

trait LineDetector {
  def detectLines : List[Line]
}