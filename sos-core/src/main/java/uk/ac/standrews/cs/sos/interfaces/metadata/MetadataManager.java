package uk.ac.standrews.cs.sos.interfaces.metadata;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.metadata.SOSMetadataException;

import java.io.InputStream;
import java.util.List;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface MetadataManager {

    SOSMetadata processMetadata(InputStream inputStream) throws SOSMetadataException;

    /**
     * cache and store metadata
     * @param metadata
     */
    void addMetadata(SOSMetadata metadata);

    /**
     * Get the metadata that matches the specified GUID
     * @param guid
     * @return
     */
    SOSMetadata getMetadata(IGUID guid);

    /**
     * Get all versions that match the given attribute and value
     * @param attribute
     * @param value
     * @return
     */
    List<IGUID> getVersions(String attribute, String value);

    // how to query for metadata? triplestore? something else?
}
