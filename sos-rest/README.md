# sos-rest

This module defines the REST API for a SOS Node.
The API is defined using Jersey.

The project **sos-rest-jetty** is an example of an HTTP server running this REST API.
You can implement your own HTTP server by adding this project as a dependency

```
<dependency>
    <groupId>uk.ac.standrews.cs.sos</groupId>
    <artifactId>rest</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

Then create a ResourceConfiguration:

```
final ResourceConfig rc = new RESTConfig().build(**SOSLocalNode instance**);
```

Then use bind the ResourceConfig with your server.

## Project structure

- The `RESTConfig` class keeps a reference of the SOSNode used by this API and defines the jersey configurations.
- The REST APIs are defined insider the package `rest`
- The `filters` package allows a better control of the REST API by intercepting the requests before they reach the definitions in `rest`
- The `bindings` are needed to bind the `rest` definitions with the `filters`
- The json models are defined in the `json` package and uses the JSON Jackson library


## Testing

Tests are available in the `test` package.

If you are command-line guy, you may like using a combination of curl and jq (e.g. `curl URL | jq`).

### Postman

You can use Postman to test this REST API. A collection of standard requests is available inside the postman folder.


# REST API Overview


## General APIs

The REST calls in this section are role-agnostics.

**Get the services of a node**
```
REQUEST
GET /info

RESPONSE
HTTP/1.1 200 OK
Content-type: application/json

{
  "guid": "GUID",
  "hostname": "ip address",
  "port": 8080,
  "services": {
    "client": true/false,
    "store": true/false,
    "dds": true/false,
    "nds": true/false,
    "mcs": true/false
  }
}
```

An example of response is:
```
HTTP/1.1 200 OK
Content-type: application/json

{
  "guid": "6b67f67f31908dd0e574699f163eda2cc117f7f4",
  "hostname": "244:244:43:1",
  "port": 8080,
  "services": {
    "client": true,
    "store": true,
    "dds": false,
    "nds": true,
    "mcs": false
  }
}
```

---

## Client

The client does not provide any access to its data.

---

## Storage

The store node is used solely to store data. The stored data is accessible to other nodes in the SOS.

**Get data of a given atom manifest**
```
REQUEST
GET /store/data/guid/<ATOM-GUID>

SUCCESSFUL RESPONSE
HTTP/1.1 200 OK

Here is the data....


UNSUCCESSFUL RESPONSE
HTTP/1.1 404 NOT FOUND
```


**Add data by location**
```
REQUEST
POST /store/uri

{
    "uri" : "URI",
}

RESPONSE
HTTP/1.1 201 CREATED
Location: <Location on this node>
Content-type: application/json

{
    guid : <GUID>,
    bundles : [ LOCATION BUNDLES ],
    etc...
}
```

*TODO*: pass policies in the request

policies:
    - replication factor
    - privacy??? or this is done via context?

Example 1
```
POST /store/uri

{
    "uri" : "http://example.com/bear.jpeg"
}

RESPONSE
HTTP/1.1 201 CREATED
Location: sos://c00dc7e8-c689-494e-8d0d-b96bb690c164/23cec17e-c246-418a-8e82-fcc97d70adfe
Content-type: application/json

{
    guid : 23cec17e-c246-418a-8e82-fcc97d70adfe
    bundles :
        [
            {
                "type" : "persistent",
                "location" : sos://c00dc7e8-c689-494e-8d0d-b96bb690c164/23cec17e-c246-418a-8e82-fcc97d70adfe
            },
            {
                "type" : "provenance",
                "location" : http://example.com/bear.jpeg
            }
        ]
}
```

Example 2
```
POST /store/uri

{
    "uri" : "sos://c6adc7e8-31ee-563a-8d0d-aa230690c296/23cec17e-c246-418a-8e82-fcc97d70adfe"
}

RESPONSE
HTTP/1.1 201 CREATED
Location: sos://c00dc7e8-c689-494e-8d0d-b96bb690c164/23cec17e-c246-418a-8e82-fcc97d70adfe
Content-type: application/json

{
    guid : 23cec17e-c246-418a-8e82-fcc97d70adfe
    bundles :
        [
            {
                "type" : "cache", # TODO - cache locations should be stored within clients only, not in the store nodes. If a node plays both services, then return different results based on role.
                "location" : sos://c6adc7e8-31ee-563a-8d0d-aa230690c296/23cec17e-c246-418a-8e82-fcc97d70adfe
            },
            {
                "type" : "persistent",
                "location" : sos://c00dc7e8-c689-494e-8d0d-b96bb690c164/23cec17e-c246-418a-8e82-fcc97d70adfe
            }
        ]
}
```

**Add data by stream**
```
REQUEST
POST /store/stream

This is some data in the body...


RESPONSE
HTTP/1.1 201 CREATED
Location: <Location on this node>
Content-type: application/json

{
    guid : <GUID>,
    bundles : [ LOCATION BUNDLES ],
    etc...
}
```

---

## Node Discovery Service

The NDS nodes allow nodes to join the SOS and discovery other nodes.

**Node joins**
```
REQUEST
PUT /node

{
    guid : node guid,
    ip : ipv4, ipv6 (or ips?),
    port : port (or ports?),
    services : [services]
}

RESPONSE
HTTP/1.1 200 OK
```

Example:
```
PUT /node

{
    guid : c6adc7e8-31ee-563a-8d0d-aa230690c296,
    ip : 234:234:20:2,
    port: 8081,
    services: [STORAGE]
}

RESPONSE
HTTP/1.1 200 OK
```

**Get node that matches GUID**
```
REQUEST
GET /services?guid=GUID

RESPONSE
HTTP/1.1 200 OK
Content-type: application/json

{
    [
     guid : guid,
     services : [ROLES],
     ip : ip,
     port : port
     expire: timestamp # tells the receiver that this info might not be valid after some time. useful to force callers to check on their info (not 100% sure about this)
    ]
}
```

Example:
```
REQUEST
GET /services?guid=c6adc7e8-31ee-563a-8d0d-aa230690c296

RESPONSE
HTTP/1.1 200 OK
Content-type: application/json

{
     guid : c6adc7e8-31ee-563a-8d0d-aa230690c296,
     services : [STORAGE],
     ip : 234:234:20:2,
     port : 8081
}

```

**Get all nodes that satisfy the given role**
```
REQUEST
GET /nodes?role=ROLE

RESPONSE
HTTP/1.1 200 OK
Content-type: application/json

{
    "role" : ROLE,
    "nodes" : [guid]
}
```

Example:
```
GET /nodes?role=STORAGE

RESPONSE
HTTP/1.1 200 OK
Content-type: application/json

{
    "role" : STORAGE,
    "nodes" :
        [
            c00dc7e8-c689-494e-8d0d-b96bb690c164,
            c6adc7e8-31ee-563a-8d0d-aa230690c296,
            be64bbbd-7bc4-4887-8036-5259be9f86f3,
            ac73a9f9-df91-47a6-a8cb-80f8e18b994a
        ]
}
```

---

## Data Discovery Service

**Add manifest**
Adds the given manifest to the DDS. No resource is created in the DDS.

```
REQUEST
POST /coordinator/manifest

This is the manifest to add

RESPONSE
HTTP/1.1 201 CREATED
```

Example:
```
REQUEST
POST /coordinator/manifest

{
  "Type": "Atom",
  "ContentGUID": "42b3edd482b3dfdbc5adc683840c21db06f00b78",
  "Locations": [
    {
    "Type" : "provenance",
    "Location" : "http://example.com/bear.jpeg"
    },
    {
      "Type": "persistent",
      "Location": "sos://6b67f67f31908dd0e574699f163eda2cc117f7f4/42b3edd482b3dfdbc5adc683840c21db06f00b78"
    }
  ]
}


RESPONSE
HTTP/1.1 201 CREATED
```

**Get Manifests for a given GUID**
```
REQUEST
GET /manifest?guid=GUID

RESPONSE
HTTP/1.1 200 OK
Content-type: application/json

{
    manifest
}
```


Example (with Atom manifest):
```
GET /manifest?guid=23cec17e-c246-418a-8e82-fcc97d70adfe

RESPONSE
HTTP/1.1 200 OK
Content-type: application/json

{
    type : atom,
    guid : 23cec17e-c246-418a-8e82-fcc97d70adfe,
    bundles:
        [
            {
                "type" : cache,
                "location" : sos://c6adc7e8-31ee-563a-8d0d-aa230690c296/23cec17e-c246-418a-8e82-fcc97d70adfe
            },
            {
                "type" : persistent,
                "location" : sos://c00dc7e8-c689-494e-8d0d-b96bb690c164/23cec17e-c246-418a-8e82-fcc97d70adfe
            }
        ]
}
```


Example (with Compound manifest):
```
GET /manifest?guid=2c26a3de-a6ce-4541-b836-853d08f2fcd2

RESPONSE
HTTP/1.1 200 OK
Content-type: application/json

{
    type : compound,
    guid : 2c26a3de-a6ce-4541-b836-853d08f2fcd2,
    compoundType : "collection" # or "data",
    signature : "SIGNATURE", # optional
    contents :
        [
            {
                "label" : "bear", # Labels are optional
                "guid" : 23cec17e-c246-418a-8e82-fcc97d70adfe # The atom above
            },
            {
                "guid" : 308f10b4-e612-44ba-883d-17fb7ada4f35
            }
        ]
}
```


Example (with Version manifest):
```
GET /manifest?guid=2c26a3de-a6ce-4541-b836-853d08f2fcd2

RESPONSE
HTTP/1.1 200 OK
Content-type: application/json

{
    type : version
    version : 7fbe0e79-661a-4086-8887-09c0739fc95c,
    invariant : d49e40d8-a21b-454a-90fc-c3969d71349d,
    content : 2c26a3de-a6ce-4541-b836-853d08f2fcd2, # The compound above
    previous :
        [
            2af2bbee-e3dd-488d-81a6-576eb75835f9,
            8872a8c0-f67e-43cd-b244-43795c8d51f9
        ],
    signature : "SIGNATURE", # optional,
    metadata :
        [
            7c490e3d-b512-4a34-a5f6-0a798a752992,
            d33add3d-29d1-41f8-b7e9-fb149fd63a26,
            06d43168-537f-4518-b287-03673f0167d5
        ]
}
```

Note that this method may return more than one manifest if the GUID is the invariant of an asset.

**Get manifests that match given metadata**
```
TODO

RESPONSE
should return info about matching data (+ next batch), and where to find that data? (or that could be with another call)
```

---

## Metadata

or **computation node**

- what a metadata node does
- how is the metadata node used by other nodes, the rest of the SOS?
- what operations are allowed?
- can we execute third parties code? or maybe run only its own code
- maybe user should be able to choose what metadata node to use? (e.g. I want to run my data against this service instead of another one, for various reasons)

Metadata nodes computer metadata given an input stream of data?

metadata might require some time to computer, so there might be a queue. How are other nodes notified of the metadata being calculated? Must be asynchrounous or some notification mechanism or all happens behind the scenes and caller never really knows about metadata (but can check what happened).

```
POST /

Data stream

RESPONSE
HTTP/1.1 200 OK

[
]
```


```
POST /location/SOS-LOCATION

RESPONSE
HTTP/1.1 200 OK

[
]
```

