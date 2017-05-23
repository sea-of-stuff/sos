package uk.ac.standrews.cs.sos.model;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.utilities.crypto.CryptoException;

import java.security.PublicKey;

/**
 * Used to create roles
 * A user has one or more roles
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface User {

    /**
     * Unique GUID for the user
     *
     * @return guid of the user
     */
    IGUID guid();

    /**
     * Human-readable name for the user
     * e.g. Simone Ivan Conte
     *
     * @return name of the user
     */
    String getName();

    /**
     * Public key of the user used to generate signatures
     *
     * @return public key of the user
     */
    PublicKey getSignatureCertificate();

    /**
     * Sign some given text using this user private key
     *
     * @param text to be signed
     * @return the signed text
     * @throws CryptoException if the text could not be signed
     */
    String sign(String text) throws CryptoException;

}
