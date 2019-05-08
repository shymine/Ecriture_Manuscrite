#!/usr/local/bin/python3

import sys
import os

from PIL import Image

"""
Laia needs all images to be the same height
needs to store in different files:
the list of image for training
the list of training transcripts
the list of images for validation
the list of validation transcripts
the list of symbol and their id
needs to know where the model is store

as

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
"""

# the path to the folder with the examples (image + transcript)
datasetPath = sys.argv[1]
datasetExport = sys.argv[2]
imgSize = sys.argv[3] if len(sys.argv) >= 4 else 64

# Processing of the images
## Need to get the names of the images

imagesNames = [i for i in os.listdir(datasetPath) if
               not i.endswith(".txt") and os.path.isfile("%s/%s" % (datasetPath, i))]

# images are only with txt docs
print(imagesNames)

for imageName in imagesNames:
    try:
        im = Image.open("%s/%s" % (datasetPath, imageName))
        wsize = int((float(imgSize)*float(im.size[0]))/float(im.size[1]))
        im = im.resize((wsize, imgSize), Image.ANTIALIAS)
        #im.thumbnail((wsize, imgSize))
        im.save("%s/%s" %(datasetExport, imageName), quality=100)
    except Exception as e:
        print("unable to load image %s/%s" % (datasetPath, imageName))
        print(e)

print("images created")

txtFiles = [t for t in os.listdir(datasetPath) if i.endswith(".txt")]
print(txtFiles)

for txtName in txtFiles:
    try:
        with open("%s/%s" %(datasetPath, txtName), "r"):

