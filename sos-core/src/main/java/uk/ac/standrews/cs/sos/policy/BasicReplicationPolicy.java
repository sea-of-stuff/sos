package uk.ac.standrews.cs.sos.policy;

import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.interfaces.policy.ReplicationPolicy;

import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class BasicReplicationPolicy implements ReplicationPolicy {

    private int replicationFactor;
    private Collection<Node> nodes = new LinkedHashSet<>();

    /**
     * Create a policy with a given replication factor and the nodes to use fo the replication.
     *
     * @param replicationFactor (zeroed if negative)
     * @param nodes (ignored if null or empty)
     */
    public BasicReplicationPolicy(int replicationFactor, Collection<Node> nodes) {
        this.replicationFactor = replicationFactor >= 0 ? replicationFactor : 0;

        if (nodes != null && !nodes.isEmpty()) {
            this.nodes.addAll(nodes);
        }
    }

    @Override
    public int getReplicationFactor() {
        return replicationFactor;
    }

    @Override
    public void addNode(Node node) {
        nodes.add(node);
    }

    @Override
    public Collection<Node> getNodes() {
        return nodes;
    }

    @Override
    public boolean isReplicationStrong() {
        return false;
    }
}
