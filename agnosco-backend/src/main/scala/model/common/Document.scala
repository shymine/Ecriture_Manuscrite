package model.common

import scala.collection.mutable.ArrayBuffer

case class Document(id : Int, name : String, pages : ArrayBuffer[Page], prepared: Boolean)