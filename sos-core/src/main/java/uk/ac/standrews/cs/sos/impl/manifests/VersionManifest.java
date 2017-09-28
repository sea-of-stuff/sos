package uk.ac.standrews.cs.sos.impl.manifests;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.guid.impl.keys.InvalidID;
import uk.ac.standrews.cs.sos.exceptions.crypto.SignatureException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.json.VersionManifestDeserializer;
import uk.ac.standrews.cs.sos.json.VersionManifestSerializer;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.model.Version;
import uk.ac.standrews.cs.sos.utils.IO;

import java.io.IOException;
import java.io.InputStream;
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
     * @param signer
     * @throws ManifestNotMadeException
     */
    public VersionManifest(IGUID invariant, IGUID content, Set<IGUID> prevs, IGUID metadata, Role signer) throws ManifestNotMadeException {
        super(signer, ManifestType.VERSION);

        if (invariant != null) {
            this.invariant = invariant;
        } else {
            this.invariant = makeInvariant();
        }

        this.guid = content;
        this.prevs = prevs;
        this.metadata = metadata;
        this.version = makeVersionGUID();

        if (version.isInvalid()) {
            throw new ManifestNotMadeException("Failed to generate version GUID");
        }

        if (signer != null) {
            try {
                this.signature = makeSignature();
            } catch (SignatureException e) {
                throw new ManifestNotMadeException("Unable to sign the manifest");
            }
        }
    }

    /**
     *
     * @param invariant
     * @param version
     * @param content
     * @param prevs
     * @param metadata
     * @param signer
     * @param signature
     */
    public VersionManifest(IGUID invariant, IGUID version, IGUID content, Set<IGUID> prevs, IGUID metadata,
                           Role signer, String signature) {
        super(signer, ManifestType.VERSION);

        this.invariant = invariant;
        this.version = version;
        this.guid = content;
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
        return guid;
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
                isGUIDValid(guid);
    }

    @Override
    public IGUID guid() {
        return getVersionGUID();
    }

    @Override
    public InputStream contentToHash() {

        String toHash = getType() +
                "I" + getInvariantGUID().toMultiHash() + // <-- this will stop a user from forking an asset. It means that they cannot generate the same version guid under a different asset.
                "C" + getContentGUID().toMultiHash();

        toHash += getPreviousToHashOrSign();
        toHash += getMetadataToHashOrSign();

        return IO.StringToInputStream(toHash);
    }

    @Override
    protected String generateSignature(String toSign) throws SignatureException {

        if (signer == null) {
            return "";
        } else {
            return signer.sign(toSign);
        }
    }

    private IGUID makeInvariant() {
        return GUIDFactory.generateRandomGUID();
    }

    private IGUID makeVersionGUID() {

        try (InputStream inputStream = contentToHash()) {

            return GUIDFactory.generateGUID(inputStream);

        } catch (GUIDGenerationException | IOException e) {

            return new InvalidID();
        }
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
            retval = "M" + metadata.toMultiHash();
        }
        return retval;
    }

}
