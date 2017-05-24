package uk.ac.standrews.cs.sos.impl.roles;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.crypto.SignatureException;
import uk.ac.standrews.cs.sos.model.User;
import uk.ac.standrews.cs.utilities.crypto.CryptoException;
import uk.ac.standrews.cs.utilities.crypto.DigitalSignature;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
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
}
