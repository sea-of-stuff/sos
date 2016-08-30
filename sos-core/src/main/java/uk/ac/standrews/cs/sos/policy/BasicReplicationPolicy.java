package uk.ac.standrews.cs.sos.policy;

import uk.ac.standrews.cs.sos.interfaces.policy.ReplicationPolicy;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class BasicReplicationPolicy implements ReplicationPolicy {

    private int replicationFactor;

    /**
     * Create a policy with a given replication factor and the nodes to use fo the replication.
     *
     * @param replicationFactor (zeroed if negative)
     */
    public BasicReplicationPolicy(int replicationFactor) {
        this.replicationFactor = replicationFactor >= 0 ? replicationFactor : 0;
    }

    @Override
    public int getReplicationFactor() {
        return replicationFactor;
    }

    @Override
    public boolean isReplicationStrong() {
        return false;
    }

    @Override
    public int getMaximumAllowedReplicationFactor() {
        return 0;
    }
}
