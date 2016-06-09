package uk.ac.standrews.cs.sos.rest;

import uk.ac.standrews.cs.sos.ServerState;
import uk.ac.standrews.cs.sos.node.ROLE;
import uk.ac.standrews.cs.sos.node.SOS.SOSCoordinator;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static uk.ac.standrews.cs.sos.ServerState.gson;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@Path("roles")
public class GetRoles {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoles(@QueryParam("guid") String node) {
        if (node != null) {
            return getANodeRoles(node);
        } else {
            return getThisNodeRoles();
        }

    }

    private Response getThisNodeRoles() {
        ROLE[] roles = ServerState.sos.getRoles();
        String jsonRoles = gson.toJson(roles);

        return Response
                .status(Response.Status.ACCEPTED)
                .entity(jsonRoles)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .build();
    }

    private Response getANodeRoles(String node) {
        // TODO - check node manager for roles of specified node
        // this should be enabled only if this node is a coordinator
        // edge cases:
        // 1. the specified node is this one
        // 2. the specified node is unknown


        SOSCoordinator sos = (SOSCoordinator) ServerState.sos.getSOS(ROLE.COORDINATOR);
        if (sos != null) {
            // TODO
            return null;
        }

        return null;
    }

}
