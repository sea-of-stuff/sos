package uk.ac.standrews.cs.sos.model.roles;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.identity.DecryptionException;
import uk.ac.standrews.cs.sos.exceptions.identity.EncryptionException;
import uk.ac.standrews.cs.sos.interfaces.Role;

import java.security.Key;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class RoleImpl implements Role { // this will take over the identity class

    // TODO - json serialization

    private IGUID roleGUID;
    private Key pubkey;
    private String name;
    private String authorName;
    private String email;

    public RoleImpl(Key pubkey, String name, String authorName, String email) {
        this.pubkey = pubkey;
        this.name = name;
        this.authorName = authorName;
        this.email = email;

        roleGUID = GUIDFactory.generateRandomGUID();
    }

    @Override
    public IGUID guid() {
        return roleGUID;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAuthorName() {
        return authorName;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public Key getPubkey() {
        return pubkey;
    }

    /**
     * Sign the given text and return a byte array representing the signature
     *
     * @param text
     * @return
     * @throws EncryptionException
     */
    @Override
    public String sign(String text) throws EncryptionException { return ""; }

    /**
     * Verify that the given text and signature match
     *
     * @param text
     * @param signature
     * @return
     * @throws DecryptionException
     */
    @Override
    public boolean verify(String text, String signature) throws DecryptionException { return false; }

}
