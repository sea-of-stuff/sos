package uk.ac.standrews.cs.sos.model.manifests;

import com.google.gson.JsonObject;
import uk.ac.standrews.cs.sos.exceptions.identity.DecryptionException;
import uk.ac.standrews.cs.sos.exceptions.utils.GUIDGenerationException;
import uk.ac.standrews.cs.sos.interfaces.identity.Identity;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;
import uk.ac.standrews.cs.utils.IGUID;

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
    private final String manifestType;

    /**
     * Constructor for a BasicManifest.
     * Initialise the type of manifest.
     *
     * @param manifestType
     */
    protected BasicManifest(String manifestType) {
        this.manifestType = manifestType;
    }

    /**
     * Gets the type of this manifest.
     *
     * @return the type of this manifest.
     */
    @Override
    public String getManifestType() {
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
     * Transforms the content of this manifest to a JSON representation.
     *
     * @return JSON representation of this manifest.
     */
    @Override
    public JsonObject toJSON() {
        JsonObject obj = new JsonObject();
        obj.addProperty(ManifestConstants.KEY_TYPE, manifestType);

        return obj;
    }

    /**
     * Verifies this manifest's GUID against its content.
     *
     * @return true if the GUID of the manifest matches the content.
     * @throws GUIDGenerationException if the manifest's GUID could not be generated.
     * @throws DecryptionException
     */
    @Override
    public abstract boolean verify(Identity identity) throws GUIDGenerationException, DecryptionException;

    /**
     * Checks whether this manifest contains valid key-value entries.
     *
     * @return true if the manifest is valid.
     */
    @Override
    public boolean isValid() {
        return isManifestTypeValid();
    }

    /**
     * Checks if the given GUID contains valid hex characters.
     *
     * @param guid to validated.
     * @return true if the guid is valid.
     */
    protected boolean isGUIDValid(IGUID guid) {
        if (guid == null)
            return false;

        Matcher matcher = HEX_PATTERN.matcher(guid.toString());
        return matcher.matches();
    }

    private boolean isManifestTypeValid() {
        return manifestType != null && !manifestType.isEmpty();
    }

}
