package uk.ac.standrews.cs.sos.rest;

import uk.ac.standrews.cs.sos.bindings.MCSNode;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@Path("/mcs/")
@MCSNode
public class RESTMCS {

    @GET
    @Path("/")
    public Response test() {
        return Response.status(200).entity("MCS-TEST").build();
    }
}
