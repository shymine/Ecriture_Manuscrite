package model

import java.util.Calendar

import org.scalatest.FlatSpec

class Some_Tests extends FlatSpec {

	"test directory command" should "work" in {
		val path = getClass.getResource("../").getPath
		println(path)
	}

	"test Calendar" should "be ok" in {
		val tmp = Calendar.getInstance().getTime
		println("[ ]".r.replaceAllIn(tmp.toString, "_"))

	}

}
