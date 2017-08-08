package uk.ac.standrews.cs.sos.impl.manifests;

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.model.*;

import java.util.HashMap;
import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
// TODO - json serialise/deserialise
public class SecureCompoundManifest extends CompoundManifest implements SecureCompound {

    private HashMap<IGUID, String> rolesToKeys;

    public SecureCompoundManifest(CompoundType type, Set<Content> contents, Role signer, HashMap<IGUID, String> rolesToKeys) throws ManifestNotMadeException {
        super(type, contents, signer);

        this.manifestType = ManifestType.COMPOUND_PROTECTED;
        this.rolesToKeys = rolesToKeys;
    }

    public SecureCompoundManifest(CompoundType type, IGUID contentGUID, Set<Content> contents, Role signer, String signature, HashMap<IGUID, String> rolesToKeys) throws ManifestNotMadeException {
        super(type, contentGUID, contents, signer, signature);

        this.manifestType = ManifestType.COMPOUND_PROTECTED;
        this.rolesToKeys = rolesToKeys;
    }

    @Override
    public HashMap<IGUID, String> keysRoles() {
        return rolesToKeys;
    }
}
