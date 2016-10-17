package uk.ac.standrews.cs.sos.interfaces.metadata;

import uk.ac.standrews.cs.sos.exceptions.metadata.SOSMetadataException;

import java.io.InputStream;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface MetadataManager {

    /**
     * Process, cache and store metadata
     * @param inputStream
     */
    SOSMetadata addMetadata(InputStream inputStream) throws SOSMetadataException;

    // getters, given meta information
}
