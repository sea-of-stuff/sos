package uk.ac.standrews.cs.sos.rest.api;

import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.rest.HTTP.HTTPResponses;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

/**
 * This is a test end-point.
 */
@Path("/sos/ping")
public class Ping {

    @GET
    @Path("/{msg: .*}") // Accept both empty and non-empty param
    public Response getMsg(@PathParam("msg") final String msg) {
        SOS_LOG.log(LEVEL.INFO, "REST: GET /sos/ping/{msg}");

        String output = "Pong : ";
        if (msg == null || msg.isEmpty()) {
            output += "What? Please give me a message to Pong -- GET /sos/ping/{msg}";
        } else {
            output += msg;
        }

        return HTTPResponses.OK(output);
    }

}
