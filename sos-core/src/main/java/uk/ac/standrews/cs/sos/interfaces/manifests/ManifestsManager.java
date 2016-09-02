package uk.ac.standrews.cs.sos.interfaces.manifests;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;

import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface ManifestsManager {

    void addManifest(Manifest manifest) throws ManifestPersistException;

    Manifest findManifest(IGUID guid) throws ManifestNotFoundException;

    Version getLatest(IGUID guid) throws ManifestNotFoundException;

    // TODO - not 100% sure on the method below. I would prefer something more abstract on the metadata

    Collection<IGUID> findManifestsByType(String type) throws ManifestNotFoundException;

    Collection<IGUID> findVersions(IGUID guid) throws ManifestNotFoundException;

    Collection<IGUID> findManifestsThatMatchLabel(String label) throws ManifestNotFoundException;
}
