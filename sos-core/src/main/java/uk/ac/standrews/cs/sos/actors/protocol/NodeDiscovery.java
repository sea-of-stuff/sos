package uk.ac.standrews.cs.sos.actors.protocol;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.node.NodeNotFoundException;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.network.Method;
import uk.ac.standrews.cs.sos.network.RequestsManager;
import uk.ac.standrews.cs.sos.network.Response;
import uk.ac.standrews.cs.sos.network.SyncRequest;
import uk.ac.standrews.cs.sos.node.directory.LocalNodesDirectory;

import java.io.IOException;
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

    public Node findNode(IGUID nodeGUID) throws NodeNotFoundException {

        Node nodeToContact = localNodesDirectory.getNode(nodeGUID);

        if (nodeToContact == null) {
            try {
                nodeToContact = findNodeViaNDS(nodeGUID);
            } catch (IOException e) {
                throw new NodeNotFoundException(e);
            }
        }

        return nodeToContact;
    }

    private Node findNodeViaNDS(IGUID nodeGUID) throws IOException {

        Set<Node> ndsNodes = localNodesDirectory.getNDSNodes();
        for(Node ndsNode:ndsNodes) {
            URL url = SOSEP.NDS_GET_NODE(ndsNode, nodeGUID);

            SyncRequest request = new SyncRequest(Method.GET, url);
            Response response = RequestsManager.getInstance().playSyncRequest(request);

            // TODO - check response and parse it to node object
            // then break else continue
        }

        return null; // TODO - return node
    }
}
