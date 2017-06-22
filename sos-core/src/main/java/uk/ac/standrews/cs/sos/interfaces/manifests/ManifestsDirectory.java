package uk.ac.standrews.cs.sos.interfaces.manifests;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.manifest.HEADNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.exceptions.manifest.TIPNotFoundException;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.Version;

import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface ManifestsDirectory {

    void addManifest(Manifest manifest) throws ManifestPersistException;

    Manifest findManifest(IGUID guid) throws ManifestNotFoundException;

    void advanceTip(Version version);

    Set<IGUID> getTips(IGUID invariant) throws TIPNotFoundException;

    IGUID getHead(IGUID invariant) throws HEADNotFoundException;

    void setHead(Version version);

    void flush();
}
