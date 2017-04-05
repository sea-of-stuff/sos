package uk.ac.standrews.cs.sos.interfaces.metadata;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.model.Metadata;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface MetadataCache {

    void addMetadata(Metadata metadata);

    Metadata getMetadata(IGUID guid); // throws ManifestsCacheMissException;

    void persist();
}
