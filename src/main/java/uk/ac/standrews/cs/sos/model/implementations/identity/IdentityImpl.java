package uk.ac.standrews.cs.sos.model.implementations.identity;

import uk.ac.standrews.cs.configurations.identity.IdentityConfiguration;
import uk.ac.standrews.cs.sos.configurations.SeaConfiguration;
import uk.ac.standrews.cs.sos.exceptions.identity.DecryptionException;
import uk.ac.standrews.cs.sos.exceptions.identity.EncryptionException;
import uk.ac.standrews.cs.sos.exceptions.identity.KeyGenerationException;
import uk.ac.standrews.cs.sos.exceptions.identity.KeyLoadedException;
import uk.ac.standrews.cs.sos.model.interfaces.identity.Identity;

import javax.crypto.Cipher;
import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class IdentityImpl implements Identity {

    private PublicKey publicKey;
    private PrivateKey privateKey;

    public IdentityImpl(SeaConfiguration configuration) throws KeyGenerationException, KeyLoadedException {
        String[] pathsToKeys = configuration.getIdentityPaths();

        File privateKeyFile = new File(pathsToKeys[0]);
        File publicKeyFile = new File(pathsToKeys[1]);
        if (!privateKeyFile.exists() || !publicKeyFile.exists()) {
            generateKeys(configuration);
        } else {
            loadKeys(pathsToKeys);
        }
    }


    @Override
    public Key getPublicKey() {
        return this.publicKey;
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

    private void loadKeys(String[] pathsToKeys) throws KeyLoadedException {
        loadPrivateKey(pathsToKeys[0]);
        loadPublicKey(pathsToKeys[1]);
    }

    private void loadPrivateKey(String privateKeyPath) throws KeyLoadedException {
        try {
            DataInputStream in=new DataInputStream(new FileInputStream(privateKeyPath));
            byte[] data = new byte[in.available()];
            in.readFully(data);

            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(data);
            KeyFactory kf = KeyFactory.getInstance(IdentityConfiguration.ALGORITHM);
            privateKey = kf.generatePrivate(keySpec);
        } catch (IOException e) {
            throw new KeyLoadedException("Private Key - uk.ac.standrews.cs.IO Exception");
        } catch (NoSuchAlgorithmException e) {
            throw new KeyLoadedException("Private Key - Algorithm Exception");
        } catch (InvalidKeySpecException e) {
            throw new KeyLoadedException("Private Key - Key spec Exception");
        }
    }

    private void loadPublicKey(String privateKeyPath) throws KeyLoadedException {
        try {
            DataInputStream in=new DataInputStream(new FileInputStream(privateKeyPath));
            byte[] data = new byte[in.available()];
            in.readFully(data);

            // http://stackoverflow.com/questions/19640735/load-public-key-data-from-file
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(data);
            KeyFactory kf = KeyFactory.getInstance(IdentityConfiguration.ALGORITHM);
            publicKey = kf.generatePublic(keySpec);
        } catch (IOException e) {
            throw new KeyLoadedException("Private Key - uk.ac.standrews.cs.IO Exception");
        } catch (NoSuchAlgorithmException e) {
            throw new KeyLoadedException("Private Key - Algorithm Exception");
        } catch (InvalidKeySpecException e) {
            throw new KeyLoadedException("Private Key - Key spec Exception");
        }
    }

    /**
     * Generate key which contains a pair of private and public key.
     * Store the set of keys in appropriate files based on the specified configuration.
     */
    private void generateKeys(SeaConfiguration configuration) throws KeyGenerationException {
        final KeyPair key = generateKeyPair();

        File privateKeyFile = createKeyFile(configuration.getIdentityPaths()[0]);
        File publicKeyFile = createKeyFile(configuration.getIdentityPaths()[1]);

        // Saving the keys
        saveKeyToFile(publicKeyFile, key.getPublic());
        saveKeyToFile(privateKeyFile, key.getPrivate());
    }

    private KeyPair generateKeyPair() throws KeyGenerationException {
        KeyPair pair = null;
        try {
            final KeyPairGenerator keyGen = KeyPairGenerator.getInstance(IdentityConfiguration.ALGORITHM);
            keyGen.initialize(IdentityConfiguration.KEY_SIZE);
            pair = keyGen.generateKeyPair();

            publicKey = pair.getPublic();
            privateKey = pair.getPrivate();
        } catch (NoSuchAlgorithmException e) {
            throw new KeyGenerationException("Could not generate key pair");
        }

        return pair;

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
        try (DataOutputStream publicKeyOS =
                     new DataOutputStream(new FileOutputStream(file))) {
            publicKeyOS.write(key.getEncoded());
            publicKeyOS.close();
        } catch (IOException e) {
            System.err.print(e.getMessage());
        }
    }
}
