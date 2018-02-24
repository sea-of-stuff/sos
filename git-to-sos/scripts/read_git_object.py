import zlib
filename = '.git/objects/09/92196b4d515ef49d9a31d058cdd69ac699c19b'
compressed_contents = open(filename, 'rb').read()
decompressed_contents = zlib.decompress(compressed_contents)
