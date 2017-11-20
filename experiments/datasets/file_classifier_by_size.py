'''Classifies files by size and writes them to a target folder'''
import os
import sys

source = sys.argv[1]
target = sys.argv[2]
lower_limit = int(sys.argv[3])
upper_limit = int(sys.argv[3])


for root, dirs, files in os.walk(source):
    
    for source_file in files:
        print "Processing file ", source_file, " at path ", root

        size_source_file = os.stat(root + os.sep + source_file).st_size    
        if size_source_file < lower_limit or size_source_file > upper_limit:
            print "\tFile size is too small or too big"
            continue
        else:
            print "\tFile is large enough"


        if not os.path.exists(target + os.sep):
            os.makedirs(target + os.sep)
        newFile = open(target + os.sep + source_file, "wba")

        with open(root + os.sep + source_file, "rb") as f:
            byte = f.read(1)
            while byte != "":

                # Do stuff with byte.
                byte = f.read(1)
                # Not optimal, but it works...
                newFile.write(byte)
