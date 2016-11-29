package uk.ac.standrews.cs.sos.model.manifests;

import com.fasterxml.jackson.core.JsonProcessingException;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.identity.DecryptionException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestVerificationException;
import uk.ac.standrews.cs.sos.interfaces.identity.Identity;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;
import uk.ac.standrews.cs.sos.utils.JSONHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The BasicManifest defines the base implementation for all other manifests.
 * This class implements some of the methods that can be generalised across all
 * other types of manifests. Manifests extending the BasicManifest MUST provide
 * implementations for the abstract methods defined in this class.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class BasicManifest implements Manifest {

    private static final Pattern HEX_PATTERN = Pattern.compile("^[0-9a-fA-F]+$");

    protected IGUID contentGUID;
    private final ManifestType manifestType;

    /**
     * Constructor for a BasicManifest.
     * Initialise the type of manifest.
     *
     * @param manifestType
     */
    protected BasicManifest(ManifestType manifestType) {
        this.manifestType = manifestType;
    }

    /**
     * Gets the type of this manifest.
     *
     * @return the type of this manifest.
     */
    @Override
    public ManifestType getManifestType() {
        return this.manifestType;
    }

    /**
     * Gets the GUID of the content referenced by this manifest.
     *
     * @return guid of the content.
     */
    @Override
    public IGUID getContentGUID() {
        return this.contentGUID;
    }

    /**
     * Verifies this manifest's GUID against its content.
     *
     * @return true if the GUID of the manifest matches the content.
     * @throws GUIDGenerationException if the manifest's GUID could not be generated.
     * @throws DecryptionException
     */
    @Override
    public abstract boolean verify(Identity identity) throws ManifestVerificationException;

    /**
     * Checks whether this manifest contains valid key-value entries.
     *
     * @return true if the manifest is valid.
     */
    @Override
    public boolean isValid() {
        return hasManifestType();
    }

    /**
     * Checks if the given GUID contains valid hex characters.
     *
     * @param guid to validated.
     * @return true if the guid is valid.
     */
    protected boolean isGUIDValid(IGUID guid) {
        if (guid == null || guid.isInvalid())
            return false;

        Matcher matcher = HEX_PATTERN.matcher(guid.toString());
        return matcher.matches();
    }

    private boolean hasManifestType() {
        return manifestType != null;
    }

    @Override
    public String toString() {
        try {
            return JSONHelper.JsonObjMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return null;
    }

}
