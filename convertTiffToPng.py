#!/usr/local/bin/python3
# need the package pillow to be installed

# find every tif in the given directory and the ones under it and transform it to png

# pip3 install defusedxml : stdlib xml parsing module is vulnerable to attacks
# pip3 install Pillow

import os
import sys
import subprocess
import defusedxml.ElementTree as ET
from PIL import Image

yourpath = sys.argv[1]
# os.mkdir("./maurdor/png")
print("your path", yourpath)
for root, dirs, files in os.walk(yourpath, topdown=False):
    for name in files:
        print(os.path.join(root, name))
        if os.path.splitext(os.path.join(root,name))[1].lower() == ".tif":
            if os.path.isfile(os.path.splitext(os.path.join(root,name))[0]+".png"):
                print("a png file already exists for %s" % name)
            else:
                outfile = os.path.splitext(os.path.join(root,name))[0]+".png"
                try:
                    im = Image.open(os.path.join(root,name))

                    print("generating png for %s" % name)
                    # im.thumbnail(im.size)
                    im.save(outfile, "PNG", quality=100)
                    # without_extension = name.split(".")[0]
                    # im.save("./maurdor/png/%s.png" % without_extension, "PNG", quality=100)
                except Exception as e:
                    print(e)

metadata = [f for f in os.listdir(yourpath) if f.endswith(".xml")]

for xmlfile in metadata:
    print("Processing file '{}'".format(xmlfile))

    xmlns = r"{http://lamp.cfar.umd.edu/media/projects/GEDI/}"

    try:
        # parsing the XML file
        xml = ET.parse("{}/{}".format(yourpath, xmlfile))
        root = xml.getroot()

        doc = root.find("{}DL_DOCUMENT".format(xmlns))
        if doc == None:
            print("No DL_DOCUMENT")
            raise ET.ParseError()

        pages = doc.findall("{}DL_PAGE".format(xmlns))

        # change src from .tif to .png for each page
        for page in pages:
            imgfile = page.get("src")

            filename = imgfile[:-4]
            extension = imgfile[-4:]

            if extension == ".tif":
                page.set("src", filename + ".png")
        
            # saving the modified xml file
            xml.write("{}/{}".format(yourpath, xmlfile))

    except ET.ParseError:
        print("Error: File '{}' is not well formed.".format(xmlfile))
        exit(1)
