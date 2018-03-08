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
