package uk.ac.standrews.cs.sos.model.identity;


import uk.ac.standrews.cs.sos.exceptions.identity.DecryptionException;
import uk.ac.standrews.cs.sos.exceptions.identity.EncryptionException;
import uk.ac.standrews.cs.sos.exceptions.identity.KeyGenerationException;
import uk.ac.standrews.cs.sos.exceptions.identity.KeyLoadedException;
import uk.ac.standrews.cs.sos.interfaces.identity.Identity;
import uk.ac.standrews.cs.sos.utils.crypto.SignatureCrypto;

import java.io.File;
import java.io.IOException;
import java.security.Key;

/**
 * https://docs.oracle.com/javase/tutorial/security/apisign/
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class IdentityImpl implements Identity {

    public static final String PRIVATE_KEY_FILE = "private.pem";
    public static final String PUBLIC_KEY_FILE = "public.pem";

    private SignatureCrypto signature;

    private File privateKeyFile;
    private File publicKeyFile;

    /**
     *
     * @param keysFolderName this is the path to the folder containing the keys
     * @throws KeyGenerationException
     * @throws KeyLoadedException
     */
    public IdentityImpl(String keysFolderName) throws KeyGenerationException, KeyLoadedException {

        signature = new SignatureCrypto();

        privateKeyFile = new File(keysFolderName + "/" + PRIVATE_KEY_FILE);
        publicKeyFile = new File(keysFolderName + "/" + PUBLIC_KEY_FILE);
        if (!privateKeyFile.exists() || !publicKeyFile.exists()) {
            generateKeys();
        } else {
            loadKeys();
        }

    }

    /**
     * Construct the Identity keys in the directory ~/sos/keys/
     * @throws KeyGenerationException
     * @throws KeyLoadedException
     */
    public IdentityImpl() throws KeyGenerationException, KeyLoadedException {
        this(System.getProperty("user.home") + "/sos/keys");
    }

    @Override
    public Key getPublicKey() {
        return this.signature.getPublicKey();
    }

    /**
     * Encrypt a given text using the keys for this identity
     *
     * @param text in plain to be encrypted.
     * @return the encrypted text.
     */
    public String sign(String text) throws EncryptionException {
        return signature.sign64(text);
    }

    /**
     * Decrypt the given encrypted text using the private key of this identity.
     * @param text to verifySignature.
     * @return the plain text. Null if the input could not be decrypted.
     */
    public boolean verify(String text, String signatureToVerify) throws DecryptionException {
        return signature.verify64(text, signatureToVerify);
    }

    /**
     * Generate key which contains a pair of private and public key.
     * Store the set of keys in appropriate files based on the specified configuration.
     */
    private void generateKeys() throws KeyGenerationException {
        signature.generateKeys();

        try {
            signature.saveToFile(privateKeyFile, publicKeyFile);
        } catch (IOException e) {
            throw new KeyGenerationException("Unable to save keys");
        }
    }

    private void loadKeys() throws KeyLoadedException {
        signature.loadKeys(privateKeyFile, publicKeyFile);
    }

    @Override
    public String toString() {
        return "Public key: " + signature.getPublicKey().toString();
    }
}
