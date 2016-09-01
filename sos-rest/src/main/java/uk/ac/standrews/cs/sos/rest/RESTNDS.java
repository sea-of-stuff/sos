package uk.ac.standrews.cs.sos.rest;

import uk.ac.standrews.cs.sos.HTTP.HTTPResponses;
import uk.ac.standrews.cs.sos.ServerState;
import uk.ac.standrews.cs.sos.bindings.NDSNode;
import uk.ac.standrews.cs.sos.interfaces.sos.NDS;
import uk.ac.standrews.cs.sos.json.model.NodeModel;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * https://quicksilver.host.cs.st-andrews.ac.uk/sos/api.html#node-discovery-service
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@Path("/nds/")
@NDSNode
public class RESTNDS {

    @PUT
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public Response register(NodeModel node) {
        NDS nds = ServerState.sos.getNDS();

        boolean nodeIsRegistered = nds.registerNode(node);

        if (nodeIsRegistered) {
            return HTTPResponses.OK("Node is registered");
        } else {
            return HTTPResponses.INTERNAL_SERVER();
        }
    }

    @GET
    @Path("/guid/{guid}")
    public void findByGUID() {}

    @GET
    @Path("/guid/{guid}/roles")
    public void getRoles() {}

    @GET
    @Path("/role/{role}")
    public void findByRole() {}
}
