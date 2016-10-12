package uk.ac.standrews.cs.sos.policy;

import uk.ac.standrews.cs.sos.interfaces.policy.ManifestPolicy;
import uk.ac.standrews.cs.sos.interfaces.policy.MetadataPolicy;
import uk.ac.standrews.cs.sos.interfaces.policy.PolicyManager;
import uk.ac.standrews.cs.sos.interfaces.policy.ReplicationPolicy;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class PolicyManagerImpl implements PolicyManager {

    private ReplicationPolicy replicationPolicy;

    @Override
    public void setReplicationPolicy(ReplicationPolicy policy) {
        this.replicationPolicy = policy;
    }

    @Override
    public void setComputationPolicy(MetadataPolicy policy) {

    }

    @Override
    public void setManifestPolicy(ManifestPolicy policy) {

    }

    @Override
    public ReplicationPolicy getReplicationPolicy() {
        return replicationPolicy;
    }

    @Override
    public MetadataPolicy getComputationPolicy() {
        return null;
    }

    @Override
    public ManifestPolicy getManifestPolicy() {
        return null;
    }
}
