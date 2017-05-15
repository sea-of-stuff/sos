package uk.ac.standrews.cs.sos.utils;

import org.apache.commons.codec.binary.Base64;
import uk.ac.standrews.cs.sos.exceptions.crypto.DecryptionException;
import uk.ac.standrews.cs.sos.exceptions.crypto.EncryptionException;
import uk.ac.standrews.cs.sos.exceptions.crypto.KeyGenerationException;
import uk.ac.standrews.cs.sos.exceptions.crypto.KeyLoadedException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 *
 * From Wikipedia: https://en.wikipedia.org/wiki/Digital_signature
 * A digital signature is a mathematical scheme for demonstrating the authenticity of digital messages or documents.
 * A valid digital signature gives a recipient reason to believe that the message was created by a known sender (authentication),
 * that the sender cannot deny having sent the message (non-repudiation), and that the message was not altered
 * in transit (integrity).
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SignatureCrypto {

    public static final String DSA_ALGORITHM = "DSA"; // Digital Signature Algorithm - https://en.wikipedia.org/wiki/Digital_Signature_Algorithm
    public static final int KEY_SIZE = 512; // in bits - size cannot exceed 2048 according to SUN specs
    public static final String PROVIDER = "SUN";
    public static final String SIGNATURE_ALGORITHM = "SHA1withDSA";

    public static final String SECURE_RANDOM_ALGORITHM = "SHA1PRNG";
    public static final String SECURE_RANDOM_PROVIDER = "SUN";


    private PublicKey publicKey;
    private PrivateKey privateKey;

    public SignatureCrypto() {}
    public SignatureCrypto(PublicKey publicKey) { this.publicKey = publicKey; }

    /**
     * Generate key which contains a pair of private and public key.
     * Store the set of keys in appropriate files based on the specified configuration.
     */
    public void generateKeys() throws KeyGenerationException {
        final KeyPair key = generateKeyPair();

        privateKey = key.getPrivate();
        publicKey = key.getPublic();
    }

    private KeyPair generateKeyPair() throws KeyGenerationException {
        KeyPair pair;
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(DSA_ALGORITHM, PROVIDER);
            SecureRandom random = SecureRandom.getInstance(SECURE_RANDOM_ALGORITHM, SECURE_RANDOM_PROVIDER);

            keyGen.initialize(KEY_SIZE, random);
            pair = keyGen.generateKeyPair();

        } catch (NoSuchAlgorithmException e) {
            throw new KeyGenerationException("Could not generate key pair - algorithm exception", e);
        } catch (NoSuchProviderException e) {
            throw new KeyGenerationException("Could not generate key pair - provided exception", e);
        }

        return pair;
    }

    public void saveToFile(File privateKeyFile, File publicKeyFile) throws IOException {

        if (!privateKeyFile.exists()) {
            FileHelper.MakeFile(privateKeyFile);
        }

        if (!publicKeyFile.exists()) {
            FileHelper.MakeFile(publicKeyFile);
        }

        saveKeyToFile(privateKeyFile, new PKCS8EncodedKeySpec(privateKey.getEncoded()));
        saveKeyToFile(publicKeyFile, new X509EncodedKeySpec(publicKey.getEncoded()));
    }

    private void saveKeyToFile(File file, EncodedKeySpec key) {

        byte[] byteKey = key.getEncoded();
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {

            fileOutputStream.write(byteKey);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadKeys(File privateKeyFile, File publicKeyFile) throws KeyLoadedException {
        loadPrivateKey(privateKeyFile);
        loadPublicKey(publicKeyFile);
    }

    private void loadPrivateKey(File privateKeyFile) throws KeyLoadedException {
        try (FileInputStream keyfis =
                     new FileInputStream(privateKeyFile.getAbsoluteFile())){

            byte[] data = new byte[keyfis.available()];
            keyfis.read(data);

            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(data);
            KeyFactory kf = KeyFactory.getInstance(DSA_ALGORITHM);
            privateKey = kf.generatePrivate(keySpec);

        } catch (IOException e) {
            throw new KeyLoadedException("Private Key - IO Exception", e);
        } catch (NoSuchAlgorithmException e) {
            throw new KeyLoadedException("Private Key - Algorithm Exception", e);
        } catch (InvalidKeySpecException e) {
            throw new KeyLoadedException("Private Key - Key spec Exception", e);
        }
    }

    private void loadPublicKey(File publicKeyFile) throws KeyLoadedException {
        try (FileInputStream keyfis =
                     new FileInputStream(publicKeyFile.getAbsoluteFile())){

            byte[] data = new byte[keyfis.available()];
            keyfis.read(data);

            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(data);
            KeyFactory kf = KeyFactory.getInstance(DSA_ALGORITHM, PROVIDER);
            publicKey = kf.generatePublic(keySpec);

        } catch (IOException e) {
            throw new KeyLoadedException("Public Key - IO Exception", e);
        } catch (NoSuchAlgorithmException e) {
            throw new KeyLoadedException("Public Key - Algorithm Exception", e);
        } catch (InvalidKeySpecException e) {
            throw new KeyLoadedException("Public Key - Key spec Exception", e);
        } catch (NoSuchProviderException e) {
            throw new KeyLoadedException("Public Key - No such provider Exception", e);
        }
    }

    /**
     * Encrypt a given text using the keys for this identity
     *
     * @param text in plain to be encrypted.
     * @return the encrypted text.
     */
    public byte[] sign(String text) throws EncryptionException {
        byte[] retval;
        try {
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM, PROVIDER);

            signature.initSign(privateKey);
            signature.update(text.getBytes());
            retval = signature.sign();
        } catch (NoSuchAlgorithmException |
                NoSuchProviderException |
                SignatureException |
                InvalidKeyException e) {
            throw new EncryptionException(e);
        }
        return retval;
    }

    public String sign64(String text) throws EncryptionException {
        byte[] signatureBytes = sign(text);
        byte[] encodedBytes = Base64.encodeBase64(signatureBytes);
        return new String(encodedBytes);
    }

    /**
     * Decrypt the given encrypted text using the private key of this identity.
     * @param text to verifySignature.
     * @return the plain text. Null if the input could not be decrypted.
     */
    public boolean verify(String text, byte[] signatureToVerify) throws DecryptionException {
        boolean isValid;
        try {
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM, PROVIDER);
            signature.initVerify(publicKey);
            signature.update(text.getBytes());
            isValid = signature.verify(signatureToVerify);
        } catch (NoSuchAlgorithmException |
                NoSuchProviderException |
                SignatureException |
                InvalidKeyException e) {
            throw new DecryptionException(e);
        }

        return isValid;
    }

    public boolean verify64(String text, String signature) throws DecryptionException {
        byte[] decodedBytes = Base64.decodeBase64(signature);
        return verify(text, decodedBytes);
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }
}