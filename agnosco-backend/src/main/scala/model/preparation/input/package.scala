package model.preparation

/** This subpackage parses the ground truths and makes PiFF objects out of them. */
package object input {
  // all the following classes are made to process the GEDI format
  sealed trait ScriptType
  final case object TypedScriptType extends ScriptType {
    override def toString: String = "typed"
  }
  final case object HandScriptType extends ScriptType {
    override def toString: String = "hand"
  }
  final case object OtherScriptType extends ScriptType {
    override def toString: String = "other"
  }
  final case object UnknownScriptType extends ScriptType {
    override def toString: String = "unknown"
  }

  def string2ScriptType(str : String) : Option[ScriptType] = {
    str match {
      case "typed" => Some(TypedScriptType)
      case "hand" => Some(HandScriptType)
      case "other" => Some(OtherScriptType)
      case "unknown" => Some(UnknownScriptType)
      case _ => None
    }
  }

  sealed trait Language
  final case object ArabicLanguage extends Language {
    override def toString: String = "arabic"
  }
  final case object EnglishLanguage extends Language {
    override def toString: String = "english"
  }
  final case object FrenchLanguage extends Language {
    override def toString: String = "french"
  }
  final case object OtherLanguage extends Language {
    override def toString: String = "other"
  }
  final case object UnknownLanguage extends Language {
    override def toString: String = "unknown"
  }

  def string2Language(str : String) : Option[Language] = {
    str match {
      case "arabic" => Some(ArabicLanguage)
      case "english" => Some(EnglishLanguage)
      case "french" => Some(FrenchLanguage)
      case "other" => Some(OtherLanguage)
      case "unknown" => Some(UnknownLanguage)
      case _ => None
    }
  }
}
