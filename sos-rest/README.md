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

### Postman

You can use Postman to test this REST API. A collection of standard requests is available inside the postman folder.