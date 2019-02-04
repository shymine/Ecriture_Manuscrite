package model.data.processing.linedetection

import model.data.types.Line

trait LineDetector {
  def detectLines : List[Line]
}