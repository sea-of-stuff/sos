# sos

## sos-core

Describe functionalities and main architecture

### Configuration File

Paths starting with the tilde `~` are parsed as local paths. TODO - give example

## sos-rest

The sos-rest project defines the REST API. This is server-agnostic.
We provide two server implementation on top of the jersey REST API.
See the sos-rest-jetty module and the sos-rest-grizzly module.

## sos-storage

The sos-storage module defines a common storage interface of files and directories.
This allows us to run the sos-core module over different storage implementations,
such as the file system, AWS S3, a network drive, Dropbox, etc.

### AWS

In order to work with an AWS S3 storage, you need to provide an access_key_id and a secret_access_key.
This can be done explicitly or by setting the environment variables:
```
export AWS_ACCESS_KEY_ID=<KEY>
export AWS_SECRET_KEY=<KEY>
```

### Network drive

This current implementation expects the network storage to be already mounted under /Volumes (Unix-only)