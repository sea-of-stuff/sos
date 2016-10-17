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
    private ManifestPolicy manifestPolicy;
    private MetadataPolicy metadataPolicy;

    @Override
    public void setReplicationPolicy(ReplicationPolicy policy) {
        this.replicationPolicy = policy;
    }

    @Override
    public void setComputationPolicy(MetadataPolicy policy) {
        this.metadataPolicy = policy;
    }

    @Override
    public void setManifestPolicy(ManifestPolicy policy) {
        this.manifestPolicy = policy;
    }

    @Override
    public ReplicationPolicy getReplicationPolicy() {
        return replicationPolicy;
    }

    @Override
    public MetadataPolicy getMetadataPolicy() {
        return metadataPolicy;
    }

    @Override
    public ManifestPolicy getManifestPolicy() {
        return manifestPolicy;
    }
}
