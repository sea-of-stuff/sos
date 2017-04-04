package uk.ac.standrews.cs.sos.model.manifests;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.crypto.EncryptionException;
import uk.ac.standrews.cs.sos.exceptions.crypto.KeyGenerationException;
import uk.ac.standrews.cs.sos.json.SecureAtomManifestDeserializer;
import uk.ac.standrews.cs.sos.json.SecureAtomManifestSerializer;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.utils.crypto.AESCrypto;
import uk.ac.standrews.cs.sos.utils.crypto.RSACrypto;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@JsonSerialize(using = SecureAtomManifestSerializer.class)
@JsonDeserialize(using = SecureAtomManifestDeserializer.class)
public class SecureAtomManifest extends AtomManifest {

    private RSACrypto rsa; // TODO - generate them only once or once per ROLE?

    /**
     * Creates a valid atom manifest given an atom.
     *
     * @param guid
     * @param locations
     */
    public SecureAtomManifest(IGUID guid, Set<LocationBundle> locations) {
        super(guid, locations);

        rsa = new RSACrypto();
        try {
            rsa.generateKeys();
        } catch (KeyGenerationException e) {
            e.printStackTrace();
        }

    }

    // TODO - use file and inputstream for data
    // Generate random key K
    // encrypt data with K --> d'
    // encrypt k with pubkey --> (k', pubkey)
    // guid = hash(d')
    private void encrypt(String data) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException {

        try {
            AESCrypto aes = new AESCrypto();
            aes.generateKey();
            String encryptedData = aes.encrypt64(data);

            // Encrypt key
            String encryptedKey = rsa.encrypt64(aes.getKey());

            IGUID encryptedDataGUID = GUIDFactory.generateGUID(encryptedData);

        } catch (KeyGenerationException e) {
            e.printStackTrace();
        } catch (EncryptionException e) {
            e.printStackTrace();
        } catch (GUIDGenerationException e) {
            e.printStackTrace();
        }

    }


}
