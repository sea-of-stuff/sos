# Examples of Contexts as JSON strings

## The void context

```
{
	"name": "Test"
}
```

## Context with specified GUID

```
{
	"name": "Test",
	"guid": "6a7d6e5c875ab6ed01665e6aef853b7ce3b74cff"
}
```

## With Predicate

```
{
	"name": "Test",
	"guid": "6a7d6e5c875ab6ed01665e6aef853b7ce3b74cff",
	"predicate": "CommonPredicates.ContentTypePredicate(Collections.singletonList(\"image/jpeg\"))"
}

```

TODO

## With Policy

TODO

## With dependencies

```
{
	"name": "Test",
	"guid": "6a7d6e5c875ab6ed01665e6aef853b7ce3b74cff"
	"dependencies": [ "uk.ac.standrews.cs.IGUID" ]
}
```
