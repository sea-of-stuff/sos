package uk.ac.standrews.cs.sos.actors;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.actors.protocol.NodeDiscovery;
import uk.ac.standrews.cs.sos.actors.protocol.NodeRegistration;
import uk.ac.standrews.cs.sos.exceptions.node.NodeNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.node.NodeRegistrationException;
import uk.ac.standrews.cs.sos.exceptions.node.NodesDirectoryException;
import uk.ac.standrews.cs.sos.interfaces.actors.NDS;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.interfaces.node.NodesDatabase;
import uk.ac.standrews.cs.sos.node.directory.LocalNodesDirectory;

import java.util.Iterator;
import java.util.Set;

/**
 * The SOSNDS represents a basic NDS implementation.
 * It provides naive methods to register new nodes in the sos and get known nodes.
 *
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSNDS implements NDS {

    private NodeDiscovery nodeDiscovery;
    private NodeRegistration nodeRegistration;

    public SOSNDS(Node localNode, NodesDatabase nodesDatabase) {
        LocalNodesDirectory localNodesDirectory = null;
        try {
            localNodesDirectory = new LocalNodesDirectory(localNode, nodesDatabase);
            nodeDiscovery = new NodeDiscovery(localNodesDirectory);
            nodeRegistration = new NodeRegistration(localNodesDirectory);
        } catch (NodesDirectoryException e) {
            e.printStackTrace();
        }

    }

    @Override
    public Node getThisNode() {
        return nodeDiscovery.getLocalNode();
    }

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
    public Iterator<Node> getDDSNodesIterator() {
        return nodeDiscovery.getDDSNodesIterator();
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
    public Iterator<Node> getStorageNodesIterator() {
        return nodeDiscovery.getStorageNodesIterator();
    }

    @Override
    public Node registerNode(Node node, boolean localOnly) throws NodeRegistrationException {
        return nodeRegistration.registerNode(node, localOnly);
    }

}
