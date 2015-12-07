package model.implementations.components.manifests;

import model.exceptions.EncryptionException;
import model.interfaces.identity.Identity;

/**
 * Abstract class for all manifests that support signatures.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class SignedManifest extends BasicManifest {

    protected Identity identity;
    protected String signature;

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
    public String getSignature() {
        return this.signature;
    }

    /**
     * Generates the signature for this manifest.
     *
     * @return signature of this manifest.
     */
    protected abstract void generateSignature() throws EncryptionException;
}
