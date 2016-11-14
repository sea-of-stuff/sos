package uk.ac.standrews.cs.sos.SOSImpl;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.node.NodeManagerException;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.interfaces.sos.NDS;
import uk.ac.standrews.cs.sos.node.NodesDirectory;
import uk.ac.standrews.cs.sos.node.SOSNode;

import java.util.Collection;

/**
 * The SOSNDS represents a basic NDS implementation.
 * It provides naive methods to register new nodes in the sos and get known nodes.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSNDS implements NDS {

    private NodesDirectory nodesDirectory;

    public SOSNDS(NodesDirectory nodesDirectory) {
        this.nodesDirectory = nodesDirectory;
    }

    @Override
    public Node getNode(IGUID guid) {
        return nodesDirectory.getNode(guid);
    }

    @Override
    public Collection<Node> getNDSNodes() {
        return nodesDirectory.getNDSNodes();
    }

    @Override
    public Collection<Node> getDDSNodes() {
        return nodesDirectory.getDDSNodes();
    }

    @Override
    public Collection<Node> getMCSNodes() {
        return nodesDirectory.getMCSNodes();
    }

    @Override
    public Collection<Node> getStorageNodes() {
        return nodesDirectory.getStorageNodes();
    }

    @Override
    public Node registerNode(Node node) {
        Node nodeToRegister = new SOSNode(node);
        nodesDirectory.addNode(nodeToRegister);

        try {
            nodesDirectory.persistNodesTable();
        } catch (NodeManagerException e) {
            e.printStackTrace();
            // TODO - throw appropriate exception
            return null;
        }

        // TODO - perform replication across other NDS nodes
        return nodeToRegister;
    }

}
