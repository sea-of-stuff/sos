# SOS protocols

Protocols are defined as tasks.

- Tasks are persistent (WIP).
- Tasks can be run synchronously or asynchronously.
    - If the `fallbacktosynctasks` parameter of the global settings is true, then all the async tasks are run as sync ones



## PingNode

- This task simply checks if a node is available.




## InfoNode

- Get info about a specific node



## Data Replication

**Parameters**

- IGUID guid
- Data data
- NodesCollection nodesCollection
- int replicationFactor
- StorageService storageService
- NodeDiscoveryService nodeDiscoveryService
- boolean delegateReplication

### Codomain

The codomain is defined by the `nodesCollection` parameter.
It is suggested that the size(codomain) > replicationFactor, so that the replication factor
can be satisfied even if some nodes cannot be reached.

### Replicate with delegation

Data is replicated to one node, which will then take care of the rest of the replications.


## Fetch Data

Get data from a specific node



## Data Challenge

Challenge a node about some data with the specified GUID.

- Before challenging, this node should generate the hash of (data with GUID + challenge string) = GUID'
- Challenge --> (GUID of data + challenge string)
- If node returns GUID', then the node has passed the challenge



## Fetch Roles

Ask a node about the roles of a given user.


## Manifest challenge

Similar to data challenge.


## Manifest replication

Similar to data challenge.

## Fetch manifest

Fetch manifest from node. The manifest can be an atom, compound, version, metadata, role, user, node, context, predicate, policy, etc...

## Fetch versions

Ask node about all known version for asset's invariant.