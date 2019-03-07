package model.common


case class Project(id : Int, name : String, var recogniser : RecogniserType.Value, documents : Iterable[Document])
