package uk.ac.standrews.cs.sos.utils.crypto;

import sun.misc.BASE64Decoder;
import uk.ac.standrews.cs.sos.exceptions.identity.EncryptionException;
import uk.ac.standrews.cs.sos.exceptions.identity.KeyGenerationException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AESCrypto {

    private Key key;

    public void generateKey() throws KeyGenerationException {
        try {
            String session = random128BitString64();
            key = generateKeyFromString(session);
        } catch (IOException e) {
            throw new KeyGenerationException("Could not generate key - IO Exception ", e);
        }
    }

    public byte[] encrypt(String text) {
        byte[] cipherText = null;
        try {
            final Cipher cipher = Cipher.getInstance(CRYPTOConstants.AES_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            cipherText = cipher.doFinal(text.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cipherText;
    }

    public String encrypt64(String text) throws EncryptionException {
        byte[] signatureBytes = encrypt(text);
        byte[] encodedBytes = Base64.getEncoder().encode(signatureBytes);
        return new String(encodedBytes);
    }

    private byte[] decrypt(String text) {
        byte[] cipherText = null;
        try {
            final Cipher cipher = Cipher.getInstance(CRYPTOConstants.AES_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);

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

    // http://www.javamex.com/tutorials/random_numbers/securerandom.shtml
    public String random128BitString64() {
        Random ranGen = new SecureRandom();
        byte[] aesKey = new byte[16]; // 16 bytes = 128 bits
        ranGen.nextBytes(aesKey);
        return new String(Base64.getEncoder().encode(aesKey));
    }

    private Key generateKeyFromString(final String secKey) throws IOException {

        byte[] keyVal = Base64.getDecoder().decode(secKey);
        final Key key = new SecretKeySpec(keyVal, CRYPTOConstants.AES_ALGORITHM);
        return key;
    }

    public String getKey() {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }
}
