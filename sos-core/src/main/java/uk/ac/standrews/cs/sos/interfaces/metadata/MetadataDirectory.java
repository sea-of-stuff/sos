package uk.ac.standrews.cs.sos.interfaces.metadata;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataException;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataPersistException;
import uk.ac.standrews.cs.sos.interfaces.model.Metadata;

import java.io.InputStream;
import java.util.List;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface MetadataDirectory { // TODO - metadata manifests stored same as other manifests!!!!

    Metadata processMetadata(InputStream inputStream) throws MetadataException;

    /**
     * cache and store metadata
     * @param metadata
     */
    void addMetadata(Metadata metadata) throws MetadataPersistException;

    /**
     * Get the metadata that matches the specified GUID
     * @param guid
     * @return
     */
    Metadata getMetadata(IGUID guid) throws MetadataNotFoundException;

    /**
     * Get all versions that match the given attribute and value
     * @param attribute
     * @param value
     * @return
     */
    List<IGUID> getVersions(String attribute, String value);

    // how to query for metadata? triplestore? something else?
}
