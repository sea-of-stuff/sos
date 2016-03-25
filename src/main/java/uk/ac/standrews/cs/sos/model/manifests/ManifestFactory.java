package uk.ac.standrews.cs.sos.model.manifests;

import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.interfaces.Identity;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.utils.GUID;

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
     * @param guid
     * @param locations where the atom resides.
     * @return the manifest for the atom
     * @throws ManifestNotMadeException
     * @throws DataStorageException
     */
    public static AtomManifest createAtomManifest(GUID guid, Collection<LocationBundle> locations) {

        return new AtomManifest(guid, locations);
    }

    /**
     *
     * @param contents
     * @param identity
     * @return
     * @throws ManifestNotMadeException
     */
    public static CompoundManifest createCompoundManifest(Collection<Content> contents,
                                                          Identity identity)
            throws ManifestNotMadeException {

        return new CompoundManifest(contents, identity);
    }

    /**
     * Creates an AssetManifest given the content of the asset,
     * the GUID of a previous asset's version, the GUID of metadata associated to this
     * asset and an identity which will be used to sign the manifest.
     *
     * @param content
     * @param invariant
     * @param prevs - optional
     * @param metadata - optional
     * @param identity
     * @return an asset manifest
     * @throws ManifestNotMadeException
     */
    public static AssetManifest createAssetManifest(GUID content,
                                                    GUID invariant,
                                                    Collection<GUID> prevs,
                                                    Collection<GUID> metadata,
                                                    Identity identity)
            throws ManifestNotMadeException {

        if (content == null) {
            throw new ManifestNotMadeException("Content parameters missing or null");
        }

        return new AssetManifest(invariant, content, prevs, metadata, identity);
    }

}
