package uk.ac.standrews.cs.sos.rest;

import uk.ac.standrews.cs.sos.HTTP.HTTPState;
import uk.ac.standrews.cs.sos.ServerState;
import uk.ac.standrews.cs.sos.node.SOSLocalNode;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@Path("/")
public class RESTGeneral {

    @GET
    @Path("/roles")
    public Response getRoles() {

        SOSLocalNode sos = ServerState.sos;

        boolean isClient = sos.isClient();
        boolean isStorage = sos.isStorage();
        boolean isCoordinator = sos.isDiscoveryData();

        String rolesJSON = makeJSONForRoles(isClient, isStorage, isCoordinator);
        return Response.status(HTTPState.OK)
                .entity(rolesJSON)
                .build();
    }

    private String makeJSONForRoles(boolean isClient, boolean isStorage, boolean isCoordinator) {

        String json = "{ ";

        json += "\"client\" : " + isClient + ", ";
        json += "\"storage\" : " + isStorage + ", ";
        json += "\"coordinator\" : " + isCoordinator;

        json += " }";

        return json;
    }
}
