#!/usr/local/bin/python

#pip3 install python-opencv

import sys
import cv2
import numpy as np

# This script cuts an image and makes a thumbnail from it, following the given
# coordinates (row, column, width, and height, giving the exact position of
# the thumbnail to make). The user is expected to give an image ID, so that the
# image files this script generates remain unique.

if len(sys.argv) != 7:
    print("Usage: {} <filename> <id> <row> <col> <width> <height>"
          .format(sys.argv[0]))
    exit(1)

filename_src = sys.argv[1]
img_id = sys.argv[2]
row = int(sys.argv[3])
col = int(sys.argv[4])
width = int(sys.argv[5])
height = int(sys.argv[6])

filename_split = filename_src.split('.')
filename_out = "{}{}.{}".format(filename_split[0], img_id, filename_split[1])

img = cv2.imread(filename_src)
row2 = row + height
col2 = col + width

img2 = img[row:row2, col:col2]
cv2.imwrite(filename_out, img2)
exit(0)
