package uk.ac.standrews.cs.sos.utils;

import org.apache.commons.codec.binary.Base64;
import uk.ac.standrews.cs.sos.exceptions.identity.EncryptionException;
import uk.ac.standrews.cs.sos.exceptions.identity.KeyGenerationException;

import javax.crypto.Cipher;
import java.security.*;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class PubKey {

    private PublicKey publicKey;
    private PrivateKey privateKey;

    public void generateKeys() throws KeyGenerationException {
        KeyPair pair = generateKeyPair();
        publicKey = pair.getPublic();
        privateKey = pair.getPrivate();
    }

    private KeyPair generateKeyPair() throws KeyGenerationException {
        KeyPair pair;
        try {
            KeyPairGenerator keyGen = KeyPairGenerator
                    .getInstance(CRYPTOConstants.RSA_ALGORITHM);

            keyGen.initialize(CRYPTOConstants.KEY_SIZE);
            pair = keyGen.generateKeyPair();

        } catch (NoSuchAlgorithmException e) {
            throw new KeyGenerationException("Could not generate key pair - algorithm exception", e);
        }

        return pair;
    }

    public byte[] encrypt(String text) {
        byte[] cipherText = null;
        try {
            // get an RSA cipher object and print the provider
            final Cipher cipher = Cipher.getInstance(publicKey.getAlgorithm());
            // encrypt the plain text using the public key
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            cipherText = cipher.doFinal(text.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cipherText;
    }

    public String encrypt64(String text) throws EncryptionException {
        byte[] signatureBytes = encrypt(text);
        byte[] encodedBytes = Base64.encodeBase64(signatureBytes);
        return new String(encodedBytes);
    }

    private void decrypt(String text) {

    }
}
