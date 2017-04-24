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
- manage the SOS Roles (agent, storage, nds, dds, mms)


## sos-rest

The sos-rest project defines the REST API. This is server-agnostic.
We provide a server implementation on top of the jersey REST API (see the sos-rest-jetty module).


## sos-filesystem

The sos-filesystem is a very basic example of how the SOS model can be mapped to a real world application.

The mapping used is the following:

- file :: version manifest -> atom manifest -> atom data
- folder :: version manifest -> compound manifest


The sos-fs is used in the sos-app. Here, the filesystem is passed to a WebDAV server (https://github.com/quicksilver-sta/WebDAV-server) and the WebUI project.
The WebDAV server exposes the sos-fs to the OS as well as any other application that wishes to interact with the SOS.


## web-ui

The web-ui exposes the sos-filesystem, similarly to the WebDAV server. However, here we are not constrained by the WebDAV protocol, thus
we are able to demonstrate additional features of the SOS.


## sos-configuration

This is a simple command line tool that creates a template file for an SOS node configuration.

```
$ mvn package -pl sos-configuration -am -DskipTests
$ java -jar sos-configuration/target/sos-configuration-jar-with-dependencies.jar (PARAMS <- define them here!)
```


## Running a SOS node


### Configuration file

`$ touch configuration.conf`

Otherwise use the sos-configuration CLI to start with a fresh template.

### Packaging

```
$ mvn package # use -DskipTests to not run any tests during the packaging process
$ mv target/sos-app.jar sos.jar
$ java -jar sos.jar -c configuration.conf ARGS
```

### How to run fluentd

We use fluentd to aggregate the logs. Make sure that Docker is installed and you have downloaded the fluentd docker container (**TODO** - instructions).

The run:

$ docker run -d -p 24224:24224 -v /tmp/data:/fluentd/log fluent/fluentd


### Running multiple nodes

You can bootstrap multiple nodes using the `experiments.sh` bash script (see script folder)

You can also find other useful bash scripts in the script folder.

## Ideas

- can get geo-location using the service ipinfo.io
example:
curl ipinfo.io
curl ipinfo.io/138.250.10.10

more here: https://ipinfo.io/developers
