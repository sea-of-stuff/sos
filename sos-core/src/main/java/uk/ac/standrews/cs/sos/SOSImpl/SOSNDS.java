package uk.ac.standrews.cs.sos.SOSImpl;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.node.NodeManagerException;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.interfaces.policy.PolicyManager;
import uk.ac.standrews.cs.sos.interfaces.sos.NDS;
import uk.ac.standrews.cs.sos.node.NodeManager;

import java.util.Collection;

/**
 * The SOSNDS represents a basic NDS implementation.
 * It provides naive methods to register new nodes in the sos and get known nodes.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSNDS implements NDS {

    private NodeManager nodeManager;

    public SOSNDS(NodeManager nodeManager) {
        this.nodeManager = nodeManager;
    }

    @Override
    public Node getNode(IGUID guid) {
        return nodeManager.getNode(guid);
    }

    @Override
    public Collection<Node> getNDSNodes() {
        return nodeManager.getNDSNodes();
    }

    @Override
    public Collection<Node> getDDSNodes() {
        return nodeManager.getDDSNodes();
    }

    @Override
    public Collection<Node> getMCSNodes() {
        return nodeManager.getMCSNodes();
    }

    @Override
    public Collection<Node> getStorageNodes() {
        return nodeManager.getStorageNodes();
    }

    @Override
    public boolean registerNode(Node node) {
        nodeManager.addNode(node);

        try {
            nodeManager.persistNodesTable();
        } catch (NodeManagerException e) {
            e.printStackTrace();
            // TODO - throw appropriate exception
            return false;
        }

        // TODO - perform replication across other NDS nodes
        return true;
    }

    @Override
    public PolicyManager getPolicyManager() {
        return null;
    }
}
