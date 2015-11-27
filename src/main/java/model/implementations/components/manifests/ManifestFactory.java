package model.implementations.components.manifests;

import model.interfaces.components.entities.Atom;
import model.interfaces.components.identity.Identity;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * The manifest factory is used to create manifests for atoms, compounds and assets.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ManifestFactory {

    /**
     * Creates an AtomManifest given an atom. The manifest is signed using
     * an arbitrary identity.
     *
     * @param atom that the manifest will refer to
     * @param identity used to sign the manifest
     * @return the manifest for the atom
     */
    public static AtomManifest createAtomManifest(Atom atom, Identity identity) {
        AtomManifest manifest = new AtomManifest(atom);
        //finaliseManifest(manifest, identity);
        return manifest;
    }

    /**
     * TODO
     * @param identity
     * @return
     */
    public static CompoundManifest createCompoundManifest(/* TODO */ Identity identity) {
        throw new NotImplementedException();
    }

    /**
     * TODO
     * @param identity
     * @return
     */
    public static AssetManifest createAssetManifest(/* TODO */ Identity identity) {
        throw new NotImplementedException();
    }

    /**
     * Generates the key-value pairs of the manifests that have not been passed
     * by the user.
     *
     * The generated key-value pairs are:
     * - GUID
     * - Signature
     *
     * @param manifest
     * @param identity
     */
    private static void finaliseManifest(BasicManifest manifest, Identity identity) {
        manifest.generateGUID();
        manifest.generateSignature(identity);
    }
}
