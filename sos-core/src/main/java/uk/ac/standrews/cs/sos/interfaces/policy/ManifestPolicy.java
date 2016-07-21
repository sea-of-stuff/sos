package uk.ac.standrews.cs.sos.interfaces.policy;

/**
 * This interface defines the policy to manage manifests
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface ManifestPolicy extends Policy {

    boolean storeManifestsLocally();

    boolean storeManifestsRemotely();

    int getReplicationFactor();
}
