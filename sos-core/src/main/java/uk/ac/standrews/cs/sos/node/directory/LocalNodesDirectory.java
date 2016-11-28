package uk.ac.standrews.cs.sos.node.directory;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.db.DatabaseConnectionException;
import uk.ac.standrews.cs.sos.exceptions.node.NodesDirectoryException;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.interfaces.node.NodesDatabase;
import uk.ac.standrews.cs.sos.node.SOSNode;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * This is the node directory for this node, which keeps track of the known nodes.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class LocalNodesDirectory {

    private static final int NO_LIMIT = 0;

    private Node localNode;
    private NodesDatabase nodesDatabase;

    private Set<Node> knownNodes;

    public LocalNodesDirectory(Node localNode, NodesDatabase nodesDatabase) throws NodesDirectoryException {
        this.localNode = localNode;
        this.nodesDatabase = nodesDatabase;

        this.knownNodes = new HashSet<>();
        loadNodesFromDB();
    }

    /**
     * Add an arbitrary node to the directory.
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
    public Set<Node> getKnownNodes() {
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

    /**
     * Get all NDS Nodes
     *
     * @return NDS nodes
     */
    public Set<Node> getNDSNodes() {
        return getNodes(Node::isNDS, NO_LIMIT);
    }

    /**
     * Get all DDS Nodes
     *
     * @return DDS nodes
     */
    public Set<Node> getDDSNodes() {
        return getNodes(Node::isDDS, NO_LIMIT);
    }

    /**
     * Get all MCS Nodes
     *
     * @return MCS nodes
     */
    public Set<Node> getMCSNodes() {
        return getNodes(Node::isMCS, NO_LIMIT);
    }

    /**
     * Get all Storage Nodes
     * @return Storage nodes
     */
    public Set<Node> getStorageNodes() {
        return getNodes(Node::isStorage, NO_LIMIT);
    }

    private Set<Node> getNodes(Predicate<Node> predicate, int limit) {

        Stream<Node> nodesStream = knownNodes.parallelStream()
                .filter(predicate)
                .distinct();

        if (limit > 0) {
            nodesStream = nodesStream.limit(limit);
        }

        List<Node> nodes = nodesStream.collect(toList());
        Collections.shuffle(nodes); // Naive load balancing

        return new HashSet<>(nodes);
    }

    /**
     * Get the local node running
     * @return
     */
    public Node getLocalNode() {
        return this.localNode;
    }

    /**
     * Persist the collection of known nodes.
     *
     * @throws NodesDirectoryException
     */
    public void persistNodesTable() throws NodesDirectoryException {
        try {
            for (Node knownNode : knownNodes) {
                nodesDatabase.addNode(knownNode); // FIXME - do not persist nodes that have not changed
            }
        } catch (DatabaseConnectionException e) {
            throw new NodesDirectoryException(e);
        }
    }

    /**
     * Load nodes from the DB to the node directory (in memory)
     * @throws NodesDirectoryException
     */
    private void loadNodesFromDB() throws NodesDirectoryException {
        try {
            Set<SOSNode> nodes = nodesDatabase.getNodes();
            knownNodes.addAll(nodes);
        } catch (DatabaseConnectionException e) {
            throw new NodesDirectoryException(e);
        }
    }
}
