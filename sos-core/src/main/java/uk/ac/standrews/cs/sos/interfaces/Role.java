package uk.ac.standrews.cs.sos.interfaces;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.identity.DecryptionException;
import uk.ac.standrews.cs.sos.exceptions.identity.EncryptionException;

import java.security.Key;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Role {

    IGUID guid(); // random GUID

    String getName(); // e.g. Simone's work

    String getAuthorName(); // e.g. Simone Ivan Conte

    String getEmail(); // sic2@st-andrews.ac.uk

    Key getPubkey();

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

}
