package uk.ac.standrews.cs.sos.impl.roles;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.model.User;
import uk.ac.standrews.cs.utilities.crypto.CryptoException;
import uk.ac.standrews.cs.utilities.crypto.DigitalSignature;

import java.security.KeyPair;
import java.security.PublicKey;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class UserImpl implements User {

    private IGUID guid;
    private String name;
    private PublicKey signatureCertificate;

    public UserImpl(String name) {
        this(GUIDFactory.generateRandomGUID(), name);
    }

    public UserImpl(IGUID guid, String name) {
        this.guid = guid;
        this.name = name;

        try {
            KeyPair keyPair = DigitalSignature.generateKeys();
            this.signatureCertificate = keyPair.getPublic();
        } catch (CryptoException e) {
            e.printStackTrace();
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
}
