package uk.ac.standrews.cs.sos.impl.roles;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.crypto.SignatureException;
import uk.ac.standrews.cs.sos.json.UserDeserializer;
import uk.ac.standrews.cs.sos.json.UserSerializer;
import uk.ac.standrews.cs.sos.model.User;
import uk.ac.standrews.cs.sos.utils.JSONHelper;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;
import uk.ac.standrews.cs.utilities.crypto.CryptoException;
import uk.ac.standrews.cs.utilities.crypto.DigitalSignature;

import java.io.File;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@JsonSerialize(using = UserSerializer.class)
@JsonDeserialize(using = UserDeserializer.class)
public class UserImpl implements User {

    protected final static String KEYS_FOLDER = System.getProperty("user.home") + "/sos/keys/"; // TODO - this info should be in the constants OR in the LocalStorage.java

    private IGUID guid;
    private String name;

    protected PublicKey signatureCertificate;
    private PrivateKey signaturePrivateKey;

    public UserImpl(String name) throws SignatureException {
        this(GUIDFactory.generateRandomGUID(), name);
    }

    UserImpl(IGUID guid, String name) throws SignatureException {
        this.guid = guid;
        this.name = name;

        manageSignatureKeys(false);
    }

    public UserImpl(IGUID guid, String name, PublicKey signatureCertificate) throws SignatureException {
        this.guid = guid;
        this.name = name;
        this.signatureCertificate = signatureCertificate;

        manageSignatureKeys(true);
    }

    @Override
    public IGUID guid() {
        return guid;
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
    public String toString() {
        try {
            return JSONHelper.JsonObjMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            SOS_LOG.log(LEVEL.ERROR, "Unable to generate JSON for user/role " + guid() + " instanceof " + this.getClass().getName());
            return "";
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
            File publicKeyFile = new File(KEYS_FOLDER + guid() + DigitalSignature.CERTIFICATE_EXTENSION);
            if (signatureCertificate == null && publicKeyFile.exists()) {
                signatureCertificate = DigitalSignature.getCertificate(publicKeyFile.toPath());
            }

            File privateKeyFile = new File(KEYS_FOLDER + guid() + DigitalSignature.PRIVATE_KEY_EXTENSION);
            if (signaturePrivateKey == null && privateKeyFile.exists()) {
                signaturePrivateKey = DigitalSignature.getPrivateKey(privateKeyFile.toPath());
            }


            if (!loadOnly && signatureCertificate == null && signaturePrivateKey == null) {

                KeyPair keys = DigitalSignature.generateKeys();
                signatureCertificate = keys.getPublic();
                signaturePrivateKey = keys.getPrivate();

                DigitalSignature.persist(keys, Paths.get(KEYS_FOLDER + guid()), Paths.get(KEYS_FOLDER + guid()));
            }

        } catch (CryptoException e) {
            throw new SignatureException(e);
        }
    }

}
