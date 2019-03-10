package model.common


case class Document(id : Long, name : String, pages : Iterable[Page], prepared: Boolean)