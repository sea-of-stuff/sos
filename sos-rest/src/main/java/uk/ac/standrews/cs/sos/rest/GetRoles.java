package uk.ac.standrews.cs.sos.rest;

import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.ServerState;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@Path("roles")
public class GetRoles {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoles() {

        // TODO - get roles for this node
        return null;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoles(@QueryParam("guid") String node) {

        // TODO - check node manager for roles of specified node
        // this should be enabled only if this node is a coordinator
        // edge cases:
        // 1. the specified node is this one
        // 2. the specified node is unknown
        return null;
    }
}
