package uk.ac.standrews.cs.sos.actors;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.actors.protocol.NodeDiscovery;
import uk.ac.standrews.cs.sos.exceptions.node.NodeNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.node.NodesDirectoryException;
import uk.ac.standrews.cs.sos.interfaces.actors.NDS;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.node.SOSNode;
import uk.ac.standrews.cs.sos.node.directory.LocalNodesDirectory;

import java.util.Collection;

/**
 * The SOSNDS represents a basic NDS implementation.
 * It provides naive methods to register new nodes in the sos and get known nodes.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSNDS implements NDS {

    private LocalNodesDirectory localNodesDirectory;
    private NodeDiscovery nodeDiscovery;

    public SOSNDS(LocalNodesDirectory localNodesDirectory) {
        this.localNodesDirectory = localNodesDirectory;

        nodeDiscovery = new NodeDiscovery(localNodesDirectory);
    }

    @Override
    public Node getThisNode() {
        return localNodesDirectory.getLocalNode();
    }

    @Override
    public Node getNode(IGUID guid) throws NodeNotFoundException {

        return nodeDiscovery.findNode(guid);
    }

    @Override
    public Collection<Node> getNDSNodes() {
        return localNodesDirectory.getNDSNodes();
    }

    @Override
    public Collection<Node> getDDSNodes() {
        return localNodesDirectory.getDDSNodes();
    }

    @Override
    public Collection<Node> getMCSNodes() {
        return localNodesDirectory.getMCSNodes();
    }

    @Override
    public Collection<Node> getStorageNodes() {
        return localNodesDirectory.getStorageNodes();
    }

    @Override
    public Node registerNode(Node node) {
        Node nodeToRegister = new SOSNode(node);
        localNodesDirectory.addNode(nodeToRegister);

        try {
            localNodesDirectory.persistNodesTable();
        } catch (NodesDirectoryException e) {
            e.printStackTrace();
            // TODO - throw appropriate exception
            return null;
        }

        // TODO - perform replication across other NDS nodes
        return nodeToRegister;
    }

}
