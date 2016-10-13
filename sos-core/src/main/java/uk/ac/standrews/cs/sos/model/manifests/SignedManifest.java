package uk.ac.standrews.cs.sos.model.manifests;

import org.apache.commons.codec.binary.Base64;
import uk.ac.standrews.cs.sos.exceptions.identity.DecryptionException;
import uk.ac.standrews.cs.sos.exceptions.identity.EncryptionException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestVerificationException;
import uk.ac.standrews.cs.sos.interfaces.identity.Identity;

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
    protected SignedManifest(Identity identity, ManifestType manifestType) {
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
     * Verify this manifest against the given identity
     * @param identity
     * @return
     * @throws DecryptionException
     */
    @Override
    public boolean verify(Identity identity) throws ManifestVerificationException {
        String manifestToSign = getManifestToSign();

        byte[] decodedBytes = Base64.decodeBase64(signature);
        boolean success;
        try {
            success = identity.verify(manifestToSign, decodedBytes);
        } catch (DecryptionException e) {
            throw new ManifestVerificationException("Unable to decrypt identity", e);
        }

        return success;
    }

    /**
     * Generate the signature for this manifest
     * @return
     * @throws ManifestNotMadeException
     */
    protected String makeSignature() throws ManifestNotMadeException {
        String signature;
        try {
            String manifestToSign = getManifestToSign();
            signature = generateSignature(manifestToSign);
        } catch (Exception e) {
            throw new ManifestNotMadeException("Manifest could not be signed", e);
        }
        return signature;
    }

    /**
     * Get the manifest sections to sign
     * @return manifest sections to sign as a string
     */
    protected abstract String getManifestToSign();

    /**
     * Generates the signature for this manifest.
     *
     * @return signature of this manifest.
     */
    protected abstract String generateSignature(String toSign) throws EncryptionException;
}
