package model.implementations.identity;

import model.exceptions.UnknownIdentityException;
import model.interfaces.identity.Identity;
import model.interfaces.identity.IdentityToken;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO - keep this for more advanced system.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class MultiSession {

    protected static IdentityToken nextToken;
    private Map<IdentityToken, Identity> tokeys;

    public MultiSession() {
        tokeys = new HashMap<IdentityToken, Identity>();
        nextToken = new IdentityTokenImpl(1);
    }

    public IdentityToken addIdentity(Identity identity) {
        tokeys.put(nextToken, identity);
        IdentityToken ret = nextToken;

        nextToken = nextToken.next();

        return ret;
    }

    public void removeIdentity(IdentityToken tokey) throws UnknownIdentityException {
        if (!containsIdentity(tokey))
            throw new UnknownIdentityException();

        tokeys.remove(tokey);
    }

    public Map<IdentityToken, Identity> getAllRegisteredIdentities() {
        return tokeys;
    }

    private boolean containsIdentity(IdentityToken tokey) {
        return tokeys.containsKey(tokey);
    }
}
