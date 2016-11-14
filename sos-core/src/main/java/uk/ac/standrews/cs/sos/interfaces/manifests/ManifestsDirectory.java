package uk.ac.standrews.cs.sos.interfaces.manifests;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.manifest.*;

import java.util.stream.Stream;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface ManifestsDirectory {

    void addManifest(Manifest manifest) throws ManifestPersistException;

    void updateAtom(Atom atom) throws ManifestsDirectoryException, ManifestNotFoundException;

    Manifest findManifest(IGUID guid) throws ManifestNotFoundException;

    Stream<Manifest> getAllManifests();

    Version getHEAD(IGUID invariant) throws HEADNotFoundException;

    void setHEAD(IGUID version) throws HEADNotSetException;

    void flush();
}
