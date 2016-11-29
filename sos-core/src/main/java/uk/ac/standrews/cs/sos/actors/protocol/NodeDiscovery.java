package uk.ac.standrews.cs.sos.actors.protocol;

import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.node.NodeNotFoundException;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.network.Method;
import uk.ac.standrews.cs.sos.network.RequestsManager;
import uk.ac.standrews.cs.sos.network.Response;
import uk.ac.standrews.cs.sos.network.SyncRequest;
import uk.ac.standrews.cs.sos.node.SOSNode;
import uk.ac.standrews.cs.sos.node.directory.LocalNodesDirectory;
import uk.ac.standrews.cs.sos.utils.IO;
import uk.ac.standrews.cs.sos.utils.JSONHelper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Set;


/**
 *
 * ideas:
 * round robin
 * by type
 * broadcasting/multicasting
 * get list of nodes from a well known url end-point
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class NodeDiscovery {

    private LocalNodesDirectory localNodesDirectory;

    public NodeDiscovery(LocalNodesDirectory localNodesDirectory) {
        this.localNodesDirectory = localNodesDirectory;
    }

    /**
     * Attempt to find a node matching the given GUID first locally, then by contacting known NDS nodes.
     *
     * @param nodeGUID
     * @return
     * @throws NodeNotFoundException
     */
    public Node findNode(IGUID nodeGUID) throws NodeNotFoundException {

        if (nodeGUID == null || nodeGUID.isInvalid()) {
            throw new NodeNotFoundException("Cannot find node for invalid GUID");
        }

        Node localNode = localNodesDirectory.getLocalNode();
        if (localNode.getNodeGUID().equals(nodeGUID)) {
            return localNode;
        }

        Node nodeToContact = localNodesDirectory.getNode(nodeGUID);

        if (nodeToContact == null) {
            try {
                nodeToContact = findNodeViaNDS(nodeGUID);
            } catch (IOException e) {
                throw new NodeNotFoundException(e);
            }
        }

        if (nodeToContact == null) {
            throw new NodeNotFoundException("Unable to find node for GUID: " + nodeGUID.toString());
        }

        return nodeToContact;
    }

    private Node findNodeViaNDS(IGUID nodeGUID) throws IOException {

        Node retval = null;

        Set<Node> ndsNodes = localNodesDirectory.getNDSNodes();
        for(Node ndsNode:ndsNodes) {
            URL url = SOSEP.NDS_GET_NODE(ndsNode, nodeGUID);

            SyncRequest request = new SyncRequest(Method.GET, url);
            Response response = RequestsManager.getInstance().playSyncRequest(request);

            retval = parseNode(response.getBody());

            if (retval != null) {
                break;
            }
        }

        return retval;
    }

    private Node parseNode(InputStream inputStream) {

        Node retval = null;
        
        try {
            String body = IO.InputStreamToString(inputStream);
            JsonNode jsonNode = JSONHelper.JsonObjMapper().readTree(body);


            IGUID nodeGUID = GUIDFactory.recreateGUID(jsonNode.get(SOSConstants.GUID).asText());
            String hostname = jsonNode.get(SOSConstants.HOSTNAME).asText();
            int port = jsonNode.get(SOSConstants.PORT).asInt();

            retval = new SOSNode(nodeGUID, hostname, port, false, true, false, false, false);
        } catch (GUIDGenerationException | IOException e) {
            e.printStackTrace();
        }

        return retval;
    }
}
