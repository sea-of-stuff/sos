package uk.ac.standrews.cs.sos.interfaces.manifests;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.model.Version;

import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface ManifestsDirectory {

    void addManifest(Manifest manifest) throws ManifestPersistException;

    Manifest findManifest(IGUID guid) throws ManifestNotFoundException;

    Set<IGUID> getHeads(IGUID invariant);

    IGUID getCurrent(Role role, IGUID invariant);

    void setCurrent(Role role, Version version);

    void flush();
}
