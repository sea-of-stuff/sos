package uk.ac.standrews.cs.sos.model.roles;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.identity.DecryptionException;
import uk.ac.standrews.cs.sos.exceptions.identity.EncryptionException;

import java.security.Key;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Role { // this will take over the identity class

    // TODO - json serialization

    private IGUID roleGUID; // TODO - randomly generated?
    private Key pubkey;

    private String name; // e.g. Simone's work
    private String authorName; // e.g. Simone Ivan Conte
    private String email; // sic2@st-andrews.ac.uk

    public Role(Key pubkey, String name, String authorName, String email) {
        this.pubkey = pubkey;

        this.name = name;
        this.authorName = authorName;
        this.email = email;

        roleGUID = GUIDFactory.generateRandomGUID();
    }

    public IGUID getRoleGUID() {
        return roleGUID;
    }

    public String getName() {
        return name;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getEmail() {
        return email;
    }

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
    String sign(String text) throws EncryptionException { return ""; }

    /**
     * Verify that the given text and signature match
     *
     * @param text
     * @param signature
     * @return
     * @throws DecryptionException
     */
    boolean verify(String text, String signature) throws DecryptionException { return false; }

}
