package uk.ac.standrews.cs.sos.model.manifests;

import org.apache.commons.codec.binary.Base64;
import uk.ac.standrews.cs.sos.exceptions.identity.DecryptionException;
import uk.ac.standrews.cs.sos.exceptions.identity.EncryptionException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.interfaces.Identity;

/**
 * Abstract class for all manifests that support signatures.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class SignedManifest extends BasicManifest {

    final protected Identity identity;
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

    @Override
    public boolean verify(Identity identity) throws DecryptionException {
        String manifestSectionToSign = getManifestToSign();
        byte[] decodedBytes = Base64.decodeBase64(signature);
        return identity.verify(manifestSectionToSign, decodedBytes);
    }

    protected String makeSignature() throws ManifestNotMadeException {
        String signature;
        try {
            String manifestSectionToSign = getManifestToSign();
            signature = generateSignature(manifestSectionToSign);
        } catch (Exception e) {
            throw new ManifestNotMadeException();
        }
        return signature;
    }

    protected abstract String getManifestToSign();

    /**
     * Generates the signature for this manifest.
     *
     * @return signature of this manifest.
     */
    protected abstract String generateSignature(String toSign) throws EncryptionException;
}
