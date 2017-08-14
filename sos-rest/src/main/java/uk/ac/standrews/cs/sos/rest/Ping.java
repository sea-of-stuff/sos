package uk.ac.standrews.cs.sos.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.HTTP.HTTPResponses;
import uk.ac.standrews.cs.sos.HTTP.HTTPStatus;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

/**
 * This is a test end-point.
 */
@Path("/ping")
@Api(value="/ping", description = "Dummy end-point")
public class Ping {

    @GET
    @Path("/{msg: .*}") // Accept both empty and non-empty param
    @ApiOperation(value = "Ping the SOS REST API")
    @ApiResponses(value = {
            @ApiResponse(code = HTTPStatus.OK, message = "PING MESSAGE")
    })
    public Response getMsg(@PathParam("msg") final String msg) {
        SOS_LOG.log(LEVEL.INFO, "REST: GET /ping/{msg}");

        String output = "Pong : ";
        if (msg == null || msg.isEmpty()) {
            output += "What? Please give me a message to Pong -- GET /ping/{msg}";
        } else {
            output += msg;
        }

        return HTTPResponses.OK(output);
    }

}
