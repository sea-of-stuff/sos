package model.implementations.components.manifests;

import model.implementations.utils.GUID;
import model.interfaces.components.metadata.Metadata;
import model.interfaces.identity.Identity;
import model.interfaces.identity.Signature;

/**
 * An Asset is identified by an asset GUID. Unlike other GUIDs they are not
 * derived from contents. Instead an asset GUID is good for all time
 * irrespective of the asset's contents.
 * Assets do not contain data, thus they exist only in the manifest space.
 * Assets refer to unions - and they are used to assert commonality over a
 * history of changes of unions.
 * <br>
 * This class defines the manifest describing an Asset, which takes the
 * following form:
 * <p>
 * Manifest - GUID <br>
 * Asset - GUID <br>
 * ManifestType - ASSET <br>
 * Timestamp - ? <br>
 * Signature - ? <br>
 * Previous Asset - GUID <br>
 * Union - GUID <br>
 * Metadata - GUID
 * </p>
 *
 * @see GUID
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AssetManifest extends BasicManifest {

    protected AssetManifest() {
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
     * @see Metadata
     */
    public AssetManifest getMetadata() {
        return null;
    }

    public boolean verify() {
        return false;
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public String toJSON() {
        return null;
    }

    @Override
    protected GUID generateGUID() {
        return null;
    }

    protected Signature generateSignature(Identity identity) {
        return null;
    }

}
