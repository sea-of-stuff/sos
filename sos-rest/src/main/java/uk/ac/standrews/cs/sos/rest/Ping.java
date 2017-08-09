package uk.ac.standrews.cs.sos.rest;

import uk.ac.standrews.cs.logger.LEVEL;
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
    @Path("/{msg: .*}") // Accept both empty and non-empty param
    public Response getMsg(@PathParam("msg") String msg) {
        SOS_LOG.log(LEVEL.INFO, "REST: GET /ping/{msg}");

        if (msg == null || msg.isEmpty()) {
            msg = "What? Please give me a message to Pong -- GET /ping/{msg}";
        }

        String output = "Pong : " + msg;
        return Response.status(200).entity(output).build();
    }

}
