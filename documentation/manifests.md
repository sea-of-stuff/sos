# Manifests

## Basic Structure

```json
{
  "type" : <Manifest Type>,
  "guid" : hash(<Content to Hash>),
  ...
}
```

## Basic Signed Structure

```json
{
  "type" : <Manifest Type>,
  "guid" : hash(<Content to Hash>),
  "signer" : guid of signer role,
  "signature" : signed manifest,
  ...
}
```

## Basic Protected Entity Structure

```json
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


```json
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

```json
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


```json
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


```json
{
  "type" : "Version",
  "guid" : hash(invariant, content, *previous), # Identifies uniquely this version.
  "invariant" : invariant GUID, # GUID of this asset (fixed once created)
  "content" : content GUID,
  "previous" : [ previous GUID ]
}
```

## Metadata

```json
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

```json
{
  "type" : "User",
  "guid" : hash(type, name, certificate),
  "name" : User name,
  "certificate" : certificate
}
```

## Role

```json
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

```json
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

```json
{
  "type" : "Predicate",
  "guid" : hash(predicate),
  "predicate" : code
}
```

## Policy

```json
{
  "type" : "Policy",
  "guid" : hash(policy),
  "apply" : code,
  "check" : code
  ]
}
```