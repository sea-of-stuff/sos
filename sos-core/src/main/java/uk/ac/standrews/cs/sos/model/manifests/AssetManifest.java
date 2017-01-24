package uk.ac.standrews.cs.sos.model.manifests;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.codec.binary.Base64;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.identity.EncryptionException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.interfaces.identity.Identity;
import uk.ac.standrews.cs.sos.interfaces.manifests.Asset;
import uk.ac.standrews.cs.sos.json.AssetManifestDeserializer;
import uk.ac.standrews.cs.sos.json.AssetManifestSerializer;

import java.util.Set;

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
 * ManifestType - ASSET <br>
 * Signature - ? <br>
 * Previous Assets - GUID <br>
 * Content - GUID <br>
 * Metadata - GUID
 * </p>
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@JsonSerialize(using = AssetManifestSerializer.class)
@JsonDeserialize(using = AssetManifestDeserializer.class)
public class AssetManifest extends SignedManifest implements Asset {

    final private IGUID version;
    final private IGUID invariant;
    final private Set<IGUID> prevs;
    final private IGUID metadata;

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
    public AssetManifest(IGUID invariant, IGUID content,
                         Set<IGUID> prevs, IGUID metadata,
                         Identity identity)
            throws ManifestNotMadeException {
        super(identity, ManifestType.ASSET);

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
            throw new ManifestNotMadeException("Failed to generate version GUID");
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
    public AssetManifest(IGUID invariant, IGUID version, IGUID content,
                         Set<IGUID> prevs, IGUID metadata,
                         String signature) {
        super(null, ManifestType.ASSET);
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
    public Set<IGUID> getPreviousVersions() {
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
    public IGUID getMetadata() {
        return metadata;
    }

    @Override
    public boolean isValid() {
        return super.isValid() &&
                isGUIDValid(contentGUID);
    }

    @Override
    public IGUID guid() {
        return getVersionGUID();
    }

    @Override
    protected String generateSignature(String toSign) throws EncryptionException {
        byte[] signatureBytes = this.identity.sign(toSign);
        byte[] encodedBytes = Base64.encodeBase64(signatureBytes);
        return new String(encodedBytes);
    }

    private String manifestToHash() {
        String toHash = getManifestType() +
                "I" + getInvariantGUID() +
                "C" + getContentGUID();

        toHash += getPreviousToHashOrSign();
        toHash += getMetadataToHashOrSign();

        return toHash;
    }

    @Override
    protected String getManifestToSign() {
        String toSign = getManifestType() +
                "C" + getContentGUID();

        toSign += getPreviousToHashOrSign();
        toSign += getMetadataToHashOrSign();

        return toSign;
    }

    private IGUID makeInvariant() {
        return GUIDFactory.generateRandomGUID();
    }

    private IGUID makeVersionGUID() throws GUIDGenerationException {
        return GUIDFactory.generateGUID(manifestToHash());
    }

    private String getPreviousToHashOrSign() {
        String retval = "";
        if (prevs != null && !prevs.isEmpty()) {
            retval = "P" + getCollectionToHashOrSign(prevs);
        }
        return retval;
    }

    private String getMetadataToHashOrSign() {
        String retval = "";
        if (metadata != null && !metadata.isInvalid()) {
            retval = "M" + metadata.toString();
        }
        return retval;
    }

    private String getCollectionToHashOrSign(Set<?> collection) {
        String toHash = "";
        for(Object obj:collection) {
            toHash += obj.toString() + ".";
        }
        return toHash;
    }

}
