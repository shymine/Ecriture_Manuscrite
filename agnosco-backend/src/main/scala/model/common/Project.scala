package model.common

import model.recogniser.Recogniser

import scala.collection.mutable.ArrayBuffer

case class Project(id : Int, name : String, var recogniser : Recogniser, documents : ArrayBuffer[Document])
