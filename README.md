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
- Users and Roles
- Contexts


Example of contexts in JSON formats can be found [here](sos-core/src/main/java/uk/ac/standrews/cs/sos/impl/context/README.md).

### SOS Architecture

Each SOS node consists of a collection of services that manage specific aspects of the node itself and its behaviour.

The services are:

- agent
- storage
- data discovery
- node discovery
- metadata
- user and role
- context

Each service, except for the agent one, can be exposed to the outside world by running a REST server.
You should set the service to be exposed in the node configuration:

```
"services": {
      "context": {
        "exposed": false
      },
      "storage": {
        "exposed": true
      },
      ...
}
```

The SOS_APP (see the **sos-app** module) should be run with the following parameters: `java -jar sos.jar -c CONFIGURATION -j`,
with the `-j` option enabling the Jetty REST server.


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

### Webdav

TODO

### Web archive

TODO

### web-ui

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


## More stuff

### Logging

The logs are automatically written under the `logs/` folder.

The SOS application uses the log4j logger and changing the logs configuration is as straightforward
as providing a new `log4j.properties` file.

To explicitly instruct the SOS application to use a non-default properties file, you must
add this parameter when running the java app:

` -Dlog4j.configuration=file:/path/to/log4j.properties`

**Note** that the property value must be a valid URL.


#### Example of a log4j.properties for both file and stdout

```
log4j.rootLogger=DEBUG,file,console
log4j.appender.console=org.apache.log4j.ConsoleAppender
log4j.appender.file=org.apache.log4j.RollingFileAppender

# http://stackoverflow.com/a/4953207/2467938
# -Dlogfile.name={logfile}
log4j.appender.file.File=${logfile.name}
log4j.appender.file.ImmediateFlush=true
log4j.appender.file.Threshold=debug
log4j.appender.file.MaxBackupIndex=10
log4j.appender.file.MaxFileSize=10MB

log4j.appender.file.layout=org.apache.log4j.PatternLayout
log4j.appender.file.layout.ConversionPattern=%d{dd-MM-yyyy HH:mm:ss} [ %-5p ] -  %c %x ( %-4r [%t] ) ==> %m%n

log4j.appender.console.layout=org.apache.log4j.PatternLayout
log4j.appender.console.layout.ConversionPattern=%d{dd-MM-yyyy HH:mm:ss} [ %-5p ] -  %c %x ( %-4r [%t] ) ==> %m%n
```


## SOS Internals

When a SOS node is instantiated, the following directory structure is created.

```
|-- sos
    |-- context         // Contains
    |-- data            // Atoms (clear and protected)
    |-- keys            // Keys for Users and Roles
                        // Users have Private Key and Certificate for Digital Signature (.key, .crt)
                        // Roles have Private/Public Asymmetric keys for data protection (.pem, _pub.pem)
                        //       + Digital Signature keys/cert as for the User
    |-- manifests       // Manifests for Atoms, Compounds, Versions and Metadata in JSON
    |-- usro            // Manifests for Users and Roles in JSON
    |-- node            // Indices, caches, dbs for the node
```


## SOS Node Configuration

### Java cacerts path (needed for SSL HTTP requests)

#### Linux
`$(readlink -f /usr/bin/java | sed "s:bin/java::")lib/security/cacerts`

#### MacOSX

`$(/usr/libexec/java_home)/jre/lib/security/cacerts`

## Useful tools

- Online JSON Linter (and more) - https://jsoncompare.com/#!/simple/
- Online hex dump inspector - https://hexed.it/
- File to SHA values - https://md5file.com/calculator

