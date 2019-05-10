#!/usr/local/bin/python3

# pip3 install defusedxml : stdlib xml parsing module is vulnerable to attacks

import os
import sys
import subprocess
import defusedxml.ElementTree as ET

# This script cuts a GEDI file "gedi.xml" containing several pages
# referring to the "img1.x", "img2.x", ... image files, into several
# GEDI files, each "gediN.xml" containing one page and referring to "imgN.x".

if len(sys.argv) != 2:
    print("Usage: {} <directory>"
          .format(sys.argv[0]))
    exit(1)

# the directory to process
directory = sys.argv[1]

metadata = [f for f in os.listdir(directory) if f.endswith(".xml")]
images = [f for f in os.listdir(directory) if f.endswith(".tif")]

for xmlfile in metadata:
    filename = xmlfile[:-4]

    print("Processing file '{}'".format(xmlfile))

    xmlns = r"{http://lamp.cfar.umd.edu/media/projects/GEDI/}"

    try:
        # parsing the XML file
        xml = ET.parse("{}/{}".format(directory, xmlfile))
        root = xml.getroot()

        doc = root.find("{}DL_DOCUMENT".format(xmlns))
        if doc == None:
            print("No DL_DOCUMENT")
            raise ET.ParseError()

        pages = doc.findall("{}DL_PAGE".format(xmlns))

        # create a new xml document for each page
        for i in range(len(pages)):
            page = pages[i]
            page_number = page.get("src")[len(filename):-4]

            imgfile = page.get("src")
            if imgfile not in images:
                print("Missing image '{}'".format(imgfile))
                exit(1)
            
            # making a copy of the xml file without the DL_DOCUMENT node
            xml2 = xml
            doc2 = xml2.getroot().find("{}DL_DOCUMENT".format(xmlns))

            # removing all the pages in the copy, and adding only the current one
            for p in doc2.findall("{}DL_PAGE".format(xmlns)):
                doc2.remove(p)
            doc2.append(page)
            doc2.set("NrOfPages", "1")
        
            # saving the copied and modified xml file
            xml2.write("{}/{}.xml".format(directory, filename + page_number))
        
        # deleting the old xml file
        os.remove("{}/{}".format(directory, xmlfile))

    except ET.ParseError:
        print("Error: File '{}' is not well formed.".format(xmlfile))
        exit(1)