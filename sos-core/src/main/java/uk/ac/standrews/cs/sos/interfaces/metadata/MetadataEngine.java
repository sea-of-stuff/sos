package uk.ac.standrews.cs.sos.interfaces.metadata;

import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataException;
import uk.ac.standrews.cs.sos.impl.metadata.MetadataBuilder;
import uk.ac.standrews.cs.sos.model.Metadata;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface MetadataEngine {

    /**
     * Process the given data and generates metadata from it.
     *
     * @param metadataBuilder containing info about metadata to be processed
     * @return the metadata
     * @throws MetadataException if the metadata could not be generated
     */
    Metadata processData(MetadataBuilder metadataBuilder) throws MetadataException;
}
