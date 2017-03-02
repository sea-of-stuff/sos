package uk.ac.standrews.cs.sos.interfaces.identity;

import uk.ac.standrews.cs.sos.exceptions.identity.DecryptionException;
import uk.ac.standrews.cs.sos.exceptions.identity.EncryptionException;

import java.security.Key;

/**
 * Represents an identity of a client within the Sea of Stuff.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Identity { // FIXME - rename to Role (will be managed by Role Service Manager)

    /**
     * Sign the given text and return a byte array representing the signature
     *
     * @param text
     * @return
     * @throws EncryptionException
     */
    String sign(String text) throws EncryptionException;

    /**
     * Verify that the given text and signature match
     *
     * @param text
     * @param signature
     * @return
     * @throws DecryptionException
     */
    boolean verify(String text, String signature) throws DecryptionException;

    /**
     * Get the public key for this identity
     *
     * @return public key of this identity
     */
    Key getPublicKey();

}
