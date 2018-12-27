package utils

/**
  * The class representing the data samples in the database.
  * @param image The path leading to the image
  * @param translation The current translation associated to the image
  */
class Sample(private val _image: String, private var _translation: String) {

	/**
	  * Give the path leading to the image of the sample
	  * @return The path of the image
	  */
	def path: String = _image

	/**
	  * Give the translation associated with the image
	  * @return Returns a copy of the translation
	  */
	def translation: String = new String(_translation)

	/**
	  * Set the translation with a copy of the given String
	  * @param newName The new translation for the sample
	  */
	def translation(newName : String) = _translation = new String(newName)

}
