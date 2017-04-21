package uk.ac.standrews.cs.sos.actors;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataException;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataPersistException;
import uk.ac.standrews.cs.sos.model.Metadata;

import java.io.InputStream;

/**
 * Metadata Management Service
 * TODO -> RENAME to metadata discovery service
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface MMS extends SeaOfStuff {

    /**
     * Computes the metadata for a given input stream
     *
     * @param inputStream
     * @return
     * @throws MetadataException
     */
    Metadata processMetadata(InputStream inputStream) throws MetadataException;

    /**
     * Add the given metadata to the sea of stuff
     *
     * @param metadata to be added to the sea of stuff
     */
    void addMetadata(Metadata metadata) throws MetadataPersistException;

    /**
     * Get the metadata that matches the given GUID.
     *
     * @param guid of the metadata
     * @return metadata associated with the GUID
     * @throws MetadataNotFoundException
     */
    Metadata getMetadata(IGUID guid) throws MetadataNotFoundException;
}
