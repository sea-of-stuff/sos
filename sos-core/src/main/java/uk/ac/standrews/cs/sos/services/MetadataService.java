package uk.ac.standrews.cs.sos.services;

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataException;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataPersistException;
import uk.ac.standrews.cs.sos.impl.metadata.MetadataBuilder;
import uk.ac.standrews.cs.sos.model.Metadata;
import uk.ac.standrews.cs.sos.model.NodesCollection;

/**
 * Metadata Management Service (MMS)
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface MetadataService extends Service {

    /**
     * Computes the metadata for some given data
     *
     * @param metadataBuilder containing info for metadata
     * @return metadata object
     * @throws MetadataException if metadata could not be processed
     */
    Metadata processMetadata(MetadataBuilder metadataBuilder) throws MetadataException;

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
     * @throws MetadataNotFoundException if not found
     */
    Metadata getMetadata(IGUID guid) throws MetadataNotFoundException;

    /**
     * Get the metadata that matches the given GUID and that is stored within a given nodes collection
     *
     * @param nodesCollection within which the metadata should be stored
     * @param guid of the metadata
     * @return the metadata
     * @throws MetadataNotFoundException if the metadata could not be found
     */
    Metadata getMetadata(NodesCollection nodesCollection, IGUID guid) throws MetadataNotFoundException;
}
