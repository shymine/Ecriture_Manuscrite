package model.common

case class Page(id : Long, imagePath : String, groundTruthPath : String, examples : Iterable[Example])