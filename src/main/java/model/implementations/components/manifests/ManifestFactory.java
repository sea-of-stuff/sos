package model.implementations.components.manifests;

import model.exceptions.ManifestNotMadeException;
import model.implementations.utils.Content;
import model.implementations.utils.GUID;
import model.implementations.utils.Location;
import model.interfaces.identity.Identity;

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
     * Creates an AtomManifest given an atom.
     *
     * @param locations where the atom resides.
     * @return the manifest for the atom
     * @throws ManifestNotMadeException
     */
    public static AtomManifest createAtomManifest(Collection<Location> locations)
            throws ManifestNotMadeException {

        AtomManifest manifest = new AtomManifest(locations);
        return manifest;
    }

    /**
     * Creates a CompoundManifest given a collection of contents and an identity
     * which will be used to sign the manifest.
     *
     * @param contents
     * @param identity
     * @return a compound manifest
     * @throws ManifestNotMadeException
     */
    public static CompoundManifest createCompoundManifest(Collection<Content> contents,
                                                          Identity identity)
            throws ManifestNotMadeException {

        CompoundManifest manifest = new CompoundManifest(contents, identity);
        return manifest;
    }

    /**
     * Creates an AssetManifest given the content of the asset,
     * the GUID of a previous asset's version, the GUID of metadata associated to this
     * asset and an identity which will be used to sign the manifest.
     *
     * @param content
     * @param previous - optional
     * @param metadata - optional
     * @param identity
     * @return an asset manifest
     * @throws ManifestNotMadeException
     */
    public static AssetManifest createAssetManifest(Content content,
                                                    GUID previous,
                                                    GUID metadata,
                                                    Identity identity)
            throws ManifestNotMadeException {

        AssetManifest manifest;

        if (previous == null && metadata == null)
            manifest = new AssetManifest(content, identity);
        else if (previous == null && metadata != null)
            manifest = new AssetManifest(content, metadata, identity);
        else if (previous != null && metadata == null)
            manifest = new AssetManifest(previous, content, identity);
        else
            manifest = new AssetManifest(previous, content, metadata, identity);

        return manifest;
    }

}
