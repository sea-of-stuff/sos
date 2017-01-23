package uk.ac.standrews.cs.sos.model.roles;

import uk.ac.standrews.cs.IGUID;

import java.security.Key;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Role {

    private IGUID roleGUID; // TODO - randomly generated?
    private Key pubkey;

    private String name; // e.g. Dean of Sci
    private String authorName; // e.g. Alan Dearle
    private String email; // dean.sci@st-andrews.ac.uk

    public Role(Key pubkey, String name, String authorName, String email) {
        this.pubkey = pubkey;

        this.name = name;
        this.authorName = authorName;
        this.email = email;
    }

    public IGUID getRoleGUID() {
        return roleGUID;
    }

    public Key getPubkey() {
        return pubkey;
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
}
