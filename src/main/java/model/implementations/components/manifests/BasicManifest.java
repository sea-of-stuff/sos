package model.implementations.components.manifests;

import model.exceptions.GuidGenerationException;
import model.implementations.utils.GUID;
import model.interfaces.SeaOfStuff;
import model.interfaces.components.Manifest;
import org.json.JSONObject;

/**
 * The BasicManifest defines the base implementation for all other manifests.
 * This class implements some of the methods that can be generalised across all
 * other types of manifests. Manifests extending the BasicManifest MUST provide
 * implementations for the abstract methods defined in this class.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class BasicManifest implements Manifest {

    private GUID guid;
    private final String manifestType;

    /**
     * Constructor for a BasicManifest.
     * Initialise the type of manifest and the timestamp for this manifest.
     *
     * @param manifestType
     */
    protected BasicManifest(String manifestType) {
        this.manifestType = manifestType;
    }

    /**
     * Generate the GUID of this manifest.
     *
     * @return the GUID of this manifest.
     */
    protected abstract GUID generateGUID() throws GuidGenerationException;

    /**
     * Verify this manifest's GUID against its content.
     *
     * {@link SeaOfStuff#verifyManifest(Manifest)}
     *
     * @return true if the GUID of the manifest matches the content.
     */
    @Override
    public abstract boolean verify();

    /**
     * Checks whether this manifest contains valid key-value entries.
     *
     * @return true if the manifest is valid.
     */
    @Override
    public boolean isValid() {
        // TODO - test for guid and manifest type
        return false;
    }

    /**
     * Transform the content of this manifest to a JSON representation.
     *
     * @return JSON representation of this manifest.
     */
    @Override
    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();

        obj.put(ManifestConstants.KEY_MANIFEST_GUID, guid);
        obj.put(ManifestConstants.KEY_TYPE, manifestType);

        return obj;
    }

    @Override
    public GUID getGUID() {
        return this.guid;
    }

    @Override
    public String getManifestType() {
        return this.manifestType;
    }

}
