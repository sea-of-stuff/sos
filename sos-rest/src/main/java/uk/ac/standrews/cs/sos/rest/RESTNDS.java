package uk.ac.standrews.cs.sos.rest;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.HTTP.HTTPResponses;
import uk.ac.standrews.cs.sos.RESTConfig;
import uk.ac.standrews.cs.sos.actors.NodeDiscoveryService;
import uk.ac.standrews.cs.sos.bindings.NDSNode;
import uk.ac.standrews.cs.sos.exceptions.node.NodeNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.node.NodeRegistrationException;
import uk.ac.standrews.cs.sos.interfaces.node.NodeType;
import uk.ac.standrews.cs.sos.json.model.NodeModel;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Set;

/**
 * https://quicksilver.host.cs.st-andrews.ac.uk/sos/api.html#node-discovery-service
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@Path("/nds/")
@NDSNode
public class RESTNDS {

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public Response register(NodeModel node) {
        SOS_LOG.log(LEVEL.INFO, "REST: POST /nds/register");

        NodeDiscoveryService nodeDiscoveryService = RESTConfig.sos.getNDS();

        try {
            Node registerNode = nodeDiscoveryService.registerNode(node, true); // TODO - might change based on configuration
            if (registerNode != null) {
                return HTTPResponses.OK(registerNode.toString());
            } else {
                return HTTPResponses.INTERNAL_SERVER();
            }

        } catch (NodeRegistrationException e) {
            return HTTPResponses.INTERNAL_SERVER();
        }


    }

    @GET
    @Path("/guid/{guid}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public Response findByGUID(@PathParam("guid") String guid) {
        SOS_LOG.log(LEVEL.INFO, "REST: GET /nds/guid/{guid}");

        if (guid == null || guid.isEmpty()) {
            return HTTPResponses.BAD_REQUEST("Bad input");
        }

        IGUID nodeGUID;
        try {
            nodeGUID = GUIDFactory.recreateGUID(guid);
        } catch (GUIDGenerationException e) {
            return HTTPResponses.BAD_REQUEST("Bad input");
        }

        NodeDiscoveryService nodeDiscoveryService = RESTConfig.sos.getNDS();

        try {
            Node node = nodeDiscoveryService.getNode(nodeGUID);
            return HTTPResponses.OK(node.toString());
        } catch (NodeNotFoundException e) {
            return HTTPResponses.NOT_FOUND("Node with GUID: " + nodeGUID.toString() + " could not be found");
        }

    }

    @GET
    @Path("/role/{role}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public Response findByRole(@PathParam("role") String role) {
        SOS_LOG.log(LEVEL.INFO, "REST: GET /nds/role/{role}");

        NodeType nodeType = NodeType.get(role.toLowerCase());

        NodeDiscoveryService nodeDiscoveryService = RESTConfig.sos.getNDS();
        Set<Node> nodes = nodeDiscoveryService.getNodes(nodeType);

        String json = collectionToJson(nodes);
        return HTTPResponses.OK(json);
    }

    private String collectionToJson(Set<Node> nodes) {

        StringBuilder json = new StringBuilder("[");
        for(Node node:nodes) {
            json.append(node.toString()).append(", ");
        }
        json = new StringBuilder(json.substring(0, json.length() - 2));

        json.append("]");
        return json.toString();
    }
}
