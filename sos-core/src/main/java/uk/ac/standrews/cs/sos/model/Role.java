package uk.ac.standrews.cs.sos.model;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.crypto.ProtectionException;

import javax.crypto.SecretKey;
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

    /**
     * GUID for this role
     *
     * @return guid of this role
     */
    IGUID guid();

    /**
     * Get the GUID for the user that created this Role
     * e.g. guid for user Simone
     *
     * @return guid of the user
     */
    IGUID getUser();

    /**
     * Get the name of the role
     * e.g. Simone's work
     *
     * @return name for the role
     */
    String getName();

    /**
     * Used to sign metadata, manifests, etc
     *
     * @return certificate for this role
     */
    PublicKey getSignatureCertificate();

    /**
     * Used to encrypt symmetric keys
     * This is an asymmetric key, such as RSA
     *
     * @return key to sign keys
     */
    PublicKey getPubKey();

    /**
     * Signature for this role manifest.
     * This signature is generated using the User public key.
     *
     * @return signfature for this role
     */
    String getSignature();

    /**
     * Encrypt a symmetric key using an asymmetric key
     *
     * @param key
     * @return
     * @throws ProtectionException
     */
    String encrypt(SecretKey key) throws ProtectionException;

    /**
     * Encrypted key is decripted using the private key (e.g. RSA)
     *
     * @param encryptedKey
     * @return
     * @throws ProtectionException
     */
    SecretKey decrypt(String encryptedKey) throws ProtectionException;

}
