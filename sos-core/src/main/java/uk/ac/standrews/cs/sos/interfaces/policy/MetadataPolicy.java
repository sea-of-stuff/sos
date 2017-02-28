package uk.ac.standrews.cs.sos.interfaces.policy;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface MetadataPolicy extends Policy {

    /**
     * Replication factor for metadata manifest.
     * Metadata will be replicated to DDS nodes.
     *
     * @return
     */
    int replicationFactor();

}
