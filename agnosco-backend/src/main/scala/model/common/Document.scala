package model.common


case class Document(id : Int, name : String, pages : Iterable[Page], prepared: Boolean)