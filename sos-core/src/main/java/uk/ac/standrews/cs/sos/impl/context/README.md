# Functions

## Predicate

This is a list of predicates that can be used:

- MetadataPropertyPredicate(IGUID guid, String property, List<String> matchingContentTypes) : boolean
- ContentTypePredicate(IGUID guid, List<String> matchingContentTypes) : boolean
- MetadataIntPropertyPredicate(IGUID guid, String property, Integer matchingValue) : boolean
- MetadataIntGreaterPropertyPredicate(IGUID guid, String property, Integer matchingValue) : boolean
- MetadataIntLessPropertyPredicate(IGUID guid, String property, Integer matchingValue) : boolean
- SignedBy(IGUID guid, IGUID signer) : boolean
- ContentIsProtected(IGUID guid) : boolean
- ContentIsNotProtected(IGUID guid) : boolean
- SearchText(IGUID guid, String textToSearch) : boolean
- SearchTextIgnoreCase(IGUID guid, String textToSearch) : boolean
- TextOccurrences(IGUID guid, String textToSearch) : int
- TextOccurrencesIgnoreCase(IGUID guid, String textToSearch) : int
- JavaFileHasMethod(IGUID guid, String method) : boolean
- JavaFileHasClass(IGUID guid, String clazz) : boolean

In addition you can write your own predicates.

FIXME
## Policies

This is a list of policies that can be used:

- ManifestReplicationPolicy(PolicyActions policyActions, NodesCollection codomain, int factor)
- DataReplicationPolicy(PolicyActions policyActions, NodesCollection codomain, int factor)
- MetadataReplicationPolicy(PolicyActions policyActions, NodesCollection codomain, int factor)
- DeletionPolicy(PolicyActions policyActions, NodesCollection codomain)
- GrantAccessPolicy(PolicyActions policyActions, IGUID granter, IGUID grantee)
- ReplicateAllVersionsPolicy(PolicyActions policyActions)
- NotifyNodesPolicy(PolicyActions policyActions, NodesCollection codomain)

# Examples of Contexts as JSON strings

## With Predicate

```json
{
	"name": "Test",
	"predicate": "CommonPredicates.ContentTypePredicate(guid, Collections.singletonList(\"image/jpeg\"))"
}

```

```json
{
    "name": "All",
    "predicate": "CommonPredicates.AcceptAll();"
}
```

## Context with specified GUID

```json
{
	"name": "Test",
	"guid": "6a7d6e5c875ab6ed01665e6aef853b7ce3b74cff",
	"predicate": "CommonPredicates.AcceptAll();"
}
```

## With Policy

- domain is LOCAL
- codomain is LOCAL

```json
{
	"name": "Test",
	"predicate": "CommonPredicates.ContentTypePredicate(guid, Collections.singletonList(\"image/jpeg\"));",
	"policies" : [
	    "CommonPolicies.ManifestReplicationPolicy(policyActions, codomain, 1)"
	]
}
```

## With dependencies

```json
{
	"name": "Test",
	"guid": "6a7d6e5c875ab6ed01665e6aef853b7ce3b74cff"
	"dependencies": [ "uk.ac.standrews.cs.IGUID" ]
}
```



## With custom domain

- domain is LOCAL, but explicit

```json
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

```json
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

```json
{
    "name": "All",
    "predicate": "CommonPredicates.AcceptAll();",
    "domain": {
        "type" : "ANY",
        "nodes" : []
    }
}
```