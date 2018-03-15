## SOS Internals

When a SOS node is instantiated, the following directory structure is created.

```
|-- sos
    |-- java            // Compiled computational work units
    |-- data            // Atoms (w/wo protection)
    |-- keys            // Keys for Users and Roles
                        // Users have Private Key and Certificate for Digital Signature (.key, .crt)
                        // Roles have Private/Public Asymmetric keys for data protection (.pem, _pub.pem)
                        //       + Digital Signature keys/cert as for the User
    |-- manifests       // Manifests
    |-- node            // Indices, caches, dbs for the node
```
