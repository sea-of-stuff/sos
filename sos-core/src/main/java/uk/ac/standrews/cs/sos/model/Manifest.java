package uk.ac.standrews.cs.sos.model;

import uk.ac.standrews.cs.guid.ALGORITHM;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.crypto.SignatureException;

import java.io.IOException;
import java.io.InputStream;

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
     * Verify this manifest's GUID against its content.
     *
     * @param role used to sign this manifest
     * @return true if the GUID of the manifest matches the content.
     * @throws SignatureException signature of the manifest could not be verified
     */
    boolean verifySignature(Role role) throws SignatureException;

    /**
     * Verifies that the GUID of this manifest matches its contents
     *
     * This method is different for the Atom Manifest, where we need to get the content from all the available locations
     *
     * @return true if the guid of the manifest matches its contents
     */
    default boolean verifyIntegrity() {

        try (InputStream contentToHash = contentToHash()) {
            if (!(contentToHash != null && guid().equals(GUIDFactory.generateGUID(ALGORITHM.SHA256, contentToHash)))) {
                return false;
            }
        } catch (IOException | GUIDGenerationException e) {
            return false;
        }

        return true;
    }

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
    ManifestType getType();

    /**
     * GUID representing this manifest
     *
     * @return the guid for this manifest
     */
    IGUID guid();

    /**
     *
     * @return the content used to generate the GUID for this manifest
     */
    InputStream contentToHash() throws IOException;

}
