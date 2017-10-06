package uk.ac.standrews.cs.sos.impl.data;

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.bundles.LocationBundle;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class StoredAtomInfo {

    private IGUID guid;
    private LocationBundle locationBundle;
    private IGUID role;
    private String encryptedKey;

    public IGUID getGuid() {
        return guid;
    }

    public StoredAtomInfo setGuid(IGUID guid) {
        this.guid = guid;

        return this;
    }

    public LocationBundle getLocationBundle() {
        return locationBundle;
    }

    public StoredAtomInfo setLocationBundle(LocationBundle locationBundle) {
        this.locationBundle = locationBundle;

        return this;
    }

    public IGUID getRole() {
        return role;
    }

    public StoredAtomInfo setRole(IGUID role) {
        this.role = role;

        return this;
    }

    public String getEncryptedKey() {
        return encryptedKey;
    }

    public StoredAtomInfo setEncryptedKey(String encryptedKey) {
        this.encryptedKey = encryptedKey;

        return this;
    }
}
