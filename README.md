# Agnosco
## Goal

The goal of this project is to take pictures of documents as inputs, as well as files called "ground truths" describing the content of these documents, and generate datasets that will be able to be converted into inputs for Deep Learning frameworks. A training example is the association of a sub-image in the document, called a thumbnail, and its written content, in text format, called the transcription. A Deep Learning framework will train itself by trying to guess the text present in the thumbnail, and the transcription acts as a correction, thus enabling supervised learning.

This project is a M1 project from INSA students in IT, with the help of Bertrand Coüasnon from the Intuidoc team of IRISA, and Erwan Foucher and Julien Bouvet from Sopra Steria.

## Current state of the project

The application is composed of a Scala backend and an Angular frontend. The frontend allows users to load documents, cut text zones in them, annotate them, validate them, and export all the created examples to an output directory. It is connected to the backend through a REST API representing the various actions the user can perform on the server. The backend uses SQLite to store projects, documents, pages, and examples and the OpenCV library to perform image processing actions.

The suitable input files for description documents are the GEDI and PiFF formats. The GEDI format is an XML-inspired format used by the Maurdor database. The PiFF format is defined by the Intuidoc team.

## Repository contents

### agnosco-backend

This directory contains the Scala backend, available as a Scala IntelliJ project.

### agnosco-frontend

This directory contains the Angular frontend, available as an npm project.

### blurlinedetector

In this directory, one can find a small Java project containing network features to receive files from a socket, call a line detector developed at IRISA Rennes by the Intuidoc research team, and send back the executable's output into another socket.

### maurdor

Documents from the Maurdor scanned documents database are available in this folder, in order to make tests for the application.

### Python scripts

Some Python scripts are present in this directory to pre-process the data before the user can actually use them in the application.

- `convertTiffToPng.py` : converts TIFF images to the PNG format ;
- `gedisplitter.py` : splits GEDI files containing several pages into several files pointing to one page only ;
- `imagecropper.py` : cuts an image thanks to the polygon given as a parameter ;
- `prepareLaia.py` : TODO

## How to run the application

In order to run the application, one must run the `Main` Scala object from the backend application, setting the right parameters (IP and ports for the line detector, ...), and run the Angular frontend by calling `ng serve` in the `agnosco-frontend` directory.