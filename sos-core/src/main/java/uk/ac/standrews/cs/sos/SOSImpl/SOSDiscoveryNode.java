package uk.ac.standrews.cs.sos.SOSImpl;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.interfaces.policy.PolicyManager;
import uk.ac.standrews.cs.sos.interfaces.sos.DiscoveryNode;
import uk.ac.standrews.cs.sos.node.NodeManager;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSDiscoveryNode implements DiscoveryNode {

    private NodeManager nodeManager;

    @Override
    public Node getNode(IGUID guid) {
        return nodeManager.getNode(guid);
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
