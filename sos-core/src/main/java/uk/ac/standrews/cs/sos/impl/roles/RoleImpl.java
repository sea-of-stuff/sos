package uk.ac.standrews.cs.sos.impl.roles;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.crypto.ProtectionException;
import uk.ac.standrews.cs.sos.exceptions.crypto.SignatureException;
import uk.ac.standrews.cs.sos.json.RoleDeserializer;
import uk.ac.standrews.cs.sos.json.RoleSerializer;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.model.User;
import uk.ac.standrews.cs.sos.utils.JSONHelper;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;
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

    private final static String KEYS_FOLDER = System.getProperty("user.home") + "/sos/keys/"; // TODO - this info should be in the constants OR in the LocalStorage.java

    private PrivateKey signaturePrivateKey;
    private PublicKey signatureCertificate;

    private PrivateKey protectionPrivateKey;
    private PublicKey protectionPublicKey;

    /**
     *
     * Keys are either created and persisted, or loaded
     *
     * @param user used to create this role
     * @param guid for this role
     * @param name for this role
     * @throws SignatureException if the signature keys or the signature for this role could not be generated
     * @throws ProtectionException if the protection keys could not be generated
     */
    public RoleImpl(User user, IGUID guid, String name) throws SignatureException, ProtectionException {
        this.userGUID = user.guid();
        this.roleGUID = guid;
        this.name = name;

        manageSignatureKeys(false);
        manageProtectionKey(false);

        this.signature = user.sign("TODO - this role string representation, which is not the JSON");
    }

    public RoleImpl(User user, String name) throws ProtectionException, SignatureException {
        this(user, GUIDFactory.generateRandomGUID(), name);
    }

    /**
     * This constructor is used to create a Role object for an already existing role.
     * Thus it will try to load the keys from disk. It is okay for the role not to have all the keys.
     * For example, one might wish to create a role that does not "own".
     *
     * @param userGUID
     * @param guid
     * @param name
     * @param signature
     * @param signatureCertificate
     * @param protectionPublicKey
     * @throws ProtectionException
     * @throws SignatureException
     */
    public RoleImpl(IGUID userGUID, IGUID guid, String name, String signature, PublicKey signatureCertificate, PublicKey protectionPublicKey) throws ProtectionException, SignatureException {
        this.userGUID = userGUID;
        this.roleGUID = guid;
        this.name = name;

        this.signature = signature;

        this.signatureCertificate = signatureCertificate;
        this.protectionPublicKey = protectionPublicKey;

        // Meanwhile attempt to load any private keys.
        manageSignatureKeys(true);
        manageProtectionKey(true);
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
        return protectionPublicKey;
    }

    @Override
    public String getSignature() {
        return signature;
    }

    @Override
    public String sign(String text) throws SignatureException {

        try {
            return DigitalSignature.sign64(signaturePrivateKey, text);
        } catch (CryptoException e) {
            throw new SignatureException(e);
        }
    }

    @Override
    public boolean verify(String text, String signatureToVerify) throws SignatureException {

        try {
            return DigitalSignature.verify64(signatureCertificate, text, signatureToVerify);
        } catch (CryptoException e) {
            throw new SignatureException(e);
        }
    }

    @Override
    public String encrypt(SecretKey key) throws ProtectionException {

        try {
            PublicKey publicKey = getPubKey();
            return AsymmetricEncryption.encryptAESKey(publicKey, key);
        } catch (CryptoException e) {
            throw new ProtectionException(e);
        }
    }

    @Override
    public SecretKey decrypt(String encryptedKey) throws ProtectionException {

        if (protectionPrivateKey == null) throw new ProtectionException("No protection private key");

        try {
            return AsymmetricEncryption.decryptAESKey(protectionPrivateKey, encryptedKey);
        } catch (CryptoException e) {
            throw new ProtectionException(e);
        }
    }

    /**
     * Attempt to load the private key and the certificate for the digital signature.
     * If keys cannot be loaded, then generate them and save to disk
     *
     * @param loadOnly if true, it will try to load the keys, but not to generate them
     * @throws SignatureException if an error occurred while managing the keys
     */
    private void manageSignatureKeys(boolean loadOnly) throws SignatureException {

        try {
            File publicKeyFile = new File(KEYS_FOLDER + roleGUID + DigitalSignature.CERTIFICATE_EXTENSION);
            if (signatureCertificate == null && publicKeyFile.exists()) {
                signatureCertificate = DigitalSignature.getCertificate(publicKeyFile.toPath());
            }

            File privateKeyFile = new File(KEYS_FOLDER + roleGUID + DigitalSignature.PRIVATE_KEY_EXTENSION);
            if (signaturePrivateKey == null && privateKeyFile.exists()) {
                signaturePrivateKey = DigitalSignature.getPrivateKey(privateKeyFile.toPath());
            }


            if (!loadOnly && signatureCertificate != null && signaturePrivateKey != null) {

                KeyPair keys = DigitalSignature.generateKeys();
                signatureCertificate = keys.getPublic();
                signaturePrivateKey = keys.getPrivate();

                DigitalSignature.persist(keys, Paths.get(KEYS_FOLDER + roleGUID), Paths.get(KEYS_FOLDER + roleGUID));
            }

        } catch (CryptoException e) {
            throw new SignatureException(e);
        }
    }

    /**
     * Attempt to load the private and public keys for the asymmetric protection key.
     * If keys cannot be loaded, then generate them and save to disk
     *
     * @param loadOnly if true, it will try to load the keys, but not to generate them
     * @throws ProtectionException if an error occurred while managing the keys
     */
    private void manageProtectionKey(boolean loadOnly) throws ProtectionException {

        try {
            File publicKeyFile = new File(KEYS_FOLDER + roleGUID + "_pub" + AsymmetricEncryption.KEY_EXTENSION);
            if (protectionPublicKey == null && publicKeyFile.exists()) {
                protectionPublicKey = AsymmetricEncryption.getPublicKey(publicKeyFile.toPath());
            }

            File privateKeyFile = new File(KEYS_FOLDER + roleGUID + AsymmetricEncryption.KEY_EXTENSION);
            if (protectionPrivateKey == null && privateKeyFile.exists()) {
                protectionPrivateKey = AsymmetricEncryption.getPrivateKey(privateKeyFile.toPath());
            }

            if (!loadOnly && protectionPublicKey != null && protectionPrivateKey != null) {

                KeyPair asymmetricKeys = AsymmetricEncryption.generateKeys();
                protectionPublicKey = asymmetricKeys.getPublic();
                protectionPrivateKey = asymmetricKeys.getPrivate();

                AsymmetricEncryption.persist(asymmetricKeys, Paths.get(KEYS_FOLDER + roleGUID), Paths.get(KEYS_FOLDER + roleGUID + "_pub"));
            }

        } catch (CryptoException e) {
            throw new ProtectionException(e);
        }

    }

    @Override
    public String toString() {
        try {
            return JSONHelper.JsonObjMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            SOS_LOG.log(LEVEL.ERROR, "Unable to generate JSON for role " + guid());
            return "";
        }
    }

}
