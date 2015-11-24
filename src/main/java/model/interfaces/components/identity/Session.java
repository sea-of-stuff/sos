package model.interfaces.components.identity;

import java.util.Map;

/**
 * Session of this access point to the Sea of Stuff.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Session {

    /**
     * Add
     * @param identity
     * @return
     */
    IdentityToken addIdentity(Identity identity);

    /**
     *
     * @param tokey
     */
    void removeIdentity(IdentityToken tokey);

    /**
     *
     * @return
     */
    Map<IdentityToken, Identity> getAllRegisteredIdentities();
}
