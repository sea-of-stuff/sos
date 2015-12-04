package model.implementations.components.manifests;

import model.interfaces.identity.Identity;
import model.interfaces.identity.Signature;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class SignedManifest extends BasicManifest {

    private Identity identity;
    private Signature signature;

    protected SignedManifest(Identity identity, String manifestType) {
        super(manifestType);
        this.identity = identity;
    }

    public Signature getSignature() {
        return this.signature;
    }

    protected Signature generateSignature(Identity identity) {
        return null;
    }
}
