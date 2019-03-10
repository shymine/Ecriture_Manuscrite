package model.common


case class Project(id : Long, name : String, var recogniser : RecogniserType.Value, documents : Iterable[Document])
