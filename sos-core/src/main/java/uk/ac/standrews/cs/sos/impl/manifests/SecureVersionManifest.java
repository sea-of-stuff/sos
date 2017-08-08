package uk.ac.standrews.cs.sos.impl.manifests;

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.model.SecureVersion;

import java.util.HashMap;
import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SecureVersionManifest extends VersionManifest implements SecureVersion {

    private HashMap<IGUID, String> rolesToKeys;

    public SecureVersionManifest(IGUID invariant, IGUID content, Set<IGUID> prevs, IGUID metadata, Role signer, HashMap<IGUID, String> rolesToKeys) throws ManifestNotMadeException {
        super(invariant, content, prevs, metadata, signer);

        this.manifestType = ManifestType.VERSION_PROTECTED;
        this.rolesToKeys = rolesToKeys;
    }

    @Override
    public HashMap<IGUID, String> keysRoles() {
        return rolesToKeys;
    }
}
