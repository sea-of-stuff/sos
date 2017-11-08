Datasets should be places under this folder.

No dataset is versioned, as of now - August 2017, due to the upload limitations imposed by Github.

## Some notes on the datasets I use for the experiments

### random_1

This is a snapshot of my download folder.

### text

This are text files I wrote myself

### Images

The images contains a few photos taken by Simone plus some photos from the jpg1 dataset available at this site: http://lear.inrialpes.fr/people/jegou/data.php

### masc_500k_texts

http://www.anc.org/data/masc/downloads/data-download/

The following files had to be changed due to issues in the XML format.

- jokes/jokes1.txt:
- jokes/jokes6.txt
- jokes/jokes9.txt
- jokes/jokes12.txt
- jokes/jokes13.txt
- jokes/jokes15.txt
- jokes/jokes16.txt
- twitter/tweets1.txt
- twitter/tweets2.txt

### deep

Dataset with subfolders.

### 20 newsgroup corpus

http://disi.unitn.it/moschitti/corpora.htm

### Reuters-21578

http://disi.unitn.it/moschitti/corpora.htm


### Gutenberg

http://www.gutenberg.org/wiki/Gutenberg:Information_About_Robot_Access_to_our_Pages

To unzip run the following command:
`find . -depth -name '*.zip' -exec /usr/bin/unzip -n {} \; -delete`

### text_1kb

Used the 20 newsgroup corpus and truncated the files

### text_100kb

Used the gutenber files and truncated


### Usenet corpus (~36gb compressed)


http://www.psych.ualberta.ca/~westburylab/downloads/usenetcorpus.download.html


### Wikipedia

https://dumps.wikimedia.org/