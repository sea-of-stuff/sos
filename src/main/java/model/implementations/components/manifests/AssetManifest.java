package model.implementations.components.manifests;

import model.exceptions.GuidGenerationException;
import model.exceptions.ManifestNotMadeException;
import model.implementations.utils.Content;
import model.implementations.utils.GUID;
import model.interfaces.components.Metadata;
import model.interfaces.identity.Identity;
import org.json.JSONObject;

/**
 * An Asset is identified by an asset GUID. Unlike other GUIDs they are not
 * derived from contents. Instead an asset GUID is good for all time
 * irrespective of the asset's contents.
 * Assets do not contain data, thus they exist only in the manifest space.
 * Assets refer to unions - and they are used to assert commonality over a
 * history of changes of unions.
 * <br>
 * This class defines the manifest describing an Asset, which takes the
 * following form:
 * <p>
 * Manifest - GUID <br>
 * Asset - GUID <br>
 * ManifestType - ASSET <br>
 * Signature - ? <br>
 * Previous Asset - GUID <br>
 * Content - GUID <br>
 * Metadata - GUID
 * </p>
 *
 * @see GUID
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AssetManifest extends SignedManifest {

    private GUID incarnation;
    private Content content;
    private GUID previous;
    private GUID metadata;

    protected AssetManifest(Content content, Identity identity)
            throws ManifestNotMadeException {
        super(identity, ManifestConstants.ASSET);
        this.content = content;

        make();
    }

    protected AssetManifest(GUID previous, Content content, Identity identity)
            throws ManifestNotMadeException {
        this(content, identity);
        this.previous = previous;

        make();
    }

    protected AssetManifest(GUID previous, Content content, GUID metadata, Identity identity)
            throws ManifestNotMadeException {
        this(previous, content, identity);
        this.metadata = metadata;

        make();
    }

    protected AssetManifest(Content content, GUID metadata, Identity identity)
            throws ManifestNotMadeException {
        this(content, identity);
        this.metadata = metadata;

        make();
    }

    private void make() throws ManifestNotMadeException {

        try {
            generateSignature(null);
        } catch (Exception e) {
            // TODO throw new ManifestNotMadeException();
        }

        try {
            generateManifestGUID();
        } catch (GuidGenerationException e) {
            throw new ManifestNotMadeException();
        }
    }

    /**
     * TODO - Incarnation
     *
     * @return the GUID of this asset
     *
     * @see GUID
     */
    public GUID getAssetGUID() {
        return incarnation;
    }

    /**
     * Get the previous asset's GUID of a given asset.
     *
     * @return the previous asset's GUID
     *         Null if the asset does not have a previous one.
     *
     */
    public GUID getPreviousManifest() {
        return previous;
    }

    /**
     * Get the content's GUID of this asset.
     *
     * @return GUID of the content of this asset.
     */
    public GUID getContentGUID() {
        return content.getGUID();
    }

    /**
     * Get the metadata associated with an asset's manifest
     *
     * @return Metadata associated with the asset.
     *         Null if there is not metadata associated with the asset.
     *
     * @see Metadata
     */
    public GUID getMetadataGUID() {
        return metadata;
    }

    public boolean verify() {
        return false;
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject obj = super.toJSON();

        obj.put(ManifestConstants.KEY_SIGNATURE, getSignature());
        obj.put(ManifestConstants.KEY_CONTENT_GUID, content.toJSON());
        obj.put(ManifestConstants.KEY_PREVIOUS_GUID, previous);
        obj.put(ManifestConstants.KEY_METADATA_GUID, metadata);

        return obj;
    }

    @Override
    protected String generateManifestToHash() {
        JSONObject obj = new JSONObject();

        obj.put(ManifestConstants.KEY_TYPE, this.getManifestType());
        obj.put(ManifestConstants.KEY_SIGNATURE, getSignature());
        obj.put(ManifestConstants.KEY_CONTENT_GUID, content.toJSON());
        obj.put(ManifestConstants.KEY_PREVIOUS_GUID, previous);
        obj.put(ManifestConstants.KEY_METADATA_GUID, metadata);

        return obj.toString();
    }

}
