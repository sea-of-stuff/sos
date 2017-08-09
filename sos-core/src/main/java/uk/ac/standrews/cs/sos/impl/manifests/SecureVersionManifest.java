package uk.ac.standrews.cs.sos.impl.manifests;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.json.SecureVersionManifestDeserializer;
import uk.ac.standrews.cs.sos.json.SecureVersionManifestSerializer;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.model.SecureVersion;

import java.util.HashMap;
import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@JsonSerialize(using = SecureVersionManifestSerializer.class)
@JsonDeserialize(using = SecureVersionManifestDeserializer.class)
public class SecureVersionManifest extends VersionManifest implements SecureVersion {

    private HashMap<IGUID, String> rolesToKeys;

    public SecureVersionManifest(IGUID invariant, IGUID content, Set<IGUID> prevs, IGUID metadata, Role signer, HashMap<IGUID, String> rolesToKeys) throws ManifestNotMadeException {
        super(invariant, content, prevs, metadata, signer);

        this.manifestType = ManifestType.VERSION_PROTECTED;
        this.rolesToKeys = rolesToKeys;
    }

    public SecureVersionManifest(IGUID invariant, IGUID version, IGUID content, Set<IGUID> prevs, IGUID metadata,
                                 Role signer, String signature, HashMap<IGUID, String> rolesToKeys) throws ManifestNotMadeException {
        super(invariant, version, content, prevs, metadata, signer, signature);

        this.manifestType = ManifestType.VERSION_PROTECTED;
        this.rolesToKeys = rolesToKeys;
    }

    @Override
    public HashMap<IGUID, String> keysRoles() {
        return rolesToKeys;
    }

    @Override
    public void setKeysRoles(HashMap<IGUID, String> keysRoles) {
        this.rolesToKeys = keysRoles;
    }
}
