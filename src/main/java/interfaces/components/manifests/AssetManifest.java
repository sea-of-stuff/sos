package interfaces.components.manifests;

import interfaces.components.GUID;
import interfaces.components.Metadata;
import interfaces.entities.Asset;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface AssetManifest extends Manifest {

    /**
     * TODO - Incarnation
     *
     * @return
     *
     * @see GUID
     */
    GUID getAssetGUID();

    /**
     * Get the previous asset's manifest of a given asset
     *
     * @return the previous asset. Null if the asset does not have a previous one.
     *
     * @see Asset
     */
    AssetManifest getPreviousManifest();

    /**
     * Get the metadata associated with an asset's manifest
     *
     * @return Metadata                 associated with the asset.
     *                                  Null if there is not metadata associated with the asset.
     *
     * @see Asset
     * @see Metadata
     */
    Metadata getMetadata();
}
