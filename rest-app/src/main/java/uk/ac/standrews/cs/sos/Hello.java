package uk.ac.standrews.cs.sos;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

/**
 * This is a test end-point
 */
@Path("/hello")
public class Hello {

    @GET
    @Path("/{param}")
    public Response getMsg(@PathParam("param") String msg) {
        if (msg == null || msg.isEmpty()) {
            msg = "What?";
        }
        String output = "Jersey say : " + msg;
        return Response.status(200).entity(output).build();
    }

}