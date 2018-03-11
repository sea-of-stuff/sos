# How to run

## Packaging

```bash
$ mvn package # or `mvn package -DskipTests` to skip the tests during the packaging process
$ mv target/app-1.0-SNAPSHOT.jar sos.jar
$ java -jar sos.jar -c configuration.conf ARGS
```


## Running

The SOS_APP (see the **sos-app** module) should be run with the following parameters: `java -jar sos.jar -c CONFIGURATION -j`,
with the `-j` option enabling the Jetty REST server.


## Experiments

The code for the experiments is found under `sos-experiments/`. Read the relevant [README](sos-experiments/README.md) for more info.

The experiments results are located under `experiments/output`.


## Headless Tika

The `sos-core` modules used the Apache Tika utility to extract useful metadata from the atoms.
The Tika utility may make some calls to the `java.awt` package, so if you want to run a sos node in headless more, you will have
to specify the following parameter: `-Djava.awt.headless=true`.
