package uk.ac.standrews.cs.sos.rest;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.HTTP.HTTPResponses;
import uk.ac.standrews.cs.sos.ServerState;
import uk.ac.standrews.cs.sos.bindings.NDSNode;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
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
            return HTTPResponses.OK("Node is registered"); // TODO - return json
        } else {
            return HTTPResponses.INTERNAL_SERVER();
        }
    }

    @GET
    @Path("/guid/{guid}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public Response findByGUID(@PathParam("guid") String guid) {
        if (guid == null || guid.isEmpty()) {
            return HTTPResponses.BAD_REQUEST("Bad input");
        }

        IGUID nodeGUID;
        try {
            nodeGUID = GUIDFactory.recreateGUID(guid);
        } catch (GUIDGenerationException e) {
            return HTTPResponses.BAD_REQUEST("Bad input");
        }

        NDS nds = ServerState.sos.getNDS();
        Node node = nds.getNode(nodeGUID);

        if (node != null) {
            return HTTPResponses.OK("Found " + node.toString()); // TODO - return json
        } else {
            return HTTPResponses.NOT_FOUND("Node with GUID: " + nodeGUID.toString() + " could not be found");
        }
    }

    @GET
    @Path("/guid/{guid}/roles")
    public void getRoles() {}

    @GET
    @Path("/role/{role}")
    public void findByRole() {}
}
