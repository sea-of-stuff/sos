package sos.model.implementations.components.manifests;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import configurations.identity.IdentityConfiguration;
import sos.exceptions.EncryptionException;
import sos.exceptions.GuidGenerationException;
import sos.exceptions.ManifestNotMadeException;
import sos.model.implementations.utils.Content;
import sos.model.implementations.utils.GUID;
import sos.model.interfaces.components.Metadata;
import sos.model.interfaces.identity.Identity;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Collection;

/**
 * An Asset is identified by an asset GUID (invariant).
 * Unlike other GUIDs, the asset's GUID is not derived from contents.
 * Instead an asset GUID is good for all time irrespective of the asset's contents.
 *
 * Assets do not contain data, thus they exist only in the manifest space.
 *
 * Assets refer to unions (atoms or compounds) - and they are used to assert commonality over a
 * history of changes of unions. <br>
 *
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
 * TODO - class needs some polishing
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AssetManifest extends SignedManifest {

    private GUID version;
    private GUID invariant;
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

        try {
            generateInvariant();
        } catch (GuidGenerationException e) {
            throw new ManifestNotMadeException();
        }
        make();
    }

    /**
     * Creates an AssetManifest given a content, an identity and the GUIDs of the previous
     * asset's manifest.
     *
     * @param invariant
     * @param content
     * @param prevs
     * @param identity
     * @throws ManifestNotMadeException
     */
    protected AssetManifest(GUID invariant, Content content,
                            Collection<GUID> prevs, Identity identity)
            throws ManifestNotMadeException {
        super(identity, ManifestConstants.ASSET);
        this.content = content;
        this.invariant = invariant;
        this.prevs = prevs;

        make();
    }

    /**
     * Creates an AssetManifest given a content, an identity, the GUIDs of the previous
     * asset's manifest, and the GUID of metadata associated to the asset.
     *
     * @param invariant
     * @param content
     * @param prevs
     * @param metadata
     * @param identity
     * @throws ManifestNotMadeException
     */
    protected AssetManifest(GUID invariant, Content content,
                            Collection<GUID> prevs, Collection<GUID> metadata,
                            Identity identity)
            throws ManifestNotMadeException {
        super(identity, ManifestConstants.ASSET);
        this.content = content;
        this.invariant = invariant;
        this.prevs = prevs;
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
        super(identity, ManifestConstants.ASSET);
        this.content = content;
        this.metadata = metadata;

        try {
            generateInvariant();
        } catch (GuidGenerationException e) {
            throw new ManifestNotMadeException();
        }
        make();
    }

    /**
     * Gets the GUID associated to this version of the asset.
     *
     * @return version GUID of this asset manifest.
     */
    public GUID getVersionGUID() {
        return version;
    }

    /**
     * Gets the GUID of this asset (invariant).
     *
     * @return the GUID of this asset
     */
    public GUID getInvariantGUID() {
        return invariant;
    }

    /**
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

    public void setContentGUID() {
        throw new NotImplementedException();
    }

    /**
     * Get the metadata's GUID associated with an asset's manifest
     *
     * @return Metadata's GUID associated with the asset.
     *         Null if there is no metadata associated with the asset.
     *
     * @see Metadata
     */
    public Collection<GUID> getMetadata() {
        return metadata;
    }

    @Override
    public boolean verify() {
        throw new NotImplementedException();
    }

    @Override
    public boolean isValid() {
        throw new NotImplementedException();
    }

    @Override
    public JsonObject toJSON() {
        JsonObject obj = manifestToHashInJSON();
        obj.addProperty(ManifestConstants.KEY_VERSION, version.toString());
        obj.addProperty(ManifestConstants.KEY_SIGNATURE, getSignature());

        return obj;
    }

    @Override
    public String toString() {
        return toJSON().toString();
    }

    @Override
    protected String generateSignature() throws EncryptionException {
        JsonObject obj = new JsonObject();

        obj.addProperty(ManifestConstants.KEY_TYPE, this.getManifestType());
        obj.add(ManifestConstants.KEY_CONTENTS, content.toJSON());

        if (prevs != null)
            obj.add(ManifestConstants.KEY_PREVIOUS_GUID, getCollectionInJSON(prevs));

        if (metadata != null)
            obj.add(ManifestConstants.KEY_METADATA_GUID, getCollectionInJSON(metadata));

        Gson gson = new Gson();
        byte[] signatureBytes = this.identity.encrypt(gson.toJson(obj));
        return IdentityConfiguration.bytesToHex(signatureBytes);
    }

    private JsonObject manifestToHashInJSON() {
        JsonObject obj = super.toJSON();

        obj.addProperty(ManifestConstants.KEY_INVARIANT, invariant.toString());
        obj.add(ManifestConstants.KEY_CONTENTS, content.toJSON());

        if (prevs != null)
            obj.add(ManifestConstants.KEY_PREVIOUS_GUID, getCollectionInJSON(prevs));

        if (metadata != null)
            obj.add(ManifestConstants.KEY_METADATA_GUID, getCollectionInJSON(metadata));

        return obj;
    }

    private void generateInvariant() throws GuidGenerationException {
        // FIXME - generate invariant GUID - maybe based on identity
        invariant = generateGUID(Double.toString(Math.random()));
    }

    private void make() throws ManifestNotMadeException {

        try {
            version = generateGUID(manifestToHashInJSON().toString());
        } catch (GuidGenerationException e) {
            throw new ManifestNotMadeException();
        }

        try {
            signature = generateSignature();
        } catch (Exception e) {
            throw new ManifestNotMadeException();
        }
    }

    private JsonArray getCollectionInJSON(Collection<?> collection) {
        JsonArray arr = new JsonArray();
        for (Object obj : collection) {
            arr.add(obj.toString());
        }
        return arr;
    }

}
