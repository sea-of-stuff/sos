package sos.model.implementations.components.manifests;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import configurations.identity.IdentityConfiguration;
import sos.exceptions.EncryptionException;
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
 * Version - GUID <br>
 * Invariant - GUID <br>
 * ManifestType - ASSET <br>
 * Signature - ? <br>
 * Previous Assets - GUID <br>
 * Content - GUID <br>
 * Metadata - GUID
 * </p>
 *
 * @see GUID
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AssetManifest extends SignedManifest {

    private GUID version; // TODO - manifest GUID
    private GUID invariant; // XXX - generate only the first time, then pass it in the constructor.
    private Content content;
    private Collection<GUID> prevs;
    private Collection<GUID> metadata;

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
    protected AssetManifest(Collection<GUID> prevs, Content content, Collection<GUID> metadata, Identity identity)
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
    protected AssetManifest(Content content, Collection<GUID> metadata, Identity identity)
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
        return invariant;
    }

    /**
     * XXX - check plural
     * Get the GUIDs of the versions from which this version is derived
     *
     * @return the previous versions
     *         Null if the asset does not have a previous version.
     */
    public Collection<GUID> getPreviousManifests() {
        return prevs;
    }

    /**
     * Get the content of this asset.
     *
     * @return the content of this asset.
     */
    public Content getContent() {
        return content;
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
    public Collection<GUID> getMetadataGUID() {
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
    public JsonObject toJSON() {
        JsonObject obj = super.toJSON();

        obj.addProperty(ManifestConstants.KEY_SIGNATURE, getSignature());
        obj.add(ManifestConstants.KEY_CONTENT_GUID, content.toJSON());
        obj.add(ManifestConstants.KEY_PREVIOUS_GUID, getCollectionInJSON(prevs));
        obj.add(ManifestConstants.KEY_METADATA_GUID, getCollectionInJSON(metadata));

        return obj;
    }

    private void make() throws ManifestNotMadeException {

        invariant = null; // FIXME - generate invariant GUID

        try {
            generateSignature();
        } catch (Exception e) {
            throw new ManifestNotMadeException();
        }
    }

    @Override
    protected void generateSignature() throws EncryptionException {
        JsonObject obj = new JsonObject();

        obj.addProperty(ManifestConstants.KEY_TYPE, this.getManifestType());
        obj.add(ManifestConstants.KEY_CONTENT_GUID, content.toJSON());
        obj.add(ManifestConstants.KEY_PREVIOUS_GUID, getCollectionInJSON(prevs));
        obj.add(ManifestConstants.KEY_METADATA_GUID, getCollectionInJSON(metadata));

        Gson gson = new Gson();
        byte[] signatureBytes = this.identity.encrypt(gson.toJson(obj));
        signature = IdentityConfiguration.bytesToHex(signatureBytes);
    }

    private JsonArray getCollectionInJSON(Collection<?> collection) {
        JsonArray arr = new JsonArray();
        for (Object obj : collection) {
            arr.add(obj.toString());
        }

        return arr;
    }

}
