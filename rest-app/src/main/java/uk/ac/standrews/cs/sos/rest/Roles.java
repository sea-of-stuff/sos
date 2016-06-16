package uk.ac.standrews.cs.sos.rest;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.ServerState;
import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.interfaces.node.SeaOfStuff;
import uk.ac.standrews.cs.sos.node.ROLE;

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
public class Roles {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoles() throws GUIDGenerationException {
        return getThisNodeRoles();
    }

    @GET
    @Path("coordinator")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRolesCoordinator(@QueryParam("guid") String input) throws GUIDGenerationException, SOSException {
        IGUID guid = GUIDFactory.recreateGUID(input);
        return getNodeRoles(guid);
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

    private Response getNodeRoles(IGUID guid) throws SOSException {
        SeaOfStuff sos = ServerState.sos.getSeaOfStuff(ROLE.COORDINATOR);
        Node node = sos.getNode(guid);

        if (node != null) {
            String jsonRoles = gson.toJson(node.getRoles());

            return Response
                    .status(Response.Status.ACCEPTED)
                    .entity(jsonRoles)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .build();

        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

}
