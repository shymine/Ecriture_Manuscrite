package main.scala.model.recogniser.laia

import java.io.{File, PrintWriter}

import main.scala.model.recogniser.Converter
import main.scala.utils.Example
import main.scala.model.recogniser.ConverterRecogniser

import scala.util.matching.Regex
import sys.process._

/**
  * The laia connector will be in charge of using the Laia recognizer to perform recognition
  * Laia should be installed beforehand using docker
  * @param converter The converter from the Example to the laia input
  */
class LaiaConnector(converter: Converter, val height: Integer) extends ConverterRecogniser(converter){

	/**
	  * Laia needs all images to be the same height
	  * needs to store in different files:
	  * 	the list of image for training
	  * 	the list of training transcripts
	  * 	the list of images for validation
	  * 	the list of validation transcripts
	  * 	the list of symbol and their id
	  * needs to know where the model is stored
	  * @param samples The samples the Recognizer trains on
	  */
	/*
		laia-train-ctc \
		  --adversarial_weight 0.5 \
		  --batch_size 16 \
		  --log_also_to_stderr info \
		  --log_level info \
		  --log_file laia.log \
		  --progress_table_output laia.dat \
		  --use_distortions true \
		  --early_stop_epochs 100 \
		  --learning_rate 0.0005 \
		  model.t7 data/lang/char/symbs.txt \
		  data/train.lst data/lang/char/train.txt \
		  data/test.lst data/lang/char/test.txt;
	 */
	override def train(samples: Iterable[Example]): Unit = {
		/*
			train.lst: path to the images to train on
				data/imgs_proc/010001.png
			test.lst: path to the images to test on
				data/imgs_proc/210341.png
			train.txt: the image id and the translation
				010001 t r e i n t a {space} m i l {space} v e i n t e
			test.txt: the image id and the translation
				210341 m i l {space} q u i n i e n t o s {space} c i n c u e n t a {space} y {space} s i e t e {space} m i l l o n e s
			symbs.txt: the symbols and their ID
				<ctc> 0
				a 1
				{space} 2
		 */
		val convertedData = this.converter.convertData(samples)
		val writer1 = new PrintWriter(new File("./model/imageTraining.lst"))
		val writer2 = new PrintWriter(new File("./model/transcriptTraining.txt"))
		val writer3 = new PrintWriter(new File("./model/imageValidation.lst"))
		val writer4 = new PrintWriter(new File("./model/transcriptValidation.txt"))
		samples.foreach(sample => writer1.write(sample.path))
		val regID: Regex = (raw"(\w+)\.png").r
		samples.foreach(sample => {
			val inte: Regex.Match =
				regID.findFirstMatchIn(sample.path)
					.getOrElse(throw new MatchError(sample.path))
			val imgId: String = inte.group(1)
			println(imgId)
			writer2.write(imgId) // verifier l'append
		})
	}

	/**
	  * Need to recognize and then compare the result it gives with the real transcriptions
	  * @param samples The set to test the Recognizer
	  */
	override def evaluate(samples: Iterable[Example]): Unit = ???

	/**
	  *
	  * @param samples The samples the Recognizer must "translate".
	  *                It's format as a list to ease the process of
	  *                knowing the correct translation to the image
	  * @return Returns the List of the sample corresponding to the
	  *         image and the translation. If the input format is a
	  *         paragraph then there may be n samples extracted from
	  *         it depending if the Recognizer output for a paragraph
	  *         is an output per line or one output for the paragraph.
	  */
	override def recognize(samples: List[Example]): List[Example] = ???

	/**
	  * To put in the module interface class
	  *
	  * Concerning Laia, changing the recognizer while using laia
	  * only means to change the language laia will be recognizing
	  * @param recognizerPath
	  * @param converter
	  */
	override def changeRecognizer(recognizerPath: String, converter: Converter): Unit = ???

	/**
	  * Initialise the network to use
	  * @param symbol
	  */
	/*
	laia-create-model \
	  --cnn_batch_norm true \
	  --cnn_type leakyrelu \
	  -- 1 64 20 model.t7;
 	*/
	def init(symbol: Integer) = {
		val ret: Integer = s"laia-docker create-model 1 $height $symbol model/model.t1" !
		if(ret != 0) {
			throw new RuntimeException("problem with creation of the model")
		}
	}

}

