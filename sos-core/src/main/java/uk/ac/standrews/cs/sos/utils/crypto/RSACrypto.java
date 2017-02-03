package uk.ac.standrews.cs.sos.utils.crypto;

import org.apache.commons.codec.binary.Base64;
import sun.misc.BASE64Decoder;
import uk.ac.standrews.cs.sos.exceptions.identity.EncryptionException;
import uk.ac.standrews.cs.sos.exceptions.identity.KeyGenerationException;

import javax.crypto.Cipher;
import java.security.*;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class RSACrypto {

    private PublicKey publicKey;
    private PrivateKey privateKey;

    public void generateKeys() throws KeyGenerationException {
        final KeyPair key = generateKeyPair();

        privateKey = key.getPrivate();
        publicKey = key.getPublic();
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
            final Cipher cipher = Cipher.getInstance(CRYPTOConstants.RSA_ALGORITHM);
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

    private byte[] decrypt(String text) {
        byte[] cipherText = null;
        try {
            final Cipher cipher = Cipher.getInstance(CRYPTOConstants.RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            final byte[] decorVal = new BASE64Decoder().decodeBuffer(text);

            cipherText = cipher.doFinal(decorVal);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cipherText;
    }

    public String decryptToString(String text) {
        return new String(decrypt(text));
    }

}
