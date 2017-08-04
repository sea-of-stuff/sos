package uk.ac.standrews.cs.sos.impl.manifests;

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.model.SecureManifest;
import uk.ac.standrews.cs.sos.model.Version;

import java.util.HashMap;
import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SecureVersionManifest extends VersionManifest implements Version, SecureManifest {

    public SecureVersionManifest(IGUID invariant, IGUID content, Set<IGUID> prevs, IGUID metadata, Role signer) throws ManifestNotMadeException {
        super(invariant, content, prevs, metadata, signer);
    }

    @Override
    public HashMap<IGUID, String> keysRoles() {
        return null;
    }
}
