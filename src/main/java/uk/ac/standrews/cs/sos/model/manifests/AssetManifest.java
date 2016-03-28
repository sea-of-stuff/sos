package uk.ac.standrews.cs.sos.model.manifests;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.codec.binary.Base64;
import uk.ac.standrews.cs.sos.exceptions.GuidGenerationException;
import uk.ac.standrews.cs.sos.exceptions.identity.EncryptionException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.interfaces.Identity;
import uk.ac.standrews.cs.utils.GUID;

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
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AssetManifest extends SignedManifest {

    final private GUID version;
    final private GUID invariant;
    final private Collection<GUID> prevs;
    final private Collection<GUID> metadata;

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
    public AssetManifest(GUID invariant, GUID content,
                            Collection<GUID> prevs, Collection<GUID> metadata,
                            Identity identity)
            throws ManifestNotMadeException {
        super(identity, ManifestConstants.ASSET);

        if (invariant != null) {
            this.invariant = invariant;
        } else {
            this.invariant = makeInvariant();
        }

        this.contentGUID = content;
        this.prevs = prevs;
        this.metadata = metadata;
        this.version = makeVersionGUID();

        if (identity != null) {
            this.signature = makeSignature();
        }
    }

    /**
     *
     * @param invariant
     * @param version
     * @param content
     * @param prevs
     * @param metadata
     * @param signature
     */
    public AssetManifest(GUID invariant, GUID version, GUID content,
                            Collection<GUID> prevs, Collection<GUID> metadata,
                            String signature) {
        super(null, ManifestConstants.ASSET);
        this.invariant = invariant;
        this.version = version;
        this.contentGUID = content;
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

    @Override
    public GUID getContentGUID() {
        return contentGUID;
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
                isGUIDValid(contentGUID);
    }

    @Override
    public JsonObject toJSON() {
        JsonObject obj = manifestToHashInJSON();
        obj.addProperty(ManifestConstants.KEY_VERSION, version.toString());

        if (signature != null && !signature.isEmpty()) {
            obj.addProperty(ManifestConstants.KEY_SIGNATURE, signature);
        }

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
        return GUID.generateRandomGUID();
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
        obj.addProperty(ManifestConstants.KEY_CONTENT_GUID, contentGUID.toString());

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
