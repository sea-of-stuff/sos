# SOS - Sea of Stuff

This is a prototype of a distributed autonomic personal data storage system built using the SOS model and architecture.

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

 --------------------------------------------------------
| Warning/Notes:                                         |
|   This is a prototype version of the SOS.              |
|   Use this software at your own discretion!            |
|   There are still bugs and missing features.           |
|   Visit https://github.com/stacs-srg/sos for more info |
 --------------------------------------------------------
```

### Repo Organisation

```
|-- sos
    |-- docs                // Webpage for this project
    |-- documentation       // Documentation about this SOS prototype
    |-- sos-core            // The core of the SOS
    |-- sos-rest            // REST interface for the SOS
    |-- sos-rest-jetty      // Jetty server
    |-- sos-filesystem      // File system used for the WebDAV server
    |-- web-ui              // Web UI for the SOS
    |-- sos-app             // Basic application to run a SOS node (with webui and WebDAV)
    |-- sos-web-archive     // Example of an application using the SOS
    |-- git-to-sos          // Utility that converts a git repository into SOS content
    |-- sos-experiments     // Code with configurations files for the experiments
    |-- experiments         // Scripts to analyse experiments results
                            // + datasets and contexts used for the experiments
                            // + Results are written here, under the output (local) or remote (distributed exp) folders
    |-- sos-instrument      // Instrumentation code. Useful to get results for the experiments.
    |-- scripts             // A bunch of useful scripts
    |-- README.md           // This README file
```


## sos-core

The sos-core module contains the code to manage a SOS node and with it:
- create and manage manifests
- manage metadata and contexts
- manage the SOS services (agent, storage, nodeDiscoveryService, dataDiscoveryService, metadataService, etc...)


## SOS Modules

In this section we provide a brief insight to some of the modules of the SOS project.

### sos-rest

The sos-rest project defines the REST API. This is server-agnostic.
We provide a server implementation on top of the jersey REST API (see the sos-rest-jetty module).


### sos-filesystem

The sos-filesystem is a very basic example of how the SOS model can be mapped to a real world application.

The mapping used is the following:

- file :: version manifest -> atom manifest -> atom data
- directory :: version manifest -> compound manifest


The sos-filesystem is used in the sos-app. Here, the filesystem is passed to a WebDAV server (https://github.com/stacs-srg/WebDAV-server) and the WebUI project.
The WebDAV server exposes the sos-fs to the OS as well as to any other application that wishes to interact with the SOS.


## Applications

### WebDAV

This is a WebDAV server running on top of the SOS. The content provided by the WebDAV uses the structure defined by the *sos-filesystem*.

### Web archive

This is a very very simple web crawler that added web content to the SOS. 
Plus this application includes a tiny server that mocks "the internet" and provides what is crawled through the browser.

### web-ui

The web-ui exposes the sos-filesystem, similarly to the WebDAV server.
However, here we are not constrained by the WebDAV protocol, thus we are able to demonstrate additional features of the SOS.

### git-to-sos

WIP

###  DNS over SOS

WIP


## How to run

- mvn package
- mvn license:format

## Contributors

This work is developed by Simone Ivan Conte ([@sic2](https://github.com/sic2)) as part of his PhD thesis.

Simone is supervised by Prof. Alan Dearle and Dr. Graham Kirby from the University of St Andrews.
