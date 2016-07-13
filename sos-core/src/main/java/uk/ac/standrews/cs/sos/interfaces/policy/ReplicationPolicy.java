package uk.ac.standrews.cs.sos.interfaces.policy;

import uk.ac.standrews.cs.sos.interfaces.node.Node;

import java.util.Collection;

/**
 * This interface defines the policy settings for replicating the data.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface ReplicationPolicy extends Policy {

    /**
     * This is an integer from 0 to n indicating how many times the data should be replicated.
     * If the replication factor is 0, then it is not replicated.
     * If the replication factor is n, where n > 0, then the data is replicated to n other nodes.
     *
     * @return
     */
    int getReplicationFactor();

    /**
     * Add a node for use in the replication process.
     *
     * @param node
     */
    void addNode(Node node);

    /**
     * Nodes to be used for the replication.
     *
     * @return
     */
    Collection<Node> getNodes();

    /**
     * Replication is strong is data is replicated in at least n nodes (where n is the replication factor)
     *
     * @return true if the replication is strong
     */
    boolean isReplicationStrong();
}
