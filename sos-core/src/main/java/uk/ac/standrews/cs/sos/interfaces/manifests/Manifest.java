package uk.ac.standrews.cs.sos.interfaces.manifests;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.identity.DecryptionException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestVerificationException;
import uk.ac.standrews.cs.sos.interfaces.identity.Identity;
import uk.ac.standrews.cs.sos.model.manifests.ManifestType;

/**
 * A manifest is an entity that describes assets, compounds and atoms by
 * recording metadata about them.
 * <p>
 * A manifest is not updatable.
 * <br>
 * Manifests are publishable within the sea of stuff and allow discoverability
 * of assets, compounds and atoms.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Manifest {

    /**
     * Gets the GUID of the content referenced by this manifest.
     *
     * @return guid of the content.
     */
    IGUID getContentGUID();

    /**
     * Verify this manifest's GUID against its content.
     *
     * @param identity
     * @return true if the GUID of the manifest matches the content.
     * @throws GUIDGenerationException if the GUIDs of the manifests could not be generated
     *                                  due to uk.ac.standrews.cs.IO, network or other issues.
     * @throws DecryptionException
     */
    boolean verify(Identity identity) throws ManifestVerificationException;

    /**
     * Check that the key-value pairs contained in the manifest comply to
     * the Sea of Stuff standard and are not malformed.
     * All required key-value pairs must be set in the manifest, for the latter
     * to be valid.
     *
     * @return true if the manifest is valid.
     */
    boolean isValid();

    /**
     * Get the type of manifest as a string.
     *
     * @return type of manifest as a string.
     */
    ManifestType getManifestType();

}
