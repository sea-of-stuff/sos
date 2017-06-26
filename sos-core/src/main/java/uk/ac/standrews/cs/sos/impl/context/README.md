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

