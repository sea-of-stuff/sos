package model.implementations.components.manifests;

import model.exceptions.ManifestNotMadeException;
import model.implementations.utils.Content;
import model.implementations.utils.GUID;
import model.implementations.utils.Location;
import model.interfaces.identity.Identity;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Collection;

/**
 * This factory is used to create manifests for atoms, compounds and assets.
 *
 * TODO - identities are not used.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ManifestFactory {

    // Suppresses default constructor, ensuring non-instantiability.
    private ManifestFactory() {}

    /**
     * Creates an AtomManifest given an atom. The manifest is signed using
     * an arbitrary identity.
     *
     * @param locations // FIXME that the manifest will refer to
     * @param identity used to sign the manifest
     * @return the manifest for the atom
     */
    public static AtomManifest createAtomManifest(Collection<Location> locations,
                                                  Identity identity)
            throws ManifestNotMadeException {
        AtomManifest manifest = new AtomManifest(locations);

        return manifest;
    }

    /**
     * Creates a CompoundManifest.
     *
     * @param identity
     * @return a compound manifest
     */
    public static CompoundManifest createCompoundManifest(Identity identity) {
        throw new NotImplementedException();
    }

    /**
     * Creates an AssetManifest.
     *
     * @param identity
     * @return an asset manifest
     */
    public static AssetManifest createAssetManifest(Content content, GUID previous, GUID metadata, Identity identity) {

        AssetManifest manifest;

        if (previous == null && metadata == null)
            manifest = new AssetManifest(content);
        else if (previous == null && metadata != null)
            manifest = new AssetManifest(content, metadata);
        else if (previous != null && metadata == null)
            manifest = new AssetManifest(previous, content);
        else
            manifest = new AssetManifest(previous, content, metadata);

        return manifest;
    }

}
