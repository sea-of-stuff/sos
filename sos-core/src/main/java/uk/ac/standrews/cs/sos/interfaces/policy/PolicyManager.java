package uk.ac.standrews.cs.sos.interfaces.policy;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface PolicyManager {

    void setDataReplicationPolicy(DataReplicationPolicy policy);

    void setMetadataPolicy(MetadataPolicy policy);

    void setManifestPolicy(ManifestPolicy policy);

    DataReplicationPolicy getDataReplicationPolicy();

    MetadataPolicy getMetadataPolicy();

    ManifestPolicy getManifestPolicy();

}
