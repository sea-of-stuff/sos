package uk.ac.standrews.cs.sos.actors.protocol;

import uk.ac.standrews.cs.sos.exceptions.node.NodeRegistrationException;
import uk.ac.standrews.cs.sos.exceptions.node.NodesDirectoryException;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.node.SOSNode;
import uk.ac.standrews.cs.sos.node.directory.LocalNodesDirectory;

import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class NodeRegistration {

    private LocalNodesDirectory localNodesDirectory;

    public NodeRegistration(LocalNodesDirectory localNodesDirectory) {
        this.localNodesDirectory = localNodesDirectory;
    }

    public Node registerNode(Node node) throws NodeRegistrationException {

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

        Set<Node> ndsNodes = localNodesDirectory.getNDSNodes(LocalNodesDirectory.NO_LIMIT);
        ndsNodes.parallelStream()
                .forEach(n -> registerNode(node, n));

        return node;
    }

    private void registerNode(Node node, Node ndsNode) {
        System.out.println("WIP - should register node: " + node.toString());
    }
}
