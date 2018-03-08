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

```json
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
