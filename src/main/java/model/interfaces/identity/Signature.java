package model.interfaces.identity;


import model.interfaces.components.Manifest;

import java.security.SignatureException;

/**
 * A signature is used within manifests and tells if manifest is trustable
 * or not by some client (demonstrates authenticity).
 *
 * The signature is trustable if and only if one trusts the signeer.
 *
 * @see Manifest
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Signature {

    // TODO - other methods to set algorithm and provider?
    // TODO - methods to verify signature?

    /**
     * Sign a given manifest using a secret private key
     *
     * @param manifest      to be signed
     * @param identity      used for signing the manifest
     * @return the signature bytes of the signing operation's result.
     * @throws SignatureException
     */
    byte[] sign(Manifest manifest, Identity identity) throws SignatureException;
}
