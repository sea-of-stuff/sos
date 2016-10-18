package uk.ac.standrews.cs.sos.interfaces.manifests;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestsCacheMissException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface ManifestsCache {

    void addManifest(Manifest manifest);

    Manifest getManifest(IGUID guid) throws ManifestsCacheMissException;

    void persist();
}
