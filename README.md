# Agnosco
## Goal

The goal of this project is to take pictures of documents as inputs, as well as files called "ground truths" describing the content of these documents, and generate datasets that will be able to be converted into inputs for Deep Learning frameworks. A training example is the association of a sub-image in the document, called a thumbnail, and its written content, in text format, called the transcription. A Deep Learning framework will train itself by trying to guess the text present in the thumbnail, and the transcription acts as a correction, thus enabling supervised learning.

This project is a M1 project from [INSA](https://www.insa-rennes.fr/) students in IT, with the help of Bertrand Coüasnon from the [Intuidoc](https://www-intuidoc.irisa.fr/) team of IRISA, and Erwan Foucher and Julien Bouvet from [Sopra Steria](https://www.soprasteria.com/fr).

## Current state of the project

The application is composed of a [Scala](https://scala-lang.org/) backend and an [Angular](https://angular.io/) frontend. The frontend allows users to load documents, cut text zones in them, annotate them, validate them, and export all the created examples to an output directory. It is connected to the backend through a REST API representing the various actions the user can perform on the server. The backend uses [SQLite](https://sqlite.org/index.html) to store projects, documents, pages, and examples and the [OpenCV](https://opencv.org/) library to perform image processing actions.

The suitable input files for description documents are the GEDI and PiFF formats. The [GEDI](https://sourceforge.net/projects/gedigroundtruth/) format is an XML-inspired format used by the Maurdor database. The PiFF format is derived from JSON, and defined by the Intuidoc team.

## Repository contents

### agnosco-backend

This directory contains the Scala backend, available as a Scala project. The required libraries are listed in the `build.sbt` file, and should be installed automatically when building with [sbt](https://www.scala-sbt.org/).

The backend has been developed with IntelliJ, with the Scala plugin installed. One should theoretically be able to run the project with Scala and `sbt` installed on the system without IntelliJ, but that has not been tested yet.

### agnosco-frontend

This directory contains the Angular frontend, available as an [npm](https://www.npmjs.com/) project. The `package-lock.json` file describes the dependencies. Running `npm install` in this directory should install all the required dependencies.

### blurlinedetector

In this directory, one can find a small Java project containing network features to receive files from a socket, call a line detector developed at IRISA Rennes by the Intuidoc research team, and send back the executable's output into another socket. This project should be extracted to a JAR file, and this JAR file should be in the `DetectLignes-v1.0` directory.

NB : all the files in the `DetectLignes-v1.0` directory should be placed in a Linux 32-bit virtual machine, with Python3, [imagemagick](https://imagemagick.org/index.php), and Java JRE version 1.8 installed.

### maurdor

Documents from the Maurdor scanned documents database are available in this folder, in order to make tests for the application.

### Python scripts

Some Python scripts are present in this directory to pre-process the data before the user can actually use them in the application.

- `convertTiffToPng.py` : converts TIFF images to the PNG format ;
- `gedisplitter.py` : splits GEDI files containing several pages into several files pointing to one page only ;
- `imagecropper.py` : cuts an image thanks to the polygon given as a parameter ;
- `prepareLaia.py` **(DOES NOT WORK YET)** : this script is a trial of interface with a recogniser, [Laia](https://github.com/jpuigcerver/Laia/tree/master/egs/iam).

These scripts require Python3 to be installed on the server's system, with several libraries installed with `pip3` : OpenCV, [Pillow](https://python-pillow.org/), and [defusedxml](https://pypi.org/project/defusedxml/).

## How to install the application

The application is composed of 3 major blocks as described previously.

The backend runs using Java 8, the Scala and sbt plugins (jdk8, scala 2.11) in IntelliJ 2019.1.2. To open the project, select `Open project`, go to the build.sbt file, and open it as a project. IntelliJ will then ask you if you want to import the dependencies, click yes ;) . SBT will download for you the dependencies of the project. You can then run the application by launching the main method in the Main object.

The frontend uses Angular6. To install the dependencies of this block, you will need npm. In the package agnosco-frontend, open a terminal and run `npm install`. The dependencies in the file `package-lock.json` will then be downloaded. You can then run the debug server by running `ng serve`.

For the scripts, you will need Python3 and pip3 to install the needed librairies :

```bash
pip3 install opencv-python --user
pip3 install pillow --user
pip3 install defusedxml --user
```

## How to run the application

In order to run the application, one must run the `Main` Scala object from the backend application.

Several parameters (IP and ports for the line detector, directories, ...) are available in the `common` package object.

For the frontend side, one needs to run the Angular project by calling `ng serve` in the `agnosco-frontend` directory.

If the line detector is used (and the boolean for it enabled in the `common` package object), the `blurlinedetector.jar` file must be run by executing the `launch.sh` script on its machine.

## Workflow

Right now, in the `maurdor` folder, the XML files are prepared as 1-page GEDI files and the TIFF files are also in a 1-page format. However, if you receive new data from the Maurdor database, or work with TIFF and GEDI formats from another base, you need first to preprocess them using first `tiffsplitter.py` to remove the multi-page TIFF files, then using `gedisplitter.py` to split the multi-page GEDI (XML) files.

Moreover, as most web browsers do not support the TIFF format, use the `convertTiffToPng.py` script.

The scripts modify in place the `src` attribute of the pages in the XML files. When loading an image and a ground truth, you will then need to use the PNG image, but if you want to keep the TIFF format, do not use the converter script, and the `src` attribute will still be pointing to the TIFF file.

Once you have annotated and validated the examples through the frontend (YOU MUST HIT ENTER to validate the set of 4 examples in the frontend), you can export the project or document into the export folder at the root of agnosco-backend.

## Bugs detected

A bug has been detected when deleting a page or when the chosen association of ground truth and image is wrong : the backend throws an exception when trying to add newer pages.
