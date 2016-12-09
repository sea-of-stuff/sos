package uk.ac.standrews.cs.sos.rest;

import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

/**
 * This is a test end-point.
 */
@Path("/ping")
public class Ping {

    @GET
    @Path("/{param}")
    public Response getMsg(@PathParam("param") String msg) {
        SOS_LOG.log(LEVEL.INFO, "REST: GET /ping/{params}");

        if (msg == null || msg.isEmpty()) {
            msg = "What? Please give me a message.";
        }

        String output = "Pong : " + msg;
        return Response.status(200).entity(output).build();
    }

}
