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
public class SecureCompoundManifest extends CompoundManifest implements Compound, SecureManifest {

    public SecureCompoundManifest(CompoundType type, Set<Content> contents, Role signer) throws ManifestNotMadeException {
        super(type, contents, signer);
    }

    public SecureCompoundManifest(CompoundType type, IGUID contentGUID, Set<Content> contents, Role signer, String signature) throws ManifestNotMadeException {
        super(type, contentGUID, contents, signer, signature);
    }

    @Override
    public HashMap<IGUID, String> keysRoles() {
        return null;
    }
}
