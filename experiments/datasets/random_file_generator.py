import os
import sys

foldername = sys.argv[1]
no_files = int(sys.argv[2])
size = int(sys.argv[3]) # in bytes

if not os.path.exists(foldername):
    os.makedirs(foldername)

for i in range(0, no_files):
    print 'Creating file ' + str(i)
    with open(foldername + '/' + str(i), 'wb') as fout:
        fout.write(os.urandom(size))