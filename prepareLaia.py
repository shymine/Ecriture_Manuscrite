#!/usr/local/bin/python3

import sys
import os
from random import random
import re
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
testRate = sys.argv[4] if len(sys.argv) >= 5 else 0.1

# Processing of the images
## Need to get the names of the images

imagesNames = [i for i in os.listdir(datasetPath) if
               not i.endswith(".txt") and os.path.isfile("%s/%s" % (datasetPath, i))]

# images are only with txt docs
print(imagesNames)

for imageName in imagesNames:
    try:
        im = Image.open("%s/%s" % (datasetPath, imageName))
        wsize = int((float(imgSize) * float(im.size[0])) / float(im.size[1]))
        im = im.resize((wsize, imgSize), Image.ANTIALIAS)
        # im.thumbnail((wsize, imgSize))
        im.save("%s/%s" % (datasetExport, imageName), quality=100)
    except Exception as e:
        print("unable to load image %s/%s" % (datasetPath, imageName))
        print(e)
print("images created")

txtFiles = [t for t in os.listdir(datasetPath) if t.endswith(".txt")]
print(txtFiles)

testLst = open("%s/test.lst" % datasetExport, "w")
testTxt = open("%s/test.txt" % datasetExport, "w")
trainLst = open("%s/train.lst" % datasetExport, "w")
trainTxt = open("%s/train.txt" % datasetExport, "w")

with open("%s/symbs.txt" % datasetExport, "w") as file:
    file.write("""<eps> 0
<ctc> 1
a 2
b 3
c 4
d 5
e 6
f 7
g 8
h 9
i 10
j 11
k 12
l 13
m 14
n 15
o 16
p 17
q 18
r 19
s 20
t 21
u 22
v 23
w 24
x 25
y 26
z 27
A 28
B 29
C 30
D 31
E 32
F 33
G 34
H 35
I 36
J 37
K 38
L 39
M 40
N 41
O 42
P 43
Q 44
R 45
S 46
T 47
U 48
V 49
W 50
X 51
Y 52
Z 53
é 54
è 55
ê 56
ë 57
à 58
â 59
ù 60
ï 61
î 62
ô 63
ö 64
° 65
+ 66
- 67
* 68
/ 69
' 70
: 71
, 72
; 73
? 74
. 75
! 76
( 77
) 78
= 79
@ 80
& 81
0 82
1 83
2 84
3 85
4 86
5 87
6 88
7 89
8 90
9 91
¤ 92
" 93
« 94
» 95
 96
ê 97
À 98
{ 99
} 100
[ 101
] 102
{space} 103""")

for txtName in txtFiles:
    try:
        test = random() <= testRate
        with open("%s/%s" % (datasetPath, txtName), "r") as file:
            text = file.readline()
            print(text)
            imageName = re.sub(r".txt$", r".png", txtName)

            imgName = re.sub(r".txt", r"", txtName)
            transcript = []
            for char in text:
                if char == " ":
                    transcript.append("{space}")
                    transcript.append(" ")
                elif not char == '\n':
                    transcript.append(char)
                    transcript.append(" ")

            if test:
                testLst.write("%s/%s\n" % (datasetExport, imageName))
                testTxt.write("%s %s\n" % (imgName, str.join('', transcript)))
            else:
                trainLst.write("%s/%s\n" % (datasetExport, imageName))
                trainTxt.write("%s %s\n" % (imgName, str.join('', transcript)))

    except Exception as e:
        print(e)
testLst.close()
testTxt.close()
trainLst.close()
trainTxt.close()
