package model.implementations.identity;

import configurations.identity.IdentityConfiguration;
import model.interfaces.components.identity.Identity;

import javax.crypto.Cipher;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.*;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class IdentityImplementation implements Identity {

    private PublicKey publicKey;
    private PrivateKey privateKey;

    /**
     * Generate key which contains a pair of private and public key.
     * Store the set of keys in appropriate files.
     *
     */
    public void generateKeys() {
            final KeyPair key = generateKeyPair();

            File privateKeyFile = createKeyFile(IdentityConfiguration.PRIVATE_KEY_FILE);
            File publicKeyFile = createKeyFile(IdentityConfiguration.PUBLIC_KEY_FILE);

            // Saving the keys
            saveKeyToFile(publicKeyFile, key.getPublic());
            saveKeyToFile(privateKeyFile, key.getPrivate());
    }

    public byte[] encrypt(String text) {
        byte[] cipherText = null;
        try {
            // get an RSA cipher object and print the provider
            final Cipher cipher = Cipher.getInstance(IdentityConfiguration.ALGORITHM);
            // encrypt the plain text using the public key
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            cipherText = cipher.doFinal(text.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cipherText;
    }

    public String decrypt(byte[] text) {
        byte[] dectyptedText = null;
        try {
            // get an RSA cipher object and print the provider
            final Cipher cipher = Cipher.getInstance(IdentityConfiguration.ALGORITHM);

            // decrypt the text using the private key
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            dectyptedText = cipher.doFinal(text);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return new String(dectyptedText);
    }

    private KeyPair generateKeyPair() {
        KeyPair key = null;
        try {
            final KeyPairGenerator keyGen = KeyPairGenerator.getInstance(IdentityConfiguration.ALGORITHM);
            keyGen.initialize(IdentityConfiguration.KEY_SIZE);
            key = keyGen.generateKeyPair();

            publicKey = key.getPublic();
            privateKey = key.getPrivate();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return key;

    }

    private File createKeyFile(String path) {
        File file = new File(path);

        // Create files to store public and private key
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }

    private static void saveKeyToFile(File file, Key key) {
        try (ObjectOutputStream publicKeyOS =
                     new ObjectOutputStream(new FileOutputStream(file))) {
            publicKeyOS.writeObject(key);
            publicKeyOS.close();
        } catch (IOException e) {

        }
    }

    @Override
    public Key getPublicKey() {
        return this.publicKey;
    }
}
