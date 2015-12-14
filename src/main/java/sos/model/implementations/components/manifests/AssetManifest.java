package sos.model.implementations.components.manifests;

import configurations.identity.IdentityConfiguration;
import org.json.JSONArray;
import org.json.JSONObject;
import sos.exceptions.EncryptionException;
import sos.exceptions.GuidGenerationException;
import sos.exceptions.ManifestNotMadeException;
import sos.model.implementations.utils.Content;
import sos.model.implementations.utils.GUID;
import sos.model.interfaces.components.Metadata;
import sos.model.interfaces.identity.Identity;

import java.util.Collection;

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
 * Manifest - GUID <br>
 * Incarnation - GUID <br>
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
// XXX - write unit tests
public class AssetManifest extends SignedManifest {

    private GUID incarnation; // XXX - generate only the first time, then pass it in the constructor.
    private Content content;
    private Collection<GUID> prevs;
    private GUID metadata; // XXX - consider having multiple of them.

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
     * Creates an AssetManifest given a content, an identity and the GUIDs of the previous
     * asset's manifest.
     *
     * @param prevs
     * @param content
     * @param identity
     * @throws ManifestNotMadeException
     */
    protected AssetManifest(Collection<GUID> prevs, Content content, Identity identity)
            throws ManifestNotMadeException {
        this(content, identity);
        this.prevs = prevs;

        make();
    }

    /**
     * Creates an AssetManifest given a content, an identity, the GUIDs of the previous
     * asset's manifest, and the GUID of metadata associated to the asset.
     *
     * @param prevs
     * @param content
     * @param metadata
     * @param identity
     * @throws ManifestNotMadeException
     */
    protected AssetManifest(Collection<GUID> prevs, Content content, GUID metadata, Identity identity)
            throws ManifestNotMadeException {
        this(prevs, content, identity);
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
     * XXX - check plural
     * Get the previous asset's GUID of a given asset.
     *
     * @return the previous asset's GUID
     *         Null if the asset does not have a previous one.
     */
    public Collection<GUID> getPreviousManifests() {
        return prevs;
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
        obj.put(ManifestConstants.KEY_PREVIOUS_GUID, getPrevsInJSON());
        obj.put(ManifestConstants.KEY_METADATA_GUID, metadata);

        return obj;
    }

    @Override
    protected JSONObject generateManifestToHash() {
        JSONObject obj = new JSONObject();

        obj.put(ManifestConstants.KEY_TYPE, this.getManifestType());
        obj.put(ManifestConstants.KEY_SIGNATURE, getSignature());
        obj.put(ManifestConstants.KEY_CONTENT_GUID, content.toJSON());
        obj.put(ManifestConstants.KEY_PREVIOUS_GUID, getPrevsInJSON());
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
        obj.put(ManifestConstants.KEY_PREVIOUS_GUID, getPrevsInJSON());
        obj.put(ManifestConstants.KEY_METADATA_GUID, metadata);

        byte[] signatureBytes = this.identity.encrypt(obj.toString());
        signature = IdentityConfiguration.bytesToHex(signatureBytes);
    }

    private JSONArray getPrevsInJSON() {
        JSONArray arr = new JSONArray();
        for (GUID prev : prevs) {
            arr.put(prev.toString());
        }

        return arr;
    }
}
