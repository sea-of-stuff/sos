package uk.ac.standrews.cs.sos.model.manifests;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.identity.EncryptionException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.interfaces.model.ManifestType;
import uk.ac.standrews.cs.sos.interfaces.model.Role;
import uk.ac.standrews.cs.sos.interfaces.model.Version;
import uk.ac.standrews.cs.sos.json.VersionManifestDeserializer;
import uk.ac.standrews.cs.sos.json.VersionManifestSerializer;

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
 * ManifestType - VERSION <br>
 * Signature - ? <br>
 * Previous Assets - GUID <br>
 * Content - GUID <br>
 * Metadata - GUID
 * </p>
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@JsonSerialize(using = VersionManifestSerializer.class)
@JsonDeserialize(using = VersionManifestDeserializer.class)
public class VersionManifest extends SignedManifest implements Version {

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
     * @param role
     * @throws ManifestNotMadeException
     */
    public VersionManifest(IGUID invariant, IGUID content,
                           Set<IGUID> prevs, IGUID metadata,
                           Role role)
            throws ManifestNotMadeException {
        super(role, ManifestType.VERSION);

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

        if (role != null) {
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
                           Set<IGUID> prevs, IGUID metadata,
                           String signature) {
        super(null, ManifestType.VERSION);
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
        return this.role.sign(toSign);
    }

    private String manifestToHash() {
        String toHash = getType() +
                "I" + getInvariantGUID() + // <-- this will stop a user from forking an asset. It means that they cannot generate the same version guid under a different asset.
                "C" + getContentGUID();

        toHash += getPreviousToHashOrSign();
        toHash += getMetadataToHashOrSign();

        return toHash;
    }

    @Override
    protected String getManifestToSign() {
        String toSign = getType() +
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
