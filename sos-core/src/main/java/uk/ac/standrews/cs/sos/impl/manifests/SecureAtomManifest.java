package uk.ac.standrews.cs.sos.impl.manifests;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.crypto.ProtectionException;
import uk.ac.standrews.cs.sos.impl.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.json.SecureAtomManifestDeserializer;
import uk.ac.standrews.cs.sos.json.SecureAtomManifestSerializer;
import uk.ac.standrews.cs.sos.model.Atom;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.model.SecureManifest;
import uk.ac.standrews.cs.utilities.Pair;
import uk.ac.standrews.cs.utilities.crypto.CryptoException;
import uk.ac.standrews.cs.utilities.crypto.SymmetricEncryption;

import javax.crypto.SecretKey;
import java.io.InputStream;
import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@JsonSerialize(using = SecureAtomManifestSerializer.class)
@JsonDeserialize(using = SecureAtomManifestDeserializer.class)
public class SecureAtomManifest extends AtomManifest implements Atom, SecureManifest {

    private Role role;

    /**
     * Creates a valid atom manifest given an atom.
     *
     * TODO - is the data already stored at the specified locations?
     * TODO - atom should store only GUID to role?
     *
     * @param guid
     * @param locations
     */
    public SecureAtomManifest(IGUID guid, Set<LocationBundle> locations, Role role) {
        super(guid, locations);
        this.manifestType = ManifestType.ATOM_PROTECTED;
        this.role = role;

        // TODO - encrypt the local data!!!
        encrypt("TODO");
    }

    // TODO - use file and inputstream for data
    // 1. Generate random key K
    // 2. encrypt data with K --> d'
    // 3. encrypt k with pubkey --> (k', pubkey)
    // 4. guid = hash(d')
    private void encrypt(String data){

        try {

            SecretKey key = SymmetricEncryption.generateRandomKey();
            String encryptedData = SymmetricEncryption.encrypt(key, data);
            // TODO - save data to disk
            String encryptedKey = role.encrypt(key);
            this.guid = GUIDFactory.generateGUID(encryptedData);

        } catch (CryptoException | ProtectionException | GUIDGenerationException e) {
            e.printStackTrace();
        }

    }

    @Override
    public InputStream getData() {
        // Return the encrypted data
        // maybe there is no need to override this method
        return null;
    }

    public InputStream getData(Role role) {
        // Retruns the unencrypted data
        return null;
    }

    @Override
    public Set<Pair<String, IGUID>> keysRoles() {

        // Return the set of (encrypted key, guid of role)

        return null;
    }
}
