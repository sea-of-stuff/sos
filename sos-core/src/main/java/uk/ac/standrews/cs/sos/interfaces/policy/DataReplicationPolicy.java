package uk.ac.standrews.cs.sos.interfaces.policy;

/**
 * This interface defines the policy settings for replicating the data.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface DataReplicationPolicy extends Policy {

    /**
     * This is an integer from 0 to n indicating how many times the data should be replicated.
     * If the replication factor is 0, then data is not replicated.
     * If the replication factor is n, where n > 0, then the data is replicated to n other nodes.
     *
     * @return
     */
    int getReplicationFactor();

}
