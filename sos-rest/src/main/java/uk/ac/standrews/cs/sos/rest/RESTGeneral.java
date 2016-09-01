package uk.ac.standrews.cs.sos.rest;

import uk.ac.standrews.cs.sos.HTTP.HTTPState;
import uk.ac.standrews.cs.sos.ServerState;
import uk.ac.standrews.cs.sos.node.SOSLocalNode;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@Path("/")
public class RESTGeneral {

    @GET
    @Path("/info")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getInfo() {

        SOSLocalNode sos = ServerState.sos;
        return Response.status(HTTPState.OK)
                .entity(sos.toString())
                .build();
    }

}
