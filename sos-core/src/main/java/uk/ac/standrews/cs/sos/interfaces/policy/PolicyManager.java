package uk.ac.standrews.cs.sos.interfaces.policy;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface PolicyManager {

    void setReplicationPolicy(ReplicationPolicy policy);

    void setComputationPolicy(ComputationPolicy policy);

    void setManifestPolicy(ManifestPolicy policy);

    ReplicationPolicy getReplicationPolicy();

    ComputationPolicy getComputationPolicy();

    ManifestPolicy getManifestPolicy();
}
