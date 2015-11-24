package model.implementations.components.manifests;

import model.implementations.utils.GUID;
import model.interfaces.components.entities.Asset;
import model.interfaces.components.metadata.Metadata;

/**
 * Manifest describing an Asset.
 *
 * Manifest of the form:
 * <p>
 * Manifest - GUID <br>
 * Asset - GUID <br>
 * ManifestType - ASSET <br>
 * Timestamp - ? <br>
 * Signature - ? <br>
 * Previous Asset - GUID <br>
 * Union - GUID <br>
 * Metadata - GUID
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AssetManifest extends ManifestImpl {

    public AssetManifest() {
        super(ManifestConstants.ASSET);
    }

    /**
     * TODO - Incarnation
     *
     * @return the GUID of this asset
     *
     * @see GUID
     */
    public GUID getAssetGUID() {
        return null;
    }

    /**
     * Get the previous asset's manifest of a given asset.
     *
     * @return the previous asset.
     *         Null if the asset does not have a previous one.
     *
     * @see Asset
     */
    public AssetManifest getPreviousManifest() {
        return null;
    }

    /**
     * Get the union of this asset via a GUID reference within the Sea of Stuff.
     *
     * @return GUID of the union of this asset.
     */
    public GUID getGUIDUnion() {
        return null;
    }

    /**
     * Get the metadata associated with an asset's manifest
     *
     * @return Metadata associated with the asset.
     *         Null if there is not metadata associated with the asset.
     *
     * @see Asset
     * @see Metadata
     */
    public AssetManifest getMetadata() {
        return null;
    }

    public boolean verify() {
        return false;
    }

    @Override
    public String toString() {
        return null;
    }
}
