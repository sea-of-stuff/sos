package uk.ac.standrews.cs.sos.interfaces.manifests;

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.model.Manifest;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface ManifestsDirectory {

    /**
     * Add the manifest to the directory
     *
     * @param manifest to be added
     * @throws ManifestPersistException if the manifest could not be persisted
     */
    void addManifest(Manifest manifest) throws ManifestPersistException;

    /**
     * Search for the manifest with the matching GUID
     *
     * @param guid of the manifest
     * @return the manifest
     * @throws ManifestNotFoundException if the manifest could not be found
     */
    Manifest findManifest(IGUID guid) throws ManifestNotFoundException;

    /**
     * Delete manifest from directory.
     *
     * @param guid of manifest
     * @throws ManifestNotFoundException if manifest was not found
     */
    void delete(IGUID guid) throws ManifestNotFoundException;

    /**
     * Persist any cache/index of the directory to disk
     */
    void flush();
}
