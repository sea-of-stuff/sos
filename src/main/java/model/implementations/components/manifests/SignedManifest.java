package model.implementations.components.manifests;

import model.interfaces.identity.Identity;
import model.interfaces.identity.Signature;

/**
 * Abstract class for all manifests that support signatures.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class SignedManifest extends BasicManifest {

    private Identity identity;
    private Signature signature;

    /**
     * Constructor for a signed manifest.
     *
     * @param identity
     * @param manifestType
     */
    protected SignedManifest(Identity identity, String manifestType) {
        super(manifestType);
        this.identity = identity;
    }

    /**
     * Gets the signature of this manifest.
     *
     * @return signature of this manifest.
     */
    public Signature getSignature() {
        return this.signature;
    }

    /**
     * Generates the signature for this manifest.
     *
     * @param identity used to sign the manifest.
     * @return signature of this manifest.
     */
    protected Signature generateSignature(Identity identity) {
        return null;
    }
}
