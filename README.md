# sos

**Index**

- [core](#sos-core)
- [rest](#sos-rest)
- [configuration](#sos-configuration)
- [web-ui](#web-ui)
- [TODO](#todo)

## sos-core

Describe functionalities and main architecture

### Project structure

- **configuration**
    - see [configuration file](#configuration-file)
- **exceptions**
- **interfaces**
- **json**
- **metadata**
- **model**
- **network**
- **node**
- **policy**
- **SOSImpl**
- **utils**

### Configuration File

Paths starting with the tilde `~` are parsed as local paths. TODO - give example

## sos-rest

The sos-rest project defines the REST API. This is server-agnostic.
We provide two server implementation on top of the jersey REST API.
See the sos-rest-jetty module and the sos-rest-grizzly module.

## sos-configuration

This is a simple command line tool that helps in making sos configuration files.

## web-ui

## TODO

This is a list of the main things to do (in no particular order):
- policies
- metadata engine
- webdav integration
- rest api

## How to run fluentd

We use fluentd to aggregate the logs:

``$ docker run -d -p 24224:24224 -v /tmp/data:/fluentd/log fluent/fluentd`