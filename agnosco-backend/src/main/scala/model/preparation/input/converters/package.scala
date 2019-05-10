package model.preparation.input

/** This package contains all the converters used by [[PiFFReader]] to parse ground truths. */
package object converters {
	val PIFF_CONVERTERS = List(GEDIToPiFFConverter)
}
