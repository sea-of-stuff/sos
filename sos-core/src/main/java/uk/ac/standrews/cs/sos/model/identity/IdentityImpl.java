package uk.ac.standrews.cs.sos.model.identity;


import uk.ac.standrews.cs.sos.exceptions.identity.DecryptionException;
import uk.ac.standrews.cs.sos.exceptions.identity.EncryptionException;
import uk.ac.standrews.cs.sos.exceptions.identity.KeyGenerationException;
import uk.ac.standrews.cs.sos.exceptions.identity.KeyLoadedException;
import uk.ac.standrews.cs.sos.interfaces.identity.Identity;

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
 * https://docs.oracle.com/javase/tutorial/security/apisign/
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class IdentityImpl implements Identity {

    public static final String PRIVATE_KEY_FILE = "private.pem";
    public static final String PUBLIC_KEY_FILE = "public.pem";

    private PublicKey publicKey;
    private PrivateKey privateKey;

    private File privateKeyFile;
    private File publicKeyFile;

    /**
     *
     * @param keysFolderName this is the path to the folder containing the keys
     * @throws KeyGenerationException
     * @throws KeyLoadedException
     */
    public IdentityImpl(String keysFolderName) throws KeyGenerationException, KeyLoadedException {

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
        return this.publicKey;
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
            Signature signature = Signature
                    .getInstance(CryptConstants.SIGNATURE_ALGORITHM,
                            CryptConstants.PROVIDER);

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

    /**
     * Decrypt the given encrypted text using the private key of this identity.
     * @param text to verify.
     * @return the plain text. Null if the input could not be decrypted.
     */
    public boolean verify(String text, byte[] signatureToVerify) throws DecryptionException {
        boolean isValid;
        try {
            Signature signature = Signature
                    .getInstance(CryptConstants.SIGNATURE_ALGORITHM,
                            CryptConstants.PROVIDER);
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

    /**
     * Generate key which contains a pair of private and public key.
     * Store the set of keys in appropriate files based on the specified configuration.
     */
    private void generateKeys() throws KeyGenerationException {
        final KeyPair key = generateKeyPair();

        File privateKFile = createKeyFile(privateKeyFile);
        File publicKFile = createKeyFile(publicKeyFile);

        saveKeyToFile(privateKFile,
                new PKCS8EncodedKeySpec(
                key.getPrivate().getEncoded()));

        saveKeyToFile(publicKFile,
                new X509EncodedKeySpec(
                key.getPublic().getEncoded()));
    }

    private KeyPair generateKeyPair() throws KeyGenerationException {
        KeyPair pair;
        try {
            KeyPairGenerator keyGen = KeyPairGenerator
                    .getInstance(CryptConstants.KEYS_ALGORITHM,
                            CryptConstants.PROVIDER);
            SecureRandom random = SecureRandom
                    .getInstance(CryptConstants.SECURE_RANDOM_ALGORITHM,
                            CryptConstants.SECURE_RANDOM_PROVIDER);

            keyGen.initialize(CryptConstants.KEY_SIZE, random);
            pair = keyGen.generateKeyPair();

            privateKey = pair.getPrivate();
            publicKey = pair.getPublic();
        } catch (NoSuchAlgorithmException e) {
            throw new KeyGenerationException("Could not generate key pair - algorithm exception", e);
        } catch (NoSuchProviderException e) {
            throw new KeyGenerationException("Could not generate key pair - provided exception", e);
        }

        return pair;
    }

    private void loadKeys() throws KeyLoadedException {
        loadPrivateKey();
        loadPublicKey();
    }

    private void loadPrivateKey() throws KeyLoadedException {
        try (FileInputStream keyfis =
                     new FileInputStream(privateKeyFile.getAbsoluteFile())){

            byte[] data = new byte[keyfis.available()];
            keyfis.read(data);

            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(data);
            KeyFactory kf = KeyFactory.getInstance(CryptConstants.KEYS_ALGORITHM);
            privateKey = kf.generatePrivate(keySpec);

        } catch (IOException e) {
            throw new KeyLoadedException("Private Key - IO Exception", e);
        } catch (NoSuchAlgorithmException e) {
            throw new KeyLoadedException("Private Key - Algorithm Exception", e);
        } catch (InvalidKeySpecException e) {
            throw new KeyLoadedException("Private Key - Key spec Exception", e);
        }
    }

    private void loadPublicKey() throws KeyLoadedException {
        try (FileInputStream keyfis =
                     new FileInputStream(publicKeyFile.getAbsoluteFile())){

            byte[] data = new byte[keyfis.available()];
            keyfis.read(data);

            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(data);
            KeyFactory kf = KeyFactory
                    .getInstance(CryptConstants.KEYS_ALGORITHM,
                            CryptConstants.PROVIDER);
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

    // Create files to storeLocation public and private key
    private File createKeyFile(File file) throws KeyGenerationException {

        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new KeyGenerationException("Could not save key to file", e);
        }

        return file;
    }

    private static void saveKeyToFile(File file, EncodedKeySpec key) {

        byte[] byteKey = key.getEncoded();

        try (FileOutputStream fileOutputStream =
                     new FileOutputStream(file)) {

            fileOutputStream.write(byteKey);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "Public key: " + publicKey.toString();
    }
}
