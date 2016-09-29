package uk.ac.standrews.cs.sos.web.model;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;
import uk.ac.standrews.cs.sos.interfaces.node.LocalNode;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ManifestsDAO {

    public static Manifest manifest(LocalNode sos, IGUID guid) throws ManifestNotFoundException {
        return sos.getClient().getManifest(guid);
    }
}
