package model.implementations.components.identity;

import model.interfaces.components.identity.Identity;
import model.interfaces.components.identity.IdentityToken;
import model.interfaces.components.identity.Session;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the current session of this view of the Sea of Stuff
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SessionImpl implements Session {

    protected static IdentityToken nextToken;
    private Map<IdentityToken, Identity> tokeys;

    public SessionImpl() {
        tokeys = new HashMap<IdentityToken, Identity>();
        nextToken = new IdentityTokenImpl(1);
    }

    public IdentityToken addIdentity(Identity identity) {
        tokeys.put(nextToken, identity);
        IdentityToken ret = nextToken;

        nextToken = nextToken.next();

        return ret;
    }

    public void removeIdentity(IdentityToken tokey) {
        tokeys.remove(tokey);
    }

    @Override
    public Map<IdentityToken, Identity> getAllRegisteredIdentities() {
        return tokeys;
    }
}
