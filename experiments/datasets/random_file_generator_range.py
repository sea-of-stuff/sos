import os
import sys

# example_1: python random_file_generator_range.py io_multiple 10 0 10000000 500000
# create subsets that go from 100kb to 10mb with 100kb intervals. Each dataset will have 10 files.
#
# example_2: python random_file_generator_range.py io_multiple_small 10 100 1000 100
# create subsets that go from 100 bytes to 1kb with 100bytes intervals. Each dataset will have 10 files.
foldername = sys.argv[1]
no_files = int(sys.argv[2])
min_size = int(sys.argv[3]) # in bytes
max_size = int(sys.argv[4]) # in bytes
step_size = int(sys.argv[5]) # in bytes

for size in range(min_size, max_size + 1, step_size):
    subfolder = foldername + '/' + str(size)
    if not os.path.exists(subfolder):
        os.makedirs(subfolder)

    print 'Creating dataset at folder: ' + subfolder
    for i in range(0, no_files):
        with open(subfolder + '/' + str(i), 'wb') as fout:
            fout.write(os.urandom(size))
