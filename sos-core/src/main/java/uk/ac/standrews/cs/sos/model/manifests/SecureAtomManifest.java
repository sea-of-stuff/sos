package uk.ac.standrews.cs.sos.model.manifests;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.identity.EncryptionException;
import uk.ac.standrews.cs.sos.exceptions.identity.KeyGenerationException;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.utils.crypto.AESCrypto;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SecureAtomManifest extends AtomManifest {

    /**
     * Creates a valid atom manifest given an atom.
     *
     * @param guid
     * @param locations
     */
    public SecureAtomManifest(IGUID guid, Set<LocationBundle> locations) {
        super(guid, locations);


    }

    // TODO - use file and inputstream for data
    // Generate random key K
    // encrypt data with K --> d'
    // encrypt k with pubkey --> (k', pubkey)
    // guid = hash(d')
    private void encrypt(String data) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException {

        try {
            AESCrypto aes = new AESCrypto();
            aes.generateKeys();
            String encryptedData = aes.encrypt64(data);

            // Encrypt key


            IGUID guid = GUIDFactory.generateGUID(encryptedData);

        } catch (KeyGenerationException e) {
            e.printStackTrace();
        } catch (EncryptionException e) {
            e.printStackTrace();
        } catch (GUIDGenerationException e) {
            e.printStackTrace();
        }

    }
}
