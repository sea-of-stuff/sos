package uk.ac.standrews.cs.sos.model.manifests;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.identity.KeyGenerationException;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.utils.CRYPTOSignature;

import javax.crypto.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SecureAtomManifest extends AtomManifest {

    private CRYPTOSignature pubKey;

    /**
     * Creates a valid atom manifest given an atom.
     *
     * @param guid
     * @param locations
     */
    public SecureAtomManifest(IGUID guid, Set<LocationBundle> locations) {
        super(guid, locations);

        // SEE IdentityImpl
        // Generate key pair (private + public)
        pubKey = new CRYPTOSignature();
        try {
            pubKey.generateKeys();
        } catch (KeyGenerationException e) {
            e.printStackTrace();
        }
    }

    private void encrypt() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException {

        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256); // for example
        SecretKey K = keyGen.generateKey();

        Cipher cipher = Cipher.getInstance(keyGen.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, K);
        byte[] input = "input".getBytes();
        byte[] encrypted = cipher.doFinal(input);


        // Generate random key K
        // encrypt data with K --> d'
        // encrypt k with pubkey --> (k', pubkey)
        // guid = hash(d')
    }
}
