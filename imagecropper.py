#!/usr/local/bin/python3

import sys
import cv2
import numpy as np

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
#row = 469
#col = 1732
#width = 591
#height = 50
row2 = row + height
col2 = col + width
img2 = img[row:row2, col:col2]
cv2.imwrite(filename_out, img2)
exit(0)