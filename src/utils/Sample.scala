package utils

/**
  * The class representing the data samples in the database.
  * @param path The path leading to the image
  * @param translation The current translation associated to the image
  */
class Sample(private val path: String, private var translation: String) {
	/**
	  * Give the path leading to the image of the sample
	  * @return The path of the image
	  */
	def getPath: String = path

	/**
	  * Give the translation associated with the image
	  * @return Returns a copy of the translation
	  */
	def getTranslation: String = new String(translation)

	/**
	  * Set the translation with a copy of the given String
	  * @param newName The new translation for the sample
	  */
	def setTranslation(newName : String) = translation = new String(newName)
	
}
