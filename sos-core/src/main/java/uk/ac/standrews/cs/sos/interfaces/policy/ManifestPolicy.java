package uk.ac.standrews.cs.sos.interfaces.policy;

/**
 * This interface defines the policy to manage manifests
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface ManifestPolicy extends Policy {

    /**
     * Defines whether the node applying this policy has to store the manifests locally.
     *
     * @return
     */
    boolean storeManifestsLocally();

    /**
     * Defines whether the node applying this policy has to store the manifests
     * in other nodes of the SOS
     *
     * @return
     */
    boolean storeManifestsRemotely();

    /**
     * Defines what the replication factor for manifests is
     *
     * @return
     */
    int getReplicationFactor();

}
