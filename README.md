# sos

**Index**

- [core](#sos-core)
- [rest](#sos-rest)
- [configuration](#sos-configuration)
- [web-ui](#web-ui)

## sos-core

The sos-core module contains the code to:
- create and manage manifests
- manage metadata and contexts
- manage the SOS Roles (agent, storage, nds, dds, mcs)


## sos-rest

The sos-rest project defines the REST API. This is server-agnostic.
We provide a server implementation on top of the jersey REST API (see the sos-rest-jetty module).


## sos-filesystem

The sos-filesystem is a very basic example of how the SOS model can be mapped to a real world application.

The mapping used is the following:

- file :: asset manifest -> atom manifest -> atom data
- folder :: asset manifest -> compound manifest


## sos-webdav

This is a simple WebDAV server that exposes the sos-filesystem.


## web-ui

The web-ui exposes the sos-filesystem, similarly to the sos-webdav. However, here we are not constrained by the WebDAV protocol, thus
we are able to demonstrate additional features of the SOS.


## sos-configuration

This is a simple command line tool that creates a template file for your configuration.


## Running the SOS


### Configuration file

`$ touch configuration.conf`

### Packaging

```
$ mvn package # use -DskipTests to not run any tests during the packaging process
$ mv target/sos-app-jar-with-dependencies.jar sos.jar
$ java -jar sos.jar ARGS
```

### How to run fluentd

We use fluentd to aggregate the logs. Make sure that Docker is installed and you have downloaded the fluentd docker container (**TODO** - instructions).

The run:

$ docker run -d -p 24224:24224 -v /tmp/data:/fluentd/log fluent/fluentd
