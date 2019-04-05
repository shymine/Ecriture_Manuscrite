package model

import org.scalatest.FlatSpec

class Some_Tests extends FlatSpec {

	"test directory command" should "work" in {
		val path = getClass.getResource("../").getPath
		println(path)
	}

}
