# Examples of Contexts as JSON strings

## With Predicate

```
{
	"name": "Test",
	"predicate": "CommonPredicates.ContentTypePredicate(guid, Collections.singletonList(\"image/jpeg\"))"
}

```

```
{
    "name": "All",
    "predicate": "CommonPredicates.AcceptAll();"
}
```

## Context with specified GUID

```
{
	"name": "Test",
	"guid": "6a7d6e5c875ab6ed01665e6aef853b7ce3b74cff",
	"predicate": "CommonPredicates.AcceptAll();"
}
```

## With Policy

- domain is LOCAL
- codomain is LOCAL

```
{
	"name": "Test",
	"predicate": "CommonPredicates.ContentTypePredicate(guid, Collections.singletonList(\"image/jpeg\"));",
	"policies" : [
	    "CommonPolicies.ManifestReplicationPolicy(policyActions, codomain, 1)"
	]
}

```

## With dependencies

```
{
	"name": "Test",
	"guid": "6a7d6e5c875ab6ed01665e6aef853b7ce3b74cff"
	"dependencies": [ "uk.ac.standrews.cs.IGUID" ]
}
```



## With custom domain

- domain is LOCAL, but explicit

```
{
    "name": "All",
    "predicate": "CommonPredicates.AcceptAll();",
    "domain": {
        "type" : "LOCAL",
        "nodes" : []
    }
}
```

- domain is SPECIFIED

```
{
    "name": "All",
    "predicate": "CommonPredicates.AcceptAll();",
    "policies" : [
    	    "CommonPolicies.ManifestReplicationPolicy(policyActions, codomain, 1)"
    	],
    "codomain": {
        "type" : "SPECIFIED",
        "nodes" : [ "SHA256_16_1111a025d7d3b2cf782da0ef24423181fdd4096091bd8cc18b18c3aab9cb00a4" ]
    }
}
```

- domain is ANY

```
{
    "name": "All",
    "predicate": "CommonPredicates.AcceptAll();",
    "domain": {
        "type" : "ANY",
        "nodes" : []
    }
}
```