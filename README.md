# SOS - Sea of Stuff

This is a prototype of a distributed autonomic personal data storage system.

```
      ___           ___           ___
     /  /\         /  /\         /  /\
    /  /:/_       /  /::\       /  /:/_
   /  /:/ /\     /  /:/\:\     /  /:/ /\
  /  /:/ /::\   /  /:/  \:\   /  /:/ /::\
 /__/:/ /:/\:\ /__/:/ \__\:\ /__/:/ /:/\:\
 \  \:\/:/~/:/ \  \:\ /  /:/ \  \:\/:/~/:/
  \  \::/ /:/   \  \:\  /:/   \  \::/ /:/
   \__\/ /:/     \  \:\/:/     \__\/ /:/
     /__/:/       \  \::/        /__/:/
     \__\/         \__\/         \__\/
```

### Repo Organisation

```
|-- sos
    |-- sos-core            // The core of the SOS
    |-- sos-rest            // REST interface for the SOS
    |-- sos-rest-jetty      // Jetty server
    |-- sos-filesystem      // File system used for the WebDAV server
    |-- web-ui              // Web UI for the SOS
    |-- sos-app             // Basic application to run a SOS node (with webui and WebDAV)
    |-- sos-web-archive     // Example of an application using the SOS
    |-- sos-experiments     // Code with configurations files for the experiments
    |-- experiments         // Scripts to analyse experiments results
    |-- sos-instrument      // Instrumentation code. Useful to get results for the experiments.
    |-- scripts             // A bunch of useful scripts
    |-- README.md           // This README file
```


## sos-core

The sos-core module contains the code to:
- create and manage manifests
- manage metadata and contexts
- manage the SOS Roles (agent, storage, nodeDiscoveryService, dataDiscoveryService, metadataService, etc...)

### SOS Model

- Manifests
- Roles
- Contexts


Example of contexts in JSON formats can be found [here](sos-core/src/main/java/uk/ac/standrews/cs/sos/impl/context/README.md).

### SOS Architecture

- Services


## sos-rest

The sos-rest project defines the REST API. This is server-agnostic.
We provide a server implementation on top of the jersey REST API (see the sos-rest-jetty module).


## sos-filesystem

The sos-filesystem is a very basic example of how the SOS model can be mapped to a real world application.

The mapping used is the following:

- file :: version manifest -> atom manifest -> atom data
- location :: version manifest -> compound manifest


The sos-filesystem is used in the sos-app. Here, the filesystem is passed to a WebDAV server (https://github.com/stacs-srg/WebDAV-server) and the WebUI project.
The WebDAV server exposes the sos-fs to the OS as well as to any other application that wishes to interact with the SOS.


## web-ui

The web-ui exposes the sos-filesystem, similarly to the WebDAV server. However, here we are not constrained by the WebDAV protocol, thus
we are able to demonstrate additional features of the SOS.

## Running a SOS node via the SOS-APP

### Packaging

```
$ mvn package # use -DskipTests` to skip the tests during the packaging process
$ mv target/app-1.0-SNAPSHOT.jar sos.jar
$ java -jar sos.jar -c configuration.conf ARGS
```

### Running multiple nodes

You can bootstrap multiple nodes using the `experiments.sh` bash script (see script folder)

You can also find other useful bash scripts in the script folder.


## Experiments

The code for the experiments is found under `sos-experiments/`. Read the relevant [README](sos-experiments/README.md) for more info.


## Contributors

This work is developed by Simone Ivan Conte ([@sic2](https://github.com/sic2)) as part of his PhD thesis.

Simone is supervised by Prof. Alan Dearle and Dr. Graham Kirby, at the University of St Andrews.
