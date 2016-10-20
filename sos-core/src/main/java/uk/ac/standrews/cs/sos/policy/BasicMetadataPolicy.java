package uk.ac.standrews.cs.sos.policy;

import uk.ac.standrews.cs.sos.interfaces.policy.MetadataPolicy;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class BasicMetadataPolicy implements MetadataPolicy {

    @Override
    public boolean computeMetadataOnBehalfOfClient() {
        return false;
    }

    @Override
    public String metadataEngine() {
        return null;
    }

    @Override
    public int replicationFactor() {
        return 0;
    }
}
