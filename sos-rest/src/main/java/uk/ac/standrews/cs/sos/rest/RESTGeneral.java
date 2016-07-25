package uk.ac.standrews.cs.sos.rest;

import uk.ac.standrews.cs.sos.ServerState;
import uk.ac.standrews.cs.sos.node.SOSLocalNode;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class RESTGeneral {

    @GET
    @Path("/roles")
    public Response getRoles() {

        SOSLocalNode sos = ServerState.sos;

        boolean isClient = sos.isClient();
        boolean isStorage = sos.isStorage();
        boolean isCoordinator = sos.isCoordinator();

        // TODO - return JSON
        return Response.status(200)
                .entity(isClient + ", " + isStorage + ", " + isCoordinator)
                .build();
    }
}
