package uk.ac.standrews.cs.sos.impl.roles;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.model.User;
import uk.ac.standrews.cs.sos.utils.SignatureCrypto;

import java.security.PublicKey;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class UserImpl implements User {

    private IGUID guid;
    private String name;
    private PublicKey pubkey;
    private SignatureCrypto signature;

    public UserImpl(String name) {
        guid = GUIDFactory.generateRandomGUID();
        this.name = name;

        signature = new SignatureCrypto();
        this.pubkey = signature.getPublicKey();
    }


    public UserImpl(IGUID guid, String name) {
        // Should be used to construct existing user
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
    public PublicKey getSignaturePubKey() {
        return pubkey;
    }
}
