package uk.ac.standrews.cs.sos.impl.roles;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.crypto.ProtectionException;
import uk.ac.standrews.cs.sos.exceptions.crypto.SignatureException;
import uk.ac.standrews.cs.sos.json.RoleDeserializer;
import uk.ac.standrews.cs.sos.json.RoleSerializer;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.model.User;
import uk.ac.standrews.cs.utilities.crypto.AsymmetricEncryption;
import uk.ac.standrews.cs.utilities.crypto.CryptoException;

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
public class RoleImpl extends UserImpl implements Role {

    private IGUID userGUID;
    private String signature;

    private PrivateKey protectionPrivateKey;
    private PublicKey protectionPublicKey;

    /**
     *
     * Keys are either created and persisted, or loaded
     *
     * @param user used to create this role
     * @param name for this role
     * @throws SignatureException if the signature keys or the signature for this role could not be generated
     * @throws ProtectionException if the protection keys could not be generated
     */
    public RoleImpl(User user, String name) throws ProtectionException, SignatureException {
        super(GUIDFactory.generateRandomGUID(), name);
        this.userGUID = user.guid();

        manageProtectionKey(false);

        this.signature = user.sign("TODO - this role string representation, which is not the JSON");
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
        super(guid, name, signatureCertificate);
        this.userGUID = userGUID;

        this.signature = signature;
        this.protectionPublicKey = protectionPublicKey;

        // Meanwhile attempt to load any private keys.
        manageProtectionKey(true);
    }

    @Override
    public IGUID getUser() {
        return userGUID;
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
     * Attempt to load the private and public keys for the asymmetric protection key.
     * If keys cannot be loaded, then generate them and save to disk
     *
     * @param loadOnly if true, it will try to load the keys, but not to generate them
     * @throws ProtectionException if an error occurred while managing the keys
     */
    private void manageProtectionKey(boolean loadOnly) throws ProtectionException {

        try {
            File publicKeyFile = new File(keysFolder + guid().toMultiHash() + "_pub" + AsymmetricEncryption.KEY_EXTENSION);
            if (protectionPublicKey == null && publicKeyFile.exists()) {
                protectionPublicKey = AsymmetricEncryption.getPublicKey(publicKeyFile.toPath());
            }

            File privateKeyFile = new File(keysFolder + guid().toMultiHash() + AsymmetricEncryption.KEY_EXTENSION);
            if (protectionPrivateKey == null && privateKeyFile.exists()) {
                protectionPrivateKey = AsymmetricEncryption.getPrivateKey(privateKeyFile.toPath());
            }

            if (!loadOnly && protectionPublicKey == null && protectionPrivateKey == null) {

                KeyPair asymmetricKeys = AsymmetricEncryption.generateKeys(512);
                protectionPublicKey = asymmetricKeys.getPublic();
                protectionPrivateKey = asymmetricKeys.getPrivate();

                AsymmetricEncryption.persist(asymmetricKeys, Paths.get(keysFolder + guid().toMultiHash()), Paths.get(keysFolder + guid().toMultiHash() + "_pub"));
            }

        } catch (CryptoException e) {
            throw new ProtectionException(e);
        }

    }

}
