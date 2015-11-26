package model.implementations.components.identity;

import model.exceptions.UnknownIdentityException;
import model.interfaces.components.identity.Identity;
import model.interfaces.components.identity.IdentityToken;

/**
 * Represents the current session of this view of the Sea of Stuff
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Session {

    protected static IdentityToken nextToken;

    private IdentityToken tokey;
    private Identity identity;

    public Session() {
        nextToken = new IdentityTokenImpl(1);
    }

    public IdentityToken addIdentity(Identity identity) {
        tokey = nextToken;
        this.identity = identity;

        nextToken = nextToken.next();

        return tokey;
    }

    public void removeIdentity(IdentityToken tokey) throws UnknownIdentityException {
        this.tokey = null;
        this.identity = null;
    }

    public Identity getRegisteredIdentity() {
        return identity;
    }

}
