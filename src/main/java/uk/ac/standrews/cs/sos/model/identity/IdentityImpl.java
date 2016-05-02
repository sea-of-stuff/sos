package uk.ac.standrews.cs.sos.model.identity;

import uk.ac.standrews.cs.sos.exceptions.identity.DecryptionException;
import uk.ac.standrews.cs.sos.exceptions.identity.EncryptionException;
import uk.ac.standrews.cs.sos.exceptions.identity.KeyGenerationException;
import uk.ac.standrews.cs.sos.exceptions.identity.KeyLoadedException;
import uk.ac.standrews.cs.sos.interfaces.identity.Identity;
import uk.ac.standrews.cs.sos.interfaces.storage.SOSFile;
import uk.ac.standrews.cs.sos.model.SeaConfiguration;

import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * https://docs.oracle.com/javase/tutorial/security/apisign/
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class IdentityImpl implements Identity {

    private PublicKey publicKey;
    private PrivateKey privateKey;

    private SOSFile[] pathsToKeys;

    public IdentityImpl(SeaConfiguration configuration) throws KeyGenerationException, KeyLoadedException {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        this.pathsToKeys = configuration.getIdentityPaths();

        File privateKeyFile = new File(pathsToKeys[0].getPathname());
        File publicKeyFile = new File(pathsToKeys[1].getPathname());
        if (!privateKeyFile.exists() || !publicKeyFile.exists()) {
            generateKeys();
        } else {
            loadKeys();
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
    public byte[] sign(String text) throws EncryptionException {
        byte[] retval;
        try {
            Signature signature = Signature.getInstance(IdentityConfiguration.SIGNATURE_ALGORITHM, IdentityConfiguration.PROVIDER);
            signature.initSign(privateKey, new SecureRandom());
            signature.update(text.getBytes());
            retval = signature.sign();
        } catch (NoSuchAlgorithmException |
                NoSuchProviderException |
                SignatureException |
                InvalidKeyException e) {
            throw new EncryptionException();
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
            Signature signature = Signature.getInstance(IdentityConfiguration.SIGNATURE_ALGORITHM, IdentityConfiguration.PROVIDER);
            signature.initVerify(publicKey);
            signature.update(text.getBytes());
            isValid = signature.verify(signatureToVerify);
        } catch (NoSuchAlgorithmException |
                NoSuchProviderException |
                SignatureException |
                InvalidKeyException e) {
            throw new DecryptionException();
        }

        return isValid;
    }

    private void loadKeys() throws KeyLoadedException {
        loadPrivateKey();
        loadPublicKey();
    }

    private void loadPrivateKey() throws KeyLoadedException {
        try {
            DataInputStream in=new DataInputStream(new FileInputStream(pathsToKeys[0].getPathname()));
            byte[] data = new byte[in.available()];
            in.readFully(data);

            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(data);
            KeyFactory kf = KeyFactory.getInstance(IdentityConfiguration.KEYS_ALGORITHM);
            privateKey = kf.generatePrivate(keySpec);
        } catch (IOException e) {
            throw new KeyLoadedException("Private Key - IO Exception");
        } catch (NoSuchAlgorithmException e) {
            throw new KeyLoadedException("Private Key - Algorithm Exception");
        } catch (InvalidKeySpecException e) {
            throw new KeyLoadedException("Private Key - Key spec Exception");
        }
    }

    private void loadPublicKey() throws KeyLoadedException {
        try {
            DataInputStream in=new DataInputStream(new FileInputStream(pathsToKeys[1].getPathname()));
            byte[] data = new byte[in.available()];
            in.readFully(data);

            // http://stackoverflow.com/questions/19640735/load-public-key-data-from-file
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(data);
            KeyFactory kf = KeyFactory.getInstance(IdentityConfiguration.KEYS_ALGORITHM);
            publicKey = kf.generatePublic(keySpec);
        } catch (IOException e) {
            throw new KeyLoadedException("Private Key - IO Exception");
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
    private void generateKeys() throws KeyGenerationException {
        final KeyPair key = generateKeyPair();

        File privateKeyFile = createKeyFile(pathsToKeys[0].getPathname());
        File publicKeyFile = createKeyFile(pathsToKeys[1].getPathname());

        // Saving the keys
        saveKeyToFile(publicKeyFile, key.getPublic());
        saveKeyToFile(privateKeyFile, key.getPrivate());
    }

    private KeyPair generateKeyPair() throws KeyGenerationException {
        KeyPair pair;
        try {
            final KeyPairGenerator keyGen = KeyPairGenerator.getInstance(IdentityConfiguration.KEYS_ALGORITHM, IdentityConfiguration.PROVIDER);
            keyGen.initialize(IdentityConfiguration.KEY_SIZE, new SecureRandom());
            pair = keyGen.generateKeyPair();

            publicKey = pair.getPublic();
            privateKey = pair.getPrivate();
        } catch (NoSuchAlgorithmException e) {
            throw new KeyGenerationException("Could not generate key pair - algorithm exception");
        } catch (NoSuchProviderException e) {
            throw new KeyGenerationException("Could not generate key pair - provided exception");
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
        } catch (IOException e) {
            System.err.print(e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "Public key: " + publicKey.toString();
    }
}
