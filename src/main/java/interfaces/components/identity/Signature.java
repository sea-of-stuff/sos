package interfaces.components.identity;

import interfaces.components.manifests.Manifest;

import java.security.PrivateKey;
import java.security.SignatureException;

/**
 * A signature is used within manifests and tells if manifest is trustable
 * or not by some client (demonstrates authenticity)
 *
 * @see Manifest
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Signature {

    // TODO - other methods to set algorithm and provider?

    /**
     * Sign a given manifest using a secret private key
     * @param manifest      to be signed
     * @param privateKey    used for signing the manifest
     * @return the signature bytes of the signing operation's result.
     * @throws SignatureException
     */
    byte[] sign(Manifest manifest, PrivateKey privateKey) throws SignatureException;
}
