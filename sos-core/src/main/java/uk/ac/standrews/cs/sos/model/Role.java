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
 *      "Email": "sic2@st-andrews.ac.uk",
 *      "PubKey": "1342242234",
 *      "Signature": "MQ17983827se="
 * }
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Role extends User {

    IGUID guid(); // random GUID

    IGUID getUser(); // e.g. guid for user Simone

    String getName(); // e.g. Simone's work

    String getEmail(); // sic2@st-andrews.ac.uk

    PublicKey getPubkey();

    String getSignature(); // signed using the user public key

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
