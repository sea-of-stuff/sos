package uk.ac.standrews.cs.sos.actors;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.actors.protocol.NodeDiscovery;
import uk.ac.standrews.cs.sos.actors.protocol.NodeRegistration;
import uk.ac.standrews.cs.sos.exceptions.node.NodeNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.node.NodeRegistrationException;
import uk.ac.standrews.cs.sos.interfaces.actors.NDS;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.node.directory.LocalNodesDirectory;

import java.util.Set;

/**
 * The SOSNDS represents a basic NDS implementation.
 * It provides naive methods to register new nodes in the sos and get known nodes.
 *
 * TODO - methods with limit params
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSNDS implements NDS {

    private LocalNodesDirectory localNodesDirectory;
    private NodeDiscovery nodeDiscovery;
    private NodeRegistration nodeRegistration;

    public SOSNDS(LocalNodesDirectory localNodesDirectory) {
        this.localNodesDirectory = localNodesDirectory; // TODO - do not ahve this here

        nodeDiscovery = new NodeDiscovery(localNodesDirectory);
        nodeRegistration = new NodeRegistration(localNodesDirectory);
    }

    @Override
    public Node getThisNode() {
        return localNodesDirectory.getLocalNode();
    } // TODO - use discoverynode

    @Override
    public Node getNode(IGUID guid) throws NodeNotFoundException {
        return nodeDiscovery.findNode(guid);
    }

    @Override
    public Set<Node> getNDSNodes() {
        return nodeDiscovery.getNDSNodes();
    }

    @Override
    public Set<Node> getDDSNodes() {
        return nodeDiscovery.getDDSNodes();
    }

    @Override
    public Set<Node> getMCSNodes() {
        return nodeDiscovery.getMCSNodes();
    }

    @Override
    public Set<Node> getStorageNodes() {
        return nodeDiscovery.getStorageNodes();
    }

    @Override
    public Node registerNode(Node node) throws NodeRegistrationException {

        Node registeredNode = nodeRegistration.registerNode(node);
        return registeredNode;
    }

}
