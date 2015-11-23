package model.interfaces.components.manifests;

import model.interfaces.components.utils.GUID;
import model.interfaces.entities.Asset;
import model.interfaces.metadata.Metadata;

/**
 *
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
public interface AssetManifest extends Manifest {

    /**
     * TODO - Incarnation
     *
     * @return the GUID of this asset
     *
     * @see GUID
     */
    GUID getAssetGUID();

    /**
     * Get the previous asset's manifest of a given asset.
     *
     * @return the previous asset.
     *         Null if the asset does not have a previous one.
     *
     * @see Asset
     */
    AssetManifest getPreviousManifest();

    /**
     * Get the union of this asset via a GUID reference within the Sea of Stuff.
     *
     * @return GUID of the union of this asset.
     */
    GUID getGUIDUnion();

    /**
     * Get the metadata associated with an asset's manifest
     *
     * @return Metadata associated with the asset.
     *         Null if there is not metadata associated with the asset.
     *
     * @see Asset
     * @see Metadata
     */
    AssetManifest getMetadata();
}
