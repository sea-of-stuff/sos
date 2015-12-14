package sos.model.implementations.identity;

import configurations.identity.IdentityConfiguration;
import sos.exceptions.DecryptionException;
import sos.exceptions.EncryptionException;
import sos.exceptions.KeyGenerationException;
import sos.model.interfaces.identity.Identity;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.crypto.Cipher;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.security.*;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class IdentityImpl implements Identity {

    private String name;
    private String description;

    // keys of this identity
    private PublicKey publicKey;
    private PrivateKey privateKey;

    public IdentityImpl() throws KeyGenerationException {
        generateKeys();
    }

    /**
     * Generate key which contains a pair of private and public key.
     * Store the set of keys in appropriate files.
     */
    public void generateKeys() throws KeyGenerationException {
        final KeyPair key = generateKeyPair();

        File privateKeyFile = createKeyFile(IdentityConfiguration.PRIVATE_KEY_FILE);
        File publicKeyFile = createKeyFile(IdentityConfiguration.PUBLIC_KEY_FILE);

        // Saving the keys
        saveKeyToFile(publicKeyFile, key.getPublic());
        saveKeyToFile(privateKeyFile, key.getPrivate());
    }

    /**
     * Encrypt a given text using the keys for this identity
     *
     * @param text in plain to be encrypted.
     * @return the encrypted text.
     */
    public byte[] encrypt(String text) throws EncryptionException {
        byte[] cipherText = null;
        try {
            final Cipher cipher = Cipher.getInstance(IdentityConfiguration.ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            cipherText = cipher.doFinal(text.getBytes());
        } catch (Exception e) {
            throw new EncryptionException();
        }
        return cipherText;
    }

    /**
     * Decrypt the given encrypted text using the private key of this identity.
     * @param text to decrypt.
     * @return the plain text. Null if the input could not be decrypted.
     */
    public String decrypt(byte[] text) throws DecryptionException {
        byte[] dectyptedText = null;
        try {
            final Cipher cipher = Cipher.getInstance(IdentityConfiguration.ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            dectyptedText = cipher.doFinal(text);
        } catch (Exception ex) {
            throw new DecryptionException();
        }

        if (dectyptedText == null)
            return null;

        return new String(dectyptedText);
    }

    private KeyPair generateKeyPair() throws KeyGenerationException {
        KeyPair key = null;
        try {
            final KeyPairGenerator keyGen = KeyPairGenerator.getInstance(IdentityConfiguration.ALGORITHM);
            keyGen.initialize(IdentityConfiguration.KEY_SIZE);
            key = keyGen.generateKeyPair();

            publicKey = key.getPublic();
            privateKey = key.getPrivate();
        } catch (NoSuchAlgorithmException e) {
            throw new KeyGenerationException("Could not generate key pair");
        }

        return key;

    }

    // Create files to store public and private key
    private File createKeyFile(String path) throws KeyGenerationException {
        File file = new File(path);

        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new KeyGenerationException("Could not save key to file");
        }

        return file;
    }

    private static void saveKeyToFile(File file, Key key) {
        try (ObjectOutputStream publicKeyOS =
                     new ObjectOutputStream(new FileOutputStream(file))) {
            publicKeyOS.writeObject(key);
            publicKeyOS.close();
        } catch (IOException e) {
            System.err.print(e.getMessage());
        }
    }

    @Override
    public void loadIdentity(Path path) {
        throw new NotImplementedException();
    }

    @Override
    public Key getPublicKey() {
        return this.publicKey;
    }
}
