package uk.ac.standrews.cs.sos.interfaces.manifests.managers;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;

import java.util.stream.Stream;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface ManifestsManager {

    void addManifest(Manifest manifest) throws ManifestPersistException;

    Manifest findManifest(IGUID guid) throws ManifestNotFoundException;

    Stream<Manifest> getAllManifests();
}
