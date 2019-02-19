package model.common

import scala.collection.mutable.ArrayBuffer

case class Page(id : Int, imagePath : String, groundTruthPath : String, examples : ArrayBuffer[Example])