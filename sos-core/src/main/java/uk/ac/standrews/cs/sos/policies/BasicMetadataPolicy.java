package uk.ac.standrews.cs.sos.policies;

import uk.ac.standrews.cs.sos.interfaces.policy.MetadataPolicy;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class BasicMetadataPolicy implements MetadataPolicy {

    private int replicationFactor;

    public BasicMetadataPolicy(int replicationFactor) {
        this.replicationFactor = replicationFactor >= 0 ? replicationFactor : 0;
    }

    @Override
    public int replicationFactor() {
        return replicationFactor;
    }
}
