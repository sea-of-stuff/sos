package uk.ac.standrews.cs.sos.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.HTTP.HTTPResponses;
import uk.ac.standrews.cs.sos.RESTConfig;
import uk.ac.standrews.cs.sos.bindings.NDSNode;
import uk.ac.standrews.cs.sos.exceptions.node.NodeNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.node.NodeRegistrationException;
import uk.ac.standrews.cs.sos.impl.node.SOSNode;
import uk.ac.standrews.cs.sos.interfaces.node.NodeType;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.services.NodeDiscoveryService;
import uk.ac.standrews.cs.sos.utils.JSONHelper;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@Path("/nds/")
@NDSNode
public class RESTNDS {

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public Response register(SOSNode node) {
        SOS_LOG.log(LEVEL.INFO, "REST: POST /nds/register");

        NodeDiscoveryService nodeDiscoveryService = RESTConfig.sos.getNDS();

        try {
            Node registeredNode = nodeDiscoveryService.registerNode(node, true); // TODO - might change based on local configuration (see settings)
            if (registeredNode != null) {
                return HTTPResponses.CREATED(registeredNode.toString());
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
    @Path("/service/{service}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public Response findByService(@PathParam("service") String service) {
        SOS_LOG.log(LEVEL.INFO, "REST: GET /nds/service/{service}");

        try {
            NodeType nodeType = NodeType.get(service.toLowerCase());

            NodeDiscoveryService nodeDiscoveryService = RESTConfig.sos.getNDS();
            Set<Node> nodes = nodeDiscoveryService.getNodes(nodeType);
            String out = JSONHelper.JsonObjMapper().writeValueAsString(nodes);

            return HTTPResponses.OK(out);

        } catch (JsonProcessingException e) {

            return HTTPResponses.INTERNAL_SERVER();
        }
    }

}
