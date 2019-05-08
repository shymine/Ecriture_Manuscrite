# need the package pillow to be installed

# find every tif in the given directory and the ones under it and transform it to png

import os
import sys
from PIL import Image

#yourpath = os.getcwd()
yourpath = sys.argv[1]
print("yourpath",yourpath)
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
                    im.thumbnail(im.size)
                    im.save(outfile, "PNG", quality=100)
                except Exception as e:
                    print(e)
