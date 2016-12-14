package uk.ac.standrews.cs.sos.actors.protocol;

import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.node.NodeRegistrationException;
import uk.ac.standrews.cs.sos.exceptions.node.NodesDirectoryException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSURLException;
import uk.ac.standrews.cs.sos.interfaces.network.Response;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.network.HTTPStatus;
import uk.ac.standrews.cs.sos.network.Method;
import uk.ac.standrews.cs.sos.network.RequestsManager;
import uk.ac.standrews.cs.sos.network.SyncRequest;
import uk.ac.standrews.cs.sos.node.SOSNode;
import uk.ac.standrews.cs.sos.node.directory.LocalNodesDirectory;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class NodeRegistration {

    private LocalNodesDirectory localNodesDirectory;

    public NodeRegistration(LocalNodesDirectory localNodesDirectory) {
        this.localNodesDirectory = localNodesDirectory;
    }

    public Node registerNode(Node node, boolean localOnly) throws NodeRegistrationException {

        if (node == null) {
            throw new NodeRegistrationException("Invalid node");
        }

        Node nodeToRegister = new SOSNode(node);

        try {
            localNodesDirectory.addNode(nodeToRegister);
            localNodesDirectory.persistNodesTable();
        } catch (NodesDirectoryException e) {
            throw new NodeRegistrationException("Unable to register node", e);
        }

        if (!localOnly) {
            Set<Node> ndsNodes = localNodesDirectory.getNDSNodes(LocalNodesDirectory.NO_LIMIT);
            ndsNodes.parallelStream()
                    .forEach(n -> registerNode(nodeToRegister, n));
        }

        return nodeToRegister;
    }

    private void registerNode(Node node, Node ndsNode) {
        SOS_LOG.log(LEVEL.INFO, "WIP - should register node: " + node.toString());

        try {
            URL url = SOSEP.NDS_REGISTER_NODE(ndsNode);
            SyncRequest request = new SyncRequest(Method.POST, url);
            request.setJSONBody(node.toString());
            Response response = RequestsManager.getInstance().playSyncRequest(request);

            if (response.getCode() == HTTPStatus.OK) {
                SOS_LOG.log(LEVEL.INFO, "Node " + node.getNodeGUID() + " was successfully registered to NDS " + ndsNode.toString());
            } else {
                SOS_LOG.log(LEVEL.WARN, "Node " + node.getNodeGUID() + " was NOT successfully registered to NDS " + ndsNode.toString());
            }

            try(InputStream ignored = response.getBody()) {} // Ensure that connection is closed properly.

        } catch (SOSURLException | IOException e) {
            e.printStackTrace();
        }

    }
}
