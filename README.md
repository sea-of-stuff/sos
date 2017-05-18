# sos - Sea of Stuff

**Index**

- [core](#sos-core)
- [rest](#sos-rest)
- [configuration](#sos-configuration)
- [web-ui](#web-ui)


## sos-core

The sos-core module contains the code to:
- create and manage manifests
- manage metadata and contexts
- manage the SOS Roles (agent, storage, nodeDiscoveryService, dataDiscoveryService, metadataService, etc...)


## sos-rest and sos-rest-jetty

The sos-rest project defines the REST API. This is server-agnostic.
We provide a server implementation on top of the jersey REST API (see the sos-rest-jetty module).


## sos-filesystem

The sos-filesystem is a very basic example of how the SOS model can be mapped to a real world application.

The mapping used is the following:

- file :: version manifest -> atom manifest -> atom data
- directory :: version manifest -> compound manifest


The sos-fs is used in the sos-app. Here, the filesystem is passed to a WebDAV server (https://github.com/stacs-srg/WebDAV-server) and the WebUI project.
The WebDAV server exposes the sos-fs to the OS as well as to any other application that wishes to interact with the SOS.


## web-ui

The web-ui exposes the sos-filesystem, similarly to the WebDAV server. However, here we are not constrained by the WebDAV protocol, thus
we are able to demonstrate additional features of the SOS.


## sos-configuration

This is a simple command line tool that creates a template file for an SOS node configuration.

```
$ mvn package -pl sos-configuration -am -DskipTests
$ java -jar sos-configuration/target/configuration-1.0-SNAPSHOT.jar (PARAMS <- define them here!)
```


## Running a SOS node


### Configuration file

Use the sos-configuration CLI to start with a fresh template.

### Packaging

```
$ mvn package # use -DskipTests` to skip the tests during the packaging process
$ mv target/app-1.0-SNAPSHOT.jar sos.jar
$ java -jar sos.jar -c configuration.conf ARGS
```

### Running multiple nodes

You can bootstrap multiple nodes using the `experiments.sh` bash script (see script folder)

You can also find other useful bash scripts in the script folder.



## Ideas

- can get geo-location using the service ipinfo.io
example:
curl ipinfo.io
curl ipinfo.io/138.250.10.10

more here: https://ipinfo.io/developers
