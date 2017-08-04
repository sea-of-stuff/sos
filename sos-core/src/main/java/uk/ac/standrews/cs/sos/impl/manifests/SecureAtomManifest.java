package uk.ac.standrews.cs.sos.impl.manifests;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.io.input.NullInputStream;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.crypto.ProtectionException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.impl.locations.LocationUtility;
import uk.ac.standrews.cs.sos.impl.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.json.SecureAtomManifestDeserializer;
import uk.ac.standrews.cs.sos.json.SecureAtomManifestSerializer;
import uk.ac.standrews.cs.sos.model.Atom;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.model.SecureManifest;
import uk.ac.standrews.cs.sos.utils.IO;
import uk.ac.standrews.cs.utilities.crypto.CryptoException;
import uk.ac.standrews.cs.utilities.crypto.SymmetricEncryption;

import javax.crypto.SecretKey;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@JsonSerialize(using = SecureAtomManifestSerializer.class)
@JsonDeserialize(using = SecureAtomManifestDeserializer.class)
public class SecureAtomManifest extends AtomManifest implements Atom, SecureManifest {

    private InputStream encryptedData;
    private HashMap<IGUID, String> rolesToKeys;

    /**
     * Creates a valid atom manifest given an atom.
     *
     * TODO - is the data already stored at the specified locations?
     * TODO - atom should store only GUID to role?
     *
     * @param guid
     * @param locations
     */
    public SecureAtomManifest(IGUID guid, Set<LocationBundle> locations, Role role) throws ManifestNotMadeException {
        super(guid, locations);
        this.manifestType = ManifestType.ATOM_PROTECTED;

        this.rolesToKeys = new HashMap<>(); // TODO - maybe this already exists, have another constructor?
        try {
            encrypt(role);
        } catch (ProtectionException e) {
            throw new ManifestNotMadeException("Unable to encrypt content");
        }
    }

    private void encrypt(Role role) throws ProtectionException {

        // Encrypt the data from one of the locations
        for (LocationBundle location : getLocations()) {

            InputStream dataStream = LocationUtility.getInputStreamFromLocation(location.getLocation());

            if (!(dataStream instanceof NullInputStream)) {
                // TODO - verify that the data matched the guid?
                encrypt(role, dataStream);
                break;
            }
        }

    }

    // 1. Generate random key K
    // 2. encrypt data with K --> d'
    // 3. encrypt k with pubkey --> (k', pubkey)
    // 4. guid = hash(d')
    // Note that the data is not saved to disk, as it is the Storage service duty to save any data to disk
    private void encrypt(Role role, InputStream originalData) throws ProtectionException {

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            SecretKey key = SymmetricEncryption.generateRandomKey();

            SymmetricEncryption.encrypt(key, originalData, out);
            encryptedData = IO.OutputStreamToInputStream(out);

            String encryptedKey = role.encrypt(key);
            rolesToKeys.put(role.guid(), encryptedKey);

            // The line below will break all existing references, thus we won't use it
            // guid = GUIDFactory.generateGUID(ALGORITHM.SHA256, in);

        } catch (CryptoException | ProtectionException | IOException e) {
            throw new ProtectionException(e);
        }

    }

    /**
     * Return the encrypted data
     */
    @Override
    public InputStream getData() throws IOException {

        // NOTE: get this from location as in the atom manifest?
        encryptedData.reset();
        return IO.CloneInputStream(encryptedData);
    }

    // Returns the unencrypted data
    public InputStream getData(Role role) throws ProtectionException {

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            String encryptedKey = rolesToKeys.get(role.guid());
            SecretKey decryptedKey = role.decrypt(encryptedKey);

            SymmetricEncryption.decrypt(decryptedKey, getData(), out);

            return IO.OutputStreamToInputStream(out);

        } catch (CryptoException | ProtectionException | IOException e) {
            throw new ProtectionException(e);
        }

    }

    @Override
    public HashMap<IGUID, String> keysRoles() {

        return rolesToKeys;
    }

}
