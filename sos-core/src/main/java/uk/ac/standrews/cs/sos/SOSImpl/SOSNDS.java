package uk.ac.standrews.cs.sos.SOSImpl;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.interfaces.policy.PolicyManager;
import uk.ac.standrews.cs.sos.interfaces.sos.NDS;
import uk.ac.standrews.cs.sos.node.NodeManager;

import java.util.Collection;

/**
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
    public Collection<Node> getNodes(String role) {
        return null;
    }

    @Override
    public void registerNode(Node node) {
        nodeManager.addNode(node);

        // TODO - replicate such knowledge?
    }

    @Override
    public PolicyManager getPolicyManager() {
        return null;
    }
}
