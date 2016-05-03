package uk.ac.standrews.sos.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("hello")
public class HelloWorld {

    @GET
    @Produces("text/plain")
    public String getHello() {
        return "Hello World!";
    }

}
