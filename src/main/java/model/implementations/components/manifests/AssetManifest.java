package model.implementations.components.manifests;

import configurations.identity.IdentityConfiguration;
import model.exceptions.EncryptionException;
import model.exceptions.GuidGenerationException;
import model.exceptions.ManifestNotMadeException;
import model.implementations.utils.Content;
import model.implementations.utils.GUID;
import model.interfaces.components.Metadata;
import model.interfaces.identity.Identity;
import org.json.JSONObject;

/**
 * An Asset is identified by an asset GUID (incarnation).
 * Unlike other GUIDs, the asset's GUID is not derived from contents.
 * Instead an asset GUID is good for all time irrespective of the asset's contents.
 *
 * Assets do not contain data, thus they exist only in the manifest space.
 *
 * Assets refer to unions (atoms or compounds) - and they are used to assert commonality over a
 * history of changes of unions. <br>
 *
 * TODO - an asset also refers to metadata
 *
 * This class defines the manifest describing an Asset, which takes the
 * following form:
 * <p>
 * Manifest's GUID - GUID <br>
 * Asset's GUID (incarnation) - GUID <br>
 * ManifestType - ASSET <br>
 * Signature - ? <br>
 * Previous Asset - GUID <br>
 * Content (union) - GUID <br>
 * Metadata - GUID
 * </p>
 *
 * @see GUID
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AssetManifest extends SignedManifest {

    private GUID incarnation; // XXX - generate only the first time, then pass it in the constructor.
    private Content content;
    private GUID previous; // XXX - consider having multiple of them.
    private GUID metadata;

    /**
     * Creates an AssetManifest given a content and an identity.
     *
     * @param content
     * @param identity
     * @throws ManifestNotMadeException
     */
    protected AssetManifest(Content content, Identity identity)
            throws ManifestNotMadeException {
        super(identity, ManifestConstants.ASSET);
        this.content = content;

        make();
    }

    /**
     * Creates an AssetManifest given a content, an identity and the GUID of a previous
     * asset's manifest.
     *
     * @param previous
     * @param content
     * @param identity
     * @throws ManifestNotMadeException
     */
    protected AssetManifest(GUID previous, Content content, Identity identity)
            throws ManifestNotMadeException {
        this(content, identity);
        this.previous = previous;

        make();
    }

    /**
     * Creates an AssetManifest given a content, an identity, the GUID of a previous
     * asset's manifest, and the GUID of metadata associated to the asset.
     *
     * @param previous
     * @param content
     * @param metadata
     * @param identity
     * @throws ManifestNotMadeException
     */
    protected AssetManifest(GUID previous, Content content, GUID metadata, Identity identity)
            throws ManifestNotMadeException {
        this(previous, content, identity);
        this.metadata = metadata;

        make();
    }

    /**
     * Creates an AssetManifest given a content, an identity, and the GUID of metadata associated to the asset.
     *
     * @param content
     * @param metadata
     * @param identity
     * @throws ManifestNotMadeException
     */
    protected AssetManifest(Content content, GUID metadata, Identity identity)
            throws ManifestNotMadeException {
        this(content, identity);
        this.metadata = metadata;

        make();
    }

    /**
     * Gets the GUID of this asset (incarnation).
     *
     * @return the GUID of this asset
     */
    public GUID getAssetGUID() {
        return incarnation;
    }

    /**
     * Get the previous asset's GUID of a given asset.
     *
     * @return the previous asset's GUID
     *         Null if the asset does not have a previous one.
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
     * Get the metadata's GUID associated with an asset's manifest
     *
     * @return Metadata's GUID associated with the asset.
     *         Null if there is no metadata associated with the asset.
     *
     * @see Metadata
     */
    public GUID getMetadataGUID() {
        return metadata;
    }

    @Override
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
    protected JSONObject generateManifestToHash() {
        JSONObject obj = new JSONObject();

        obj.put(ManifestConstants.KEY_TYPE, this.getManifestType());
        obj.put(ManifestConstants.KEY_SIGNATURE, getSignature());
        obj.put(ManifestConstants.KEY_CONTENT_GUID, content.toJSON());
        obj.put(ManifestConstants.KEY_PREVIOUS_GUID, previous);
        obj.put(ManifestConstants.KEY_METADATA_GUID, metadata);

        return obj;
    }

    private void make() throws ManifestNotMadeException {

        incarnation = null; // FIXME - generate incarnation GUID

        try {
            generateSignature();
        } catch (Exception e) {
            throw new ManifestNotMadeException();
        }

        try {
            generateManifestGUID();
        } catch (GuidGenerationException e) {
            throw new ManifestNotMadeException();
        }
    }

    @Override
    protected void generateSignature() throws EncryptionException {
        JSONObject obj = new JSONObject();

        obj.put(ManifestConstants.KEY_TYPE, this.getManifestType());
        obj.put(ManifestConstants.KEY_CONTENT_GUID, content.toJSON());
        obj.put(ManifestConstants.KEY_PREVIOUS_GUID, previous);
        obj.put(ManifestConstants.KEY_METADATA_GUID, metadata);

        byte[] signatureBytes = this.identity.encrypt(obj.toString());
        signature = IdentityConfiguration.bytesToHex(signatureBytes);
    }
}
