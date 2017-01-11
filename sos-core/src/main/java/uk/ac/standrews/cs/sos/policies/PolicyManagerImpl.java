package uk.ac.standrews.cs.sos.policies;

import uk.ac.standrews.cs.sos.interfaces.policy.DataReplicationPolicy;
import uk.ac.standrews.cs.sos.interfaces.policy.ManifestPolicy;
import uk.ac.standrews.cs.sos.interfaces.policy.MetadataPolicy;
import uk.ac.standrews.cs.sos.interfaces.policy.PolicyManager;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class PolicyManagerImpl implements PolicyManager {

    private DataReplicationPolicy dataReplicationPolicy;
    private ManifestPolicy manifestPolicy;
    private MetadataPolicy metadataPolicy;

    public void setDataReplicationPolicy(DataReplicationPolicy policy) {
        this.dataReplicationPolicy = policy;
    }

    @Override
    public void setMetadataPolicy(MetadataPolicy policy) {
        this.metadataPolicy = policy;
    }

    @Override
    public void setManifestPolicy(ManifestPolicy policy) {
        this.manifestPolicy = policy;
    }

    public DataReplicationPolicy getDataReplicationPolicy() {
        return dataReplicationPolicy;
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
