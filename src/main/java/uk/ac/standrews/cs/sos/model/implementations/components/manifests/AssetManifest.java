package uk.ac.standrews.cs.sos.model.implementations.components.manifests;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.codec.binary.Base64;
import uk.ac.standrews.cs.sos.exceptions.GuidGenerationException;
import uk.ac.standrews.cs.sos.exceptions.identity.EncryptionException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.model.implementations.utils.Content;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUID;
import uk.ac.standrews.cs.sos.model.interfaces.identity.Identity;

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
     * Creates an AssetManifest given a content, an identity, the GUIDs of the previous
     * asset's manifest, and the GUID of metadata associated to the asset.
     *
     * @param invariant - if null it will be generated
     * @param content
     * @param prevs
     * @param metadata
     * @param identity
     * @throws ManifestNotMadeException
     */
    public AssetManifest(GUID invariant, Content content,
                            Collection<GUID> prevs, Collection<GUID> metadata,
                            Identity identity)
            throws ManifestNotMadeException {
        super(identity, ManifestConstants.ASSET);

        if (invariant != null) {
            this.invariant = invariant;
        } else {
            this.invariant = makeInvariant();
        }

        this.content = content;
        this.prevs = prevs;
        this.metadata = metadata;
        this.version = makeVersionGUID();

        if (identity != null) {
            this.signature = makeSignature();
        }
    }

    public AssetManifest(GUID invariant, GUID version, Content content,
                            Collection<GUID> prevs, Collection<GUID> metadata,
                            String signature) {
        super(null, ManifestConstants.ASSET);
        this.invariant = invariant;
        this.version = version;
        this.content = content;
        this.prevs = prevs;
        this.metadata = metadata;
        this.signature = signature;
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

    @Override
    public GUID getContentGUID() {
        return content.getGUID();
    }

    @Override
    public void setContentGUID(GUID guid) {
        if (content == null || content.getGUID() == null)
            content = new Content(guid);
    }

    /**
     * Get the metadata's GUID associated with an asset's manifest
     *
     * @return Metadata's GUID associated with the asset.
     *         Null if there is no metadata associated with the asset.
     *
     */
    public Collection<GUID> getMetadata() {
        return metadata;
    }

    @Override
    public boolean isValid() {
        return super.isValid() &&
                content != null &&
                isGUIDValid(content.getGUID());
    }

    @Override
    public JsonObject toJSON() {
        JsonObject obj = manifestToHashInJSON();
        obj.addProperty(ManifestConstants.KEY_VERSION, version.toString());
        obj.addProperty(ManifestConstants.KEY_SIGNATURE, getSignature());

        return obj;
    }

    @Override
    protected String generateSignature(String toSign) throws EncryptionException {
        byte[] signatureBytes = this.identity.sign(toSign);
        byte[] encodedBytes = Base64.encodeBase64(signatureBytes);
        return new String(encodedBytes);
    }

    private JsonObject manifestToHashInJSON() {
        JsonObject obj = super.toJSON();

        obj.addProperty(ManifestConstants.KEY_INVARIANT, invariant.toString());
        addVersionElemenetsToJSON(obj);

        return obj;
    }

    private GUID makeInvariant() throws ManifestNotMadeException {
        GUID guid;
        try {
            guid = generateInvariant();
        } catch (GuidGenerationException e) {
            throw new ManifestNotMadeException();
        }

        return guid;
    }

    private GUID generateInvariant() throws GuidGenerationException {
        return GUID.generateGUID(Double.toString(Math.random()));
    }

    private GUID makeVersionGUID() throws ManifestNotMadeException {
        GUID guid;
        try {
            guid = GUID.generateGUID(manifestToHashInJSON().toString());
        } catch (GuidGenerationException e) {
            throw new ManifestNotMadeException();
        }
        return guid;
    }

    @Override
    protected String getManifestToSign() {
        JsonObject obj = new JsonObject();

        obj.addProperty(ManifestConstants.KEY_TYPE, this.getManifestType());
        addVersionElemenetsToJSON(obj);

        Gson gson = new Gson();
        return gson.toJson(obj);
    }


    private void addVersionElemenetsToJSON(JsonObject obj) {
        obj.add(ManifestConstants.KEY_CONTENTS, content.toJSON());

        if (prevs != null && !prevs.isEmpty())
            obj.add(ManifestConstants.KEY_PREVIOUS_GUID, getCollectionInJSON(prevs));

        if (metadata != null && !metadata.isEmpty())
            obj.add(ManifestConstants.KEY_METADATA_GUID, getCollectionInJSON(metadata));
    }

    private JsonArray getCollectionInJSON(Collection<?> collection) {
        JsonArray arr = new JsonArray();
        for (Object obj : collection) {
            arr.add(obj.toString());
        }
        return arr;
    }

}
