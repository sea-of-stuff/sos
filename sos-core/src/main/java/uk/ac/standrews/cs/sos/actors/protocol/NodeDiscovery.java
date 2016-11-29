package uk.ac.standrews.cs.sos.actors.protocol;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.node.NodeNotFoundException;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.network.Method;
import uk.ac.standrews.cs.sos.network.RequestsManager;
import uk.ac.standrews.cs.sos.network.Response;
import uk.ac.standrews.cs.sos.network.SyncRequest;
import uk.ac.standrews.cs.sos.node.directory.LocalNodesDirectory;
import uk.ac.standrews.cs.sos.utils.IO;

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

        Set<Node> ndsNodes = localNodesDirectory.getNDSNodes();
        for(Node ndsNode:ndsNodes) {
            URL url = SOSEP.NDS_GET_NODE(ndsNode, nodeGUID);

            SyncRequest request = new SyncRequest(Method.GET, url);
            Response response = RequestsManager.getInstance().playSyncRequest(request);


            // TODO - check response and parse it to node object
            // then break else continue
        }

        return null;
    }

    private Node parseNode(InputStream inputStream) {

        try {
            String guidString = IO.InputStreamToString(inputStream);
            GUIDFactory.recreateGUID(guidString);
        } catch (GUIDGenerationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
