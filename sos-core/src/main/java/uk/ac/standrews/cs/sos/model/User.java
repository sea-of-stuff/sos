package uk.ac.standrews.cs.sos.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.crypto.SignatureException;
import uk.ac.standrews.cs.sos.json.UserDeserializer;
import uk.ac.standrews.cs.sos.json.UserSerializer;

import java.security.PublicKey;

/**
 * Used to create roles
 * A user has one or more roles
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@JsonSerialize(using = UserSerializer.class)
@JsonDeserialize(using = UserDeserializer.class)
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
     * @throws SignatureException if the text could not be signed
     */
    String sign(String text) throws SignatureException;

    /**
     * Verify that the given text and signature match
     *
     * @param text
     * @param signatureToVerify
     * @return
     * @throws SignatureException
     */
    boolean verify(String text, String signatureToVerify) throws SignatureException;

    boolean hasPrivateKeys();

}
