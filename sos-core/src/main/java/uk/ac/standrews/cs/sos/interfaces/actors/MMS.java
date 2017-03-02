package uk.ac.standrews.cs.sos.interfaces.actors;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataException;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataPersistException;
import uk.ac.standrews.cs.sos.interfaces.model.SOSMetadata;

import java.io.InputStream;

/**
 * Metadata Management Service
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface MMS extends SeaOfStuff {

    /**
     * Add the given metadata to the sea of stuff
     * @param metadata to be added to the sea of stuff
     */
    void addMetadata(SOSMetadata metadata) throws MetadataPersistException;

    /**
     * Get the metadata that matches the given GUID.
     *
     * @param guid of the metadata
     * @return metadata associated with the GUID
     * @throws MetadataNotFoundException
     */
    SOSMetadata getMetadata(IGUID guid) throws MetadataNotFoundException;

    SOSMetadata processMetadata(InputStream inputStream) throws MetadataException;
}
