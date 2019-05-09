#!/usr/local/bin/python3

# pip3 install defusedxml : stdlib xml parsing module is vulnerable to attacks

import os
import sys
import subprocess
import defusedxml.ElementTree as ET

# This script cuts an image file "img.x" containing several pages
# into several ("img1.x", "img2.x", ...) image files with only one
# page each. It also updates the GEDI ground truth file ("img.xml")
# accordingly, so that every page is still associated to the right
# image even after the cut.

if len(sys.argv) != 2:
    print("Usage: {} <directory>"
          .format(sys.argv[0]))
    exit(1)

directory = sys.argv[1]

if not (os.path.isdir(directory + "/xml") and os.path.isdir(directory + "/tiff")):
    print("Error: 'xml' and 'tiff' subdirectories must exist in the given directory.")
    exit(1)

metadata = [f for f in os.listdir(directory + "/xml") if f.endswith(".xml")]
images = [f for f in os.listdir(directory + "/tiff") if f.endswith(".tif")]

for image in images:
    filename = image[:-4]
    extension = image[-3:]

    print("Processing image '{}'".format(image))

    image_path = directory + "/tiff/" + image
    image_path_no_ext = directory + "/tiff/" + filename

    # splitting the image
    process = subprocess.Popen(
        ["convert", image_path, "-scene", "1", "{}%d.{}".format(image_path_no_ext, extension)]
    )
    process.wait()

    # deleting the old image
    os.remove(image_path)

    # getting the new images
    newly_created_images = [f for f in os.listdir(directory + "/tiff") if f.startswith(filename)]

    if not os.path.isfile("{}/xml/{}.xml".format(directory, filename)):
        print("Warning: associated file '{}.xml' was not found.".format(filename))
        continue
    
    xmlns = r"{http://lamp.cfar.umd.edu/media/projects/GEDI/}"
    
    try:
        # parsing the associated XML file
        xml = ET.parse("{}/xml/{}.xml".format(directory, filename))
        root = xml.getroot()

        doc = root.find("{}DL_DOCUMENT".format(xmlns))
        if doc == None:
            print("No DL_DOCUMENT")
            raise ET.ParseError()

        pages = doc.findall("{}DL_PAGE".format(xmlns))
        if len(pages) != len(newly_created_images):
            print("not enough pages")
            raise ET.ParseError()

        # changing the image source for each page
        for new_image in newly_created_images:
            # page_id = new_image[-5]
            page_id = new_image[len(filename):-4]

            pages_with_right_id = [p for p in pages if p.get("pageID") == page_id]
            if len(pages_with_right_id) != 1:
                print("no right id or several")
                raise ET.ParseError()
            
            page = pages_with_right_id[0]
            page.set("src", new_image)
        
        # saving the xml file
        xml.write("{}/xml/{}.xml".format(directory, filename))

    except ET.ParseError:
        print("Error: associated file '{}.xml' is not well formed.".format(filename))
        exit(1)
