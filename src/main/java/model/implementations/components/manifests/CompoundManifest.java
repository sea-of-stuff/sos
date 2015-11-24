package model.implementations.components.manifests;

/**
 * Manifest describing a Compound.
 *
 * <p>
 * Manifest - GUID <br>
 * ManifestType - COMPOUND <br>
 * Timestamp - ? <br>
 * Signature - signature of the manifest <br>
 * Locations - list of GUIDs/locations <br>
 * Content - GUID Content
 * </p>
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class CompoundManifest extends ManifestImpl {

    public CompoundManifest() {
        super(ManifestConstants.COMPOUND);
    }

    @Override
    public boolean verify() {
        return false;
    }

    @Override
    public String toString() {
        return null;
    }
}
