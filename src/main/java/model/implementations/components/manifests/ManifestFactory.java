package model.implementations.components.manifests;

import model.implementations.utils.Location;
import model.interfaces.identity.Identity;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Collection;

/**
 * This factory is used to create manifests for atoms, compounds and assets.
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
     * @param atom that the manifest will refer to
     * @param identity used to sign the manifest
     * @return the manifest for the atom
     */
    public static AtomManifest createAtomManifest(Collection<Location> locations, Identity identity) {
        AtomManifest manifest = new AtomManifest(locations);
        // XXX - move to internal constructor
        // manifest.generateGUID();
        return manifest;
    }

    /**
     * Creates a CompoundManifest.
     *
     * @param identity
     * @return a compound manifest
     */
    public static CompoundManifest createCompoundManifest(/* TODO */ Identity identity) {
        throw new NotImplementedException();
    }

    /**
     * Creates an AssetManifest.
     *
     * @param identity
     * @return an asset manifest
     */
    public static AssetManifest createAssetManifest(/* TODO */ Identity identity) {
        throw new NotImplementedException();
    }

}
