package uk.ac.standrews.cs.sos.model.manifests;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.codec.binary.Base64;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.identity.EncryptionException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.interfaces.identity.Identity;
import uk.ac.standrews.cs.sos.interfaces.manifests.Version;

import java.util.Collection;

/**
 * An Version is identified by an asset GUID (invariant).
 * Unlike other GUIDs, the asset's GUID is not derived from contents.
 * Instead an asset GUID is good for all time irrespective of the asset's contents.
 *
 * Assets do not contain data, thus they exist only in the manifest space.
 *
 * Assets refer to unions (atoms or compounds) - and they are used to assert commonality over a
 * history of changes of unions. <br>
 *
 *
 * This class defines the manifest describing an Version, which takes the
 * following form:
 * <p>
 * Version - GUID <br>
 * Invariant - GUID <br>
 * ManifestType - VERSION <br>
 * Signature - ? <br>
 * Previous Assets - GUID <br>
 * Content - GUID <br>
 * Metadata - GUID
 * </p>
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class VersionManifest extends SignedManifest implements Version {

    final private IGUID version;
    final private IGUID invariant;
    final private Collection<IGUID> prevs;
    final private Collection<IGUID> metadata;

    /**
     * Creates an VersionManifest given a content, an identity, the GUIDs of the previous
     * asset's manifest, and the GUID of metadata associated to the asset.
     *
     * @param invariant - if null it will be generated
     * @param content
     * @param prevs
     * @param metadata
     * @param identity
     * @throws ManifestNotMadeException
     */
    public VersionManifest(IGUID invariant, IGUID content,
                           Collection<IGUID> prevs, Collection<IGUID> metadata,
                           Identity identity)
            throws ManifestNotMadeException {
        super(identity, ManifestConstants.VERSION);

        if (invariant != null) {
            this.invariant = invariant;
        } else {
            this.invariant = makeInvariant();
        }

        this.contentGUID = content;
        this.prevs = prevs;
        this.metadata = metadata;
        try {
            this.version = makeVersionGUID();
        } catch (GUIDGenerationException e) {
            throw new ManifestNotMadeException();
        }

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
    public VersionManifest(IGUID invariant, IGUID version, IGUID content,
                           Collection<IGUID> prevs, Collection<IGUID> metadata,
                           String signature) {
        super(null, ManifestConstants.VERSION);
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
    @Override
    public IGUID getVersionGUID() {
        return version;
    }

    /**
     * Gets the GUID of this asset (invariant).
     *
     * @return the GUID of this asset
     */
    @Override
    public IGUID getInvariantGUID() {
        return invariant;
    }

    /**
     * Get the GUIDs of the versions from which this version is derived
     *
     * @return the previous versions
     *         Null if the asset does not have a previous version.
     */
    @Override
    public Collection<IGUID> getPreviousManifests() {
        return prevs;
    }

    @Override
    public IGUID getContentGUID() {
        return contentGUID;
    }

    /**
     * Get the metadata's GUID associated with an asset's manifest
     *
     * @return Metadata's GUID associated with the asset.
     *         Null if there is no metadata associated with the asset.
     *
     */
    @Override
    public Collection<IGUID> getMetadata() {
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

    private IGUID makeInvariant() throws ManifestNotMadeException {
        IGUID guid;
        try {
            guid = generateInvariant();
        } catch (GUIDGenerationException e) {
            throw new ManifestNotMadeException();
        }

        return guid;
    }

    private IGUID generateInvariant() throws GUIDGenerationException {
        return GUIDFactory.generateRandomGUID();
    }

    private IGUID makeVersionGUID() throws GUIDGenerationException {
        return GUIDFactory.generateGUID(manifestToHashInJSON().toString());
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
