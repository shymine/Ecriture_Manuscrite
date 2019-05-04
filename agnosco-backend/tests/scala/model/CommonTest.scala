package model

import model.common.{Example, Page}
import org.scalatest.FlatSpec

class CommonTest extends FlatSpec {


	"page" should "give a nice json" in {
		val page = Page(-1,"AFGHJK1.piff", List())
		println(page.toJSON.toString())
	}
}
