# Manifests


## Basic Structure

```
{
  "type" : <Manifest Type>,
  "guid" : hash(<Content to Hash>),
  ...
}
```


## Basic Signed Structure

```
{
  "type" : <Manifest Type>,
  "guid" : hash(<Content to Hash>),
  "signer" : guid of signer role,
  "signature" : signed manifest,
  ...
}
```


## Basic Protected Entity Structure

```
{
  "type" : Protected Manifest Type,
  "guid" : hash(contents of manifest),
  ...,
  "keys" : 
  [
    {
      "key" : encrypted key,
      "role" : role guid
    }
  ]
}
```


## Node

```
{
  "type" : "Node",
  "guid" : hash(certificate),
  "certificate" : certificate,
  "hostname" : hostname or IP address,
  "port" : port number,
  "services" : {
    "storage" : {
      "exposed" : true/false
    },
    "cms" : {
      "exposed" : true/false
    },
    "mds" : {
      "exposed" : true/false
    },
    "nds" : {
      "exposed" : true/false
    },
    "rms" : {
      "exposed" : true/false
    },
    "mms" : {
      "exposed" : true/false
    }
  }
}
```


## Atom

```
{
    "type" : "Atom",
    "guid" : hash(data),
    "locations":
        [
            { 
                "type" : "cache", "persistent", or "provenance",
                "location" : SOS/external location
            }
        ]
}
```


## Compound

```
{
    "type" : "Compound",
    "guid" : hash(type, compound_type, contents),
    "compound_type" : "collection" or "data",
    "contents" : 
        [
            { 
                "label" : label value, # optional
                "guid" : GUID to content
            },
            {
                "guid" : GUID to content
            }
        ]
}
```


## Version

```
{
  "type" : "Version",
  "guid" : hash(invariant, content, *previous), # Identifies uniquely this version.
  "invariant" : invariant GUID, # GUID of this asset (fixed once created)
  "content" : content GUID,
  "previous" : [ previous GUID ]
}
```


## Metadata

```
{
  "type" : "Metadata",
  "guid" : hash(properties),
  "properties" : [ 
    {
      "key" : property name,
      "type" : "string", "long", "double", "boolean", "guid", or "any"
      "value" : property value
    }
  ]
}
```


## User

```
{
  "type" : "User",
  "guid" : hash(type, name, certificate),
  "name" : User name,
  "certificate" : certificate
}
```


## Role

```
{
  "type" : "Role",
  "guid" : hash(type, user, name, signature, certificate, public key),
  "user" : User GUID,
  "name" : Role name,
  "signature" : as signed by User,
  "certificate" : certificate,
  "public_key" : public key
}
```


## Context

```
{
    "type": "Context",
    "guid": hash(type, name, invariant, *previous, *content, 
    			domain, codomain, predicate, max_age, *policies),
    "timestamp": timestamp, # in seconds
    "name": context name,
    "invariant": hash(type, predicate, policies, max_age),
    "previous": previous GUID # Optional
    "content": content GUID,
    "domain": {
        "type": "LOCAL", "SPECIFIED", or "ANY",
        "nodes": [ node GUID ]
    },
    "codomain": {
        "type": "LOCAL", "SPECIFIED", or "ANY",
        "nodes": [ node GUID ]
    },
    "predicate": predicate GUID,
    "max_age": maximum age, # in seconds
    "policies": [ policy GUID ]
}
```


## Predicate

```
{
  "type" : "Predicate",
  "guid" : hash(predicate),
  "predicate" : code
}
```


## Policy

```
{
  "type" : "Policy",
  "guid" : hash(policy),
  "apply" : code,
  "check" : code
  ]
}
```
