package uk.ac.standrews.cs.sos.policy;

import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.interfaces.policy.ReplicationPolicy;

import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class BasicReplicationPolicy implements ReplicationPolicy {

    private int replicationFactor;
    private Collection<Node> nodes;

    /**
     * Create a policy with a given replication factor and the nodes to use fo the replication.
     *
     * @param replicationFactor
     * @param nodes
     */
    public BasicReplicationPolicy(int replicationFactor, Collection<Node> nodes) {
        this.replicationFactor = replicationFactor;
        this.nodes = nodes;
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
