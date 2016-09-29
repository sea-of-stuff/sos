package uk.ac.standrews.cs.sos.interfaces.policy;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface MetadataPolicy {

    boolean computeMetadataOnBehalfOfClient();

    String metadataEngine();


}
