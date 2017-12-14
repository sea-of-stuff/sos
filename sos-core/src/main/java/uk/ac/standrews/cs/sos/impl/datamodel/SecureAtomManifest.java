package uk.ac.standrews.cs.sos.impl.datamodel;

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.model.SecureAtom;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SecureAtomManifest extends AtomManifest implements SecureAtom {

    private HashMap<IGUID, String> rolesToKeys;

    /**
     * Creates a valid secure atom manifest
     *
     * @param guid of protected atom
     * @param locations where protected atom is stored
     * @param rolesToKeys for protected atom
     */
    public SecureAtomManifest(IGUID guid, Set<LocationBundle> locations, HashMap<IGUID, String> rolesToKeys) {
        super(guid, locations);
        this.manifestType = ManifestType.ATOM_PROTECTED;
        this.rolesToKeys = rolesToKeys;
    }

    public SecureAtomManifest(IGUID guid, Set<LocationBundle> locations) {
        super(guid, locations);
        this.manifestType = ManifestType.ATOM_PROTECTED;
        this.rolesToKeys = new LinkedHashMap<>();
    }

    @Override
    public HashMap<IGUID, String> keysRoles() {

        return rolesToKeys;
    }

    @Override
    public void setKeysRoles(HashMap<IGUID, String> keysRoles) {
        this.rolesToKeys = keysRoles;
    }

    @Override
    public void addKeyRole(IGUID role, String encryptedKey) {
        this.rolesToKeys.put(role, encryptedKey);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SecureAtomManifest that = (SecureAtomManifest) o;
        return Objects.equals(rolesToKeys, that.rolesToKeys);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), rolesToKeys);
    }
}
