package uk.ac.standrews.cs.sos.impl.roles;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.json.RoleDeserializer;
import uk.ac.standrews.cs.sos.json.RoleSerializer;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.model.User;
import uk.ac.standrews.cs.utilities.crypto.AsymmetricEncryption;
import uk.ac.standrews.cs.utilities.crypto.CryptoException;
import uk.ac.standrews.cs.utilities.crypto.DigitalSignature;

import javax.crypto.SecretKey;
import java.io.File;
import java.nio.file.Paths;
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
    private String signature;

    private final static String KEYS_FOLDER = System.getProperty("user.home") + "/sos/keys/";

    private PrivateKey signaturePrivateKey;
    private PublicKey signatureCertificate;

    private KeyPair asymmetricKeys;

    /**
     *
     * keys are either created and persisted, or loaded
     *
     * @param guid
     * @param name
     * @throws CryptoException
     */
    public RoleImpl(User user, IGUID guid, String name) throws CryptoException {
        this.userGUID = user.guid();
        this.roleGUID = guid;
        this.name = name;

        manageSignatureKeys();

        // Keys to encrypt AES keys
        asymmetricKeys = AsymmetricEncryption.generateKeys();
        // TODO - load/save keys

        signature = user.sign("TODO - this role string representation");
    }

    // FIXME - better exceptions (e.g. RoleException)
    public RoleImpl(User user, String name) throws CryptoException {
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
    public String sign(String text) throws CryptoException {

        return DigitalSignature.sign64(signaturePrivateKey, text);
    }

    @Override
    public boolean verify(String text, String signatureToVerify) throws CryptoException {

        return DigitalSignature.verify64(signatureCertificate, text, signatureToVerify);
    }

    @Override
    public String encrypt(SecretKey key) throws CryptoException {

        PublicKey publicKey = getPubKey();
        return AsymmetricEncryption.encryptAESKey(publicKey, key);
    }

    @Override
    public SecretKey decrypt(String encryptedKey) {

        return null;
    }

    /**
     * Attempt to load the private key and the certificate for the digital signature.
     * If keys cannot be loaded, then generate them and save to disk
     *
     * @throws CryptoException if an error occurred while managing the keys
     */
    private void manageSignatureKeys() throws CryptoException {

        File publicKeyFile = new File(KEYS_FOLDER + roleGUID + "_pub.pem");
        if (publicKeyFile.exists()) {
            signatureCertificate = DigitalSignature.getCertificate(publicKeyFile.toPath());
        }

        File privateKeyFile = new File(KEYS_FOLDER + roleGUID + ".pem");
        if (privateKeyFile.exists()) {
            signaturePrivateKey = DigitalSignature.getPrivateKey(privateKeyFile.toPath());
        }


        if (signatureCertificate != null && signaturePrivateKey != null) {

            KeyPair keys = DigitalSignature.generateKeys();
            DigitalSignature.persist(keys, Paths.get(KEYS_FOLDER + roleGUID), Paths.get(KEYS_FOLDER + roleGUID + "_pub"));
        }

    }

}
