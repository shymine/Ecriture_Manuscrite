package model.common

import model.recogniser.Recogniser

import scala.collection.mutable.ArrayBuffer

class Project(val id : Int, val name : String, var recogniser : Recogniser, val documents : ArrayBuffer[Document]) {
}
