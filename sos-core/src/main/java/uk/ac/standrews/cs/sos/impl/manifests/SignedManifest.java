package uk.ac.standrews.cs.sos.impl.manifests;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.crypto.DecryptionException;
import uk.ac.standrews.cs.sos.exceptions.crypto.EncryptionException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestVerificationException;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.model.Role;

/**
 * Abstract class for all manifests that support signatures.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class SignedManifest extends BasicManifest {

    final protected IGUID signer;
    protected String signature;

    /**
     * Constructor for a signed manifest.
     *
     * @param signer
     * @param manifestType
     */
    protected SignedManifest(IGUID signer, ManifestType manifestType) {
        super(manifestType);
        this.signer = signer;
    }

    public IGUID getSigner() {
        return this.signer;
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
     * @param role
     * @return
     * @throws DecryptionException
     */
    @Override
    public boolean verifySignature(Role role) throws ManifestVerificationException {

        boolean success;
        try {
            String manifestToSign = getManifestToSign();
            success = role.verify(manifestToSign, signature);
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
