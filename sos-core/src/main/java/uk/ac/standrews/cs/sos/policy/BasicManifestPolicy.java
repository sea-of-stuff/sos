package uk.ac.standrews.cs.sos.policy;

import uk.ac.standrews.cs.sos.interfaces.policy.ManifestPolicy;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class BasicManifestPolicy implements ManifestPolicy {

    private boolean storeManifestsLocally;
    private boolean storeManifestsRemotely;
    private int replicationFactor;

    /**
     *
     * @param storeLocally
     * @param storeRemotely
     * @param replicationFactor (zeroed if negative)
     */
    public BasicManifestPolicy(boolean storeLocally, boolean storeRemotely, int replicationFactor) {
        this.storeManifestsLocally = storeLocally;
        this.storeManifestsRemotely = storeRemotely;
        this.replicationFactor = replicationFactor >= 0 ? replicationFactor : 0;
    }

    @Override
    public boolean storeManifestsLocally() {
        return storeManifestsLocally;
    }

    @Override
    public boolean storeManifestsRemotely() {
        return storeManifestsRemotely;
    }

    @Override
    public int getReplicationFactor() {
        return replicationFactor;
    }
}
