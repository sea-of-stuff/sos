package uk.ac.standrews.cs.sos.network.protocol;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.network.Method;
import uk.ac.standrews.cs.sos.network.RequestsManager;
import uk.ac.standrews.cs.sos.network.Response;
import uk.ac.standrews.cs.sos.network.SyncRequest;
import uk.ac.standrews.cs.sos.node.NodesDirectory;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class NodeDiscovery {

    public static Node findNode(NodesDirectory nodesDirectory, IGUID nodeGUID) throws IOException {

        Node nodeToContact = nodesDirectory.getNode(nodeGUID);

        if (nodeToContact == null) {
            nodeToContact = findNodeViaNDS(nodesDirectory, nodeGUID);
        }

        return nodeToContact;
    }

    private static Node findNodeViaNDS(NodesDirectory nodesDirectory, IGUID nodeGUID) throws IOException {

        Collection<Node> ndsNodes = nodesDirectory.getNDSNodes();
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
