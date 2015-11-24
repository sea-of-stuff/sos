package model.implementations.components.manifests;

import model.implementations.utils.GUID;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AssetManifest extends Manifest implements model.interfaces.components.manifests.AssetManifest {

    public GUID getAssetGUID() {
        return null;
    }

    public model.interfaces.components.manifests.AssetManifest getPreviousManifest() {
        return null;
    }

    public GUID getGUIDUnion() {
        return null;
    }

    public model.interfaces.components.manifests.AssetManifest getMetadata() {
        return null;
    }

    public boolean verify() {
        return false;
    }
}
