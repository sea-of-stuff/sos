# sos-experiment

This module contains the code to run the experiment on the SOS.

The ChicShock utility can be used to distribute a SOS application to multiple nodes and run the experiment from a remote node.

The experiment can also be run locally, by running main methods for the experiments.

## Configuration

Each experiment needs two types of configuration files:

**configuration.json**: This file contains information on how to distribute the application for the experiment PLUS generic information about the actual experiment

**node configurations**: These are the configuration files for the nodes to be run. It is suggested to call the node configuration files as node_{NODE_ID}.json

Local node configuration file should be called local_node.json