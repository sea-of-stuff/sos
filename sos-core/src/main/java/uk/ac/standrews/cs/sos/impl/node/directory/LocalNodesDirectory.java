package uk.ac.standrews.cs.sos.impl.node.directory;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.db.DatabaseConnectionException;
import uk.ac.standrews.cs.sos.exceptions.node.NodesDirectoryException;
import uk.ac.standrews.cs.sos.impl.node.SOSNode;
import uk.ac.standrews.cs.sos.interfaces.node.Database;
import uk.ac.standrews.cs.sos.model.Node;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * The nodes directory which keeps track of the known nodes at this given node
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class LocalNodesDirectory {

    public static final int NO_LIMIT = 0;

    private Node localNode;
    private Database database;

    private Set<Node> knownNodes;

    public LocalNodesDirectory(Node localNode, Database database) throws NodesDirectoryException {
        this.localNode = localNode;
        this.database = database;

        this.knownNodes = new HashSet<>(); // Order not preserved
        loadNodesFromDB();
    }

    /**
     * Add an arbitrary node to the directory.
     * This will be used to discovery nodes/data in the LocalSOSNode.
     *
     * @param node to be added
     */
    public void addNode(Node node) {

        synchronized (knownNodes) {
            SOSNode clone = new SOSNode(node);
            if (knownNodes.contains(node)) {
                knownNodes.remove(node);
            }
            knownNodes.add(clone);
        }

    }

    /**
     * Get all known nodes.
     *
     * @return set of known nodes
     */
    public Set<Node> getKnownNodes() {
        return knownNodes;
    }

    /**
     * Get a Node node given its guid identifier.
     *
     * @param guid for the node
     * @return matching node
     */
    public Node getNode(IGUID guid) {
        Optional<Node> node = knownNodes.stream()
                .filter(n -> n.getNodeGUID().equals(guid))
                .findFirst();

        return node.orElse(null);
    }

    /**
     * Get the nodes matching the predicate and within the given limit
     *
     * @param predicate (e.g. Node::isAgent, Node::isStorage)
     * @param limit max number of nodes to return, ignore if limit <= 0
     * @return set of nodes
     */
    public Set<Node> getNodes(Predicate<Node> predicate, int limit) {

        Stream<Node> nodesStream = knownNodes.stream()
                .filter(predicate);

        if (getLocalNode() != null) {
            nodesStream = nodesStream.filter(n -> !n.getNodeGUID().equals(getLocalNode().getNodeGUID()));
        }

        nodesStream = nodesStream.distinct();

        if (limit > NO_LIMIT) {
            nodesStream = nodesStream.limit(limit);
        }

        List<Node> nodes = nodesStream.collect(toList());
        Collections.shuffle(nodes); // Naive load balancing

        return new HashSet<>(nodes);
    }

    /**
     * Get the local node running
     *
     * @return local node
     */
    public Node getLocalNode() {
        return this.localNode;
    }

    /**
     * Persist the collection of known nodes.
     *
     * @throws NodesDirectoryException if nodes table cannot be persisted
     */
    public void persistNodesTable() throws NodesDirectoryException {
        try {
            for (Node knownNode : knownNodes) {
                database.addNode(knownNode);
            }
        } catch (DatabaseConnectionException e) {
            throw new NodesDirectoryException(e);
        }
    }

    /**
     * Load nodes from the DB to the node directory (in memory)
     *
     * @throws NodesDirectoryException if nodes cannot be loaded
     */
    private void loadNodesFromDB() throws NodesDirectoryException {
        try {
            Set<SOSNode> nodes = database.getNodes();
            knownNodes.addAll(nodes);
        } catch (DatabaseConnectionException e) {
            throw new NodesDirectoryException(e);
        }
    }
}
