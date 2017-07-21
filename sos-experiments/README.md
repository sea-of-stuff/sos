# sos-experiment

This module contains the code to run the experiment on the SOS.

## Experiment Setup

An experiment is defined by a Java class and by a bunch of experiment configuration files (see documentation below).
The experiment class must implement that Experiment interface, which consists of five main methods.

- setup
- start
- finish
- collectStats
- cleanup

Each method defines a particular phase of the experiment.
If you are writing a new experiment and extend the BaseExperiment class, then these methods will be execute as in the order above.


## Running an experiment via ChicShock

The ChicShock utility can be used to distribute a SOS application to multiple nodes and run the experiment from a remote node.

The ChicShock utility has two types of methods:

- the chic methods distribute SOS application to remote nodes
- the shock methods start the remote SOS instances


## Running an experiment locally

The experiment can also be run locally, by running main methods for the experiments.


## Experiment Configuration

Each experiment needs two types of configuration files:

**configuration.json**: This file contains information on how to distribute the application for the experiment PLUS generic information about the actual experiment

**node configurations**: These are the configuration files for the nodes to be run. It is suggested to call the node configuration files as node_{NODE_ID}.json

Local node configuration file should be called node_0.json


### Example

In the **Experiment_X_1** we bootstrap one local SOS node (running the experiment) and one remote SOS node.
Thus, we need three configuration files: one for describing the experiment and two for the configuration files of the two SOS nodes.

```
|-- configurations
    |-- x_1
        |-- configuration.json  // The configuration of the experiment
        |-- node_0.json     // The configuration for the local node
        |-- node_1.json         // The configuration for one of the remote nodes, as specified in configuration.json
```
