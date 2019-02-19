package model.common

import model.preparation.input.piff.PiFF

import scala.collection.mutable.ArrayBuffer

class Page(val id : Int, val imagePath : String, val groundTruthPath : String, val examples : ArrayBuffer[Example]) {

}
