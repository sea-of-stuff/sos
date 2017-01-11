package uk.ac.standrews.cs.sos.policies;

import uk.ac.standrews.cs.sos.interfaces.policy.DataReplicationPolicy;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class BasicDataReplicationPolicy implements DataReplicationPolicy {

    private int replicationFactor;

    /**
     * Create a policy with a given replication factor and the nodes to use fo the replication.
     *
     * @param replicationFactor (zeroed if negative)
     */
    public BasicDataReplicationPolicy(int replicationFactor) {
        this.replicationFactor = replicationFactor > 0 ? replicationFactor : 0;
    }

    @Override
    public int getReplicationFactor() {
        return replicationFactor;
    }

}
