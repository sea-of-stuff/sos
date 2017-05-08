package uk.ac.standrews.cs.sos.model;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.crypto.DecryptionException;
import uk.ac.standrews.cs.sos.exceptions.crypto.EncryptionException;

import java.security.PublicKey;

/**
 *
 * {
 *      "GUID": "a243",
 *      "Name": "Simone's work",
 *      "User": "2321aaa3",
 *      "Signature_PubKey": "1342242234",
 *      "Data_PubKey" : "13442421",
 *      "Signature": "MQ17983827se=" // Generated using User's keys
 * }
 *
 * TODO: consider sharing algorithm info about encryption, signatures, etc
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Role extends User {

    IGUID guid(); // random GUID

    /**
     * Get the GUID for the user that created this Role
     * e.g. guid for user Simone
     *
     * @return
     */
    IGUID getUser();

    /**
     * Get the name of the role
     * e.g. Simone's work
     *
     * @return
     */
    String getName();

    /**
     * Used to sign metadata, manifests, etc
     *
     * @return
     */
    PublicKey getSignaturePubKey();

    /**
     * Used to encrypt data
     *
     * @return
     */
    PublicKey getDataPubKey();

    /**
     * Signature for this role manifest.
     * This signature is generated using the User public key.
     *
     * @return
     */
    String getSignature();

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
     * @param signatureToVerify
     * @return
     * @throws DecryptionException
     */
    boolean verify(String text, String signatureToVerify) throws DecryptionException;

}
