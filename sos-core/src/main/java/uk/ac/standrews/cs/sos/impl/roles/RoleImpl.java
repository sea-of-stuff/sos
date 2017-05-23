package uk.ac.standrews.cs.sos.impl.roles;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.crypto.DecryptionException;
import uk.ac.standrews.cs.sos.exceptions.crypto.EncryptionException;
import uk.ac.standrews.cs.sos.exceptions.crypto.KeyGenerationException;
import uk.ac.standrews.cs.sos.exceptions.crypto.KeyLoadedException;
import uk.ac.standrews.cs.sos.json.RoleDeserializer;
import uk.ac.standrews.cs.sos.json.RoleSerializer;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.model.User;
import uk.ac.standrews.cs.utilities.crypto.AsymmetricEncryption;
import uk.ac.standrews.cs.utilities.crypto.CryptoException;
import uk.ac.standrews.cs.utilities.crypto.DigitalSignature;

import javax.crypto.SecretKey;
import java.io.File;
import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@JsonSerialize(using = RoleSerializer.class)
@JsonDeserialize(using = RoleDeserializer.class)
public class RoleImpl implements Role {

    private IGUID userGUID;
    private IGUID roleGUID;
    private String name;

    private final static String KEYS_FOLDER = System.getProperty("user.home") + "/sos/keys/";

    private PrivateKey signaturePrivateKey;
    private PublicKey signatureCertificate;

    private KeyPair asymmetricKeys;

    private String signature;

    /**
     *
     * keys are either created and persisted, or loaded
     *
     * @param guid
     * @param name
     * @throws KeyGenerationException
     * @throws KeyLoadedException
     */
    public RoleImpl(User user, IGUID guid, String name) throws KeyGenerationException, KeyLoadedException, CryptoException, EncryptionException {
        this.userGUID = user.guid();
        this.roleGUID = guid;
        this.name = name;

        manageSignatureKeys();

        // Keys to encrypt AES keys
        asymmetricKeys = AsymmetricEncryption.generateKeys();

        // TODO - load/save keys

        // TODO - generate SIGNATURE for this role using the user

        // signature = new SignatureCrypto(user.getSignatureCertificate()).sign64("DUMMY_SIGNATURE");
    }

    // FIXME - better exceptions (e.g. RoleException)
    public RoleImpl(User user, String name) throws KeyGenerationException, KeyLoadedException, CryptoException, EncryptionException {
        this(user, GUIDFactory.generateRandomGUID(), name);
    }

    @Override
    public IGUID guid() {
        return roleGUID;
    }

    @Override
    public IGUID getUser() {
        return userGUID;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public PublicKey getSignatureCertificate() {
        return signatureCertificate;
    }

    @Override
    public PublicKey getPubKey() {
        return asymmetricKeys.getPublic();
    }

    @Override
    public String getSignature() {
        return signature;
    }

    @Override
    public String sign(String text) throws EncryptionException {
        try {
            return DigitalSignature.sign64(signaturePrivateKey, text);
        } catch (CryptoException e) {
            throw new EncryptionException(e);
        }
    }

    @Override
    public boolean verify(String text, String signatureToVerify) throws DecryptionException {

        try {
            return DigitalSignature.verify64(signatureCertificate, text, signatureToVerify);
        } catch (CryptoException e) {
            throw new DecryptionException(e);
        }
    }

    @Override
    public String encrypt(SecretKey key) {
        try {
            // TODO - extend the utilities project
            AsymmetricEncryption.encryptAESKey(key, null, null);
        } catch (IOException | CryptoException e) {
            e.printStackTrace();
        }

        return "TODO";
    }

    @Override
    public SecretKey decrypt(String encryptedKey) {
        return null;
    }


    private void manageSignatureKeys() throws KeyGenerationException, KeyLoadedException, CryptoException {

        // TODO - load private and certificate independently from each other

        // Private key is saved to disk and keps into memory, but it is not exposed to other objects or other nodes
        // for obvious security issues
        File privateKeyFile = new File(KEYS_FOLDER + roleGUID + ".pem");
        File publicKeyFile = new File(KEYS_FOLDER + roleGUID + "-pub.pem");
        if (!privateKeyFile.exists() || !publicKeyFile.exists()) {

            KeyPair keys = DigitalSignature.generateKeys();
            DigitalSignature.persist(keys, privateKeyFile.toPath(), publicKeyFile.toPath());
        } else {

            signaturePrivateKey = DigitalSignature.getPrivateKey(privateKeyFile.toPath());
            signatureCertificate = DigitalSignature.getCertificate(publicKeyFile.toPath());
        }
    }

}
