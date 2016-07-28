# sos

## sos-core

Describe functionalities and main architecture

### Configuration File

Paths starting with the tilde `~` are parsed as local paths. TODO - give example

## sos-rest

The sos-rest project defines the REST API. This is server-agnostic.
We provide two server implementation on top of the jersey REST API.
See the sos-rest-jetty module and the sos-rest-grizzly module.

## storage-sta

The sos-storage module defines a common storage interface of files and directories.
This allows us to run the sos-core module over different storage implementations,
such as the file system, AWS S3, a network drive, Dropbox, etc.

Check the README in the storage-sta for more information about it.

This module is available at the following link: LINK

## TODO

This is a list of the main things to do (in no particular order):
- policies
- metadata engine
- webdav integration
- rest api