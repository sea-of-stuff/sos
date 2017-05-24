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

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@JsonSerialize(using = UserSerializer.class)
@JsonDeserialize(using = UserDeserializer.class)
public class UserImpl implements User {

    private IGUID guid;
    private String name;

    private PrivateKey signaturePrivateKey;
    private PublicKey signatureCertificate;

    public UserImpl(String name) throws SignatureException {
        this(GUIDFactory.generateRandomGUID(), name);
    }

    public UserImpl(IGUID guid, String name) throws SignatureException {
        this.guid = guid;
        this.name = name;

        try {

            KeyPair keyPair = DigitalSignature.generateKeys();
            this.signaturePrivateKey = keyPair.getPrivate();
            this.signatureCertificate = keyPair.getPublic();

        } catch (CryptoException e) {
            throw new SignatureException(e);
        }
    }

    public UserImpl(IGUID guid, String name, PublicKey signatureCertificate) throws SignatureException {
        this.guid = guid;
        this.name = name;
        this.signatureCertificate = signatureCertificate;
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
    public String toString() {
        try {
            return JSONHelper.JsonObjMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            SOS_LOG.log(LEVEL.ERROR, "Unable to generate JSON for user " + guid());
            return "";
        }
    }

}
