package uk.ac.standrews.cs.sos.node;

import com.j256.ormlite.logger.LocalLog;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.NodeManagerException;
import uk.ac.standrews.cs.sos.exceptions.db.DatabaseConnectionException;
import uk.ac.standrews.cs.sos.exceptions.db.DatabasePersistenceException;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.interfaces.node.NodeDatabase;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

/**
 * This is the node manager for this node. That is, it keeps track of the known nodes.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class NodeManager {

    private Node localNode;
    private NodeDatabase nodeDatabase;

    private Collection<Node> knownNodes;

    public NodeManager(Node localNode, NodeDatabase nodeDatabase) throws NodeManagerException {
        this.localNode = localNode;
        this.nodeDatabase = nodeDatabase;

        this.knownNodes = new HashSet<>();

        // Setting log-level for ORMLite to ERROR
        System.setProperty(LocalLog.LOCAL_LOG_LEVEL_PROPERTY, "ERROR");
        loadNodesFromDB();
    }

    /**
     * Add an arbitrary node to the manager.
     * This will be used to discovery nodes/data in the LocalSOSNode.
     *
     * @param node
     */
    public void addNode(Node node) {
        knownNodes.add(node);
    }

    /**
     * Get all known nodes.
     *
     * @return
     */
    public Collection<Node> getKnownNodes() {
        return knownNodes;
    }

    /**
     * Get a LocalSOSNode node given its guid identifier.
     *
     * @param guid
     * @return
     */
    public Node getNode(IGUID guid) {
        Optional<Node> node = knownNodes.stream()
                .filter(n -> n.getNodeGUID().equals(guid))
                .findFirst();

        return node.isPresent() ? node.get() : null;
    }

    public Node getLocalNode() {
        return this.localNode;
    }

    /**
     * Persist the collection of known nodes.
     *
     * @throws DatabasePersistenceException
     */
    public void persistNodesTable() throws NodeManagerException {
        try {
            for (Node knownNode : knownNodes) {
                nodeDatabase.addNode(knownNode);
            }
        } catch (DatabaseConnectionException e) {
            throw new NodeManagerException(e);
        }
    }

    /**
     * Load nodes from the DB to the node manager (in memory)
     * @throws NodeManagerException
     */
    private void loadNodesFromDB() throws NodeManagerException {
        try {
            Collection<SOSNode> nodes = nodeDatabase.getNodes();
            knownNodes.addAll(nodes);
        } catch (DatabaseConnectionException e) {
            throw new NodeManagerException(e);
        }
    }
}
