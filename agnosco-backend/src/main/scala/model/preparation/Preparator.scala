package model.preparation

import model.common.{Example, Page}

trait Preparator {
	def prepareData(page: Page): Iterable[Example]
	def setConnectionData(ip: String, port1: Int, port2: Int): Unit
}
