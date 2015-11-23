package model.interfaces.components.identity;

import java.nio.file.Path;
import java.security.Key;

/**
 * Represents an identity of a client within the Sea of Stuff.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Identity {

    /**
     * Load identity - TODO - path to keys?
     * @param path
     */
    void loadIdentity(Path path);

    Key getPublicKey();

}
