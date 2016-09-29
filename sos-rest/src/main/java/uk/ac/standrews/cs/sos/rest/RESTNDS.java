package uk.ac.standrews.cs.sos.rest;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.HTTP.HTTPResponses;
import uk.ac.standrews.cs.sos.RESTConfig;
import uk.ac.standrews.cs.sos.bindings.NDSNode;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.interfaces.sos.NDS;
import uk.ac.standrews.cs.sos.json.model.NodeModel;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;

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
        NDS nds = RESTConfig.sos.getNDS();

        Node registerNode = nds.registerNode(node);

        if (registerNode != null) {
            return HTTPResponses.OK(registerNode.toString());
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

        NDS nds = RESTConfig.sos.getNDS();
        Node node = nds.getNode(nodeGUID);

        if (node != null) {
            return HTTPResponses.OK(node.toString());
        } else {
            return HTTPResponses.NOT_FOUND("Node with GUID: " + nodeGUID.toString() + " could not be found");
        }
    }

    @GET
    @Path("/role/{role}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public Response findByRole(@PathParam("role") String role) {
        role = role.toLowerCase();

        NDS nds = RESTConfig.sos.getNDS();

        Collection<Node> nodes;

        switch(role) {
            case "storage":
                nodes = nds.getStorageNodes();
                break;
            case "nds":
                nodes = nds.getNDSNodes();
                break;
            case "dds":
                nodes = nds.getDDSNodes();
                break;
            case "mcs":
                nodes = nds.getMCSNodes();
                break;
            case "client":
            default:
                return HTTPResponses.BAD_REQUEST("Bad input");
        }

        String json = collectionToJson(nodes);
        return HTTPResponses.OK(json);
    }

    private String collectionToJson(Collection<Node> nodes) {

        String json = "[";
        for(Node node:nodes) {
            json += node.toString() + ", ";
        }
        json = json.substring(0, json.length()-2);

        json += "]";
        return json;
    }
}
