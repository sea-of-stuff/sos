package uk.ac.standrews.cs.sos.actors.protocol;

import uk.ac.standrews.cs.sos.actors.protocol.tasks.RegisterNode;
import uk.ac.standrews.cs.sos.exceptions.node.NodeRegistrationException;
import uk.ac.standrews.cs.sos.exceptions.node.NodesDirectoryException;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.node.SOSNode;
import uk.ac.standrews.cs.sos.node.directory.LocalNodesDirectory;
import uk.ac.standrews.cs.sos.tasks.TasksQueue;

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
            ndsNodes.forEach(n -> {
                RegisterNode registerNode = new RegisterNode(node, n);
                TasksQueue.instance().performSyncTask(registerNode);
            });
        }

        return nodeToRegister;
    }

}
