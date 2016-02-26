package uk.ac.standrews.cs.sos.model.implementations.components.manifests;

import uk.ac.standrews.cs.sos.configurations.SeaConfiguration;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.model.implementations.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.model.implementations.utils.Content;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUID;
import uk.ac.standrews.cs.sos.model.interfaces.identity.Identity;

import java.util.Collection;

/**
 * This factory is used to create manifests for atoms, compounds and assets.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ManifestFactory {

    // Suppresses default constructor, ensuring non-instantiability.
    public ManifestFactory() {}

    /**
     * Creates an AtomManifest given an atom.
     *
     * @param locations where the atom resides.
     * @return the manifest for the atom
     * @throws ManifestNotMadeException
     */
    public static AtomManifest createAtomManifest(SeaConfiguration configuration, Collection<LocationBundle> locations)
            throws ManifestNotMadeException, DataStorageException {

        GUID guid = DataStorage.storeAtom(configuration, locations);
        return new AtomManifest(guid, locations);
    }

    public static CompoundManifest createCompoundManifest(Collection<Content> contents,
                                                          Identity identity)
            throws ManifestNotMadeException {

        return new CompoundManifest(contents, identity);
    }

    /**
     * Creates a CompoundManifest given a collection of contents and a signature.
     *
     * @param contents
     * @param signature
     * @return a compound manifest
     * @throws ManifestNotMadeException
     */
    public static CompoundManifest createCompoundManifest(Collection<Content> contents,
                                                          String signature)
            throws ManifestNotMadeException {

        CompoundManifest manifest = new CompoundManifest(contents, null);
        manifest.setSignature(signature);

        return manifest;
    }

    /**
     * Creates an AssetManifest given the content of the asset,
     * the GUID of a previous asset's version, the GUID of metadata associated to this
     * asset and an identity which will be used to sign the manifest.
     *
     * @param content
     * @param prevs - optional
     * @param metadata - optional
     * @param identity
     * @return an asset manifest
     * @throws ManifestNotMadeException
     */
    public static AssetManifest createAssetManifest(Content content,
                                                    GUID invariant,
                                                    Collection<GUID> prevs,
                                                    Collection<GUID> metadata,
                                                    Identity identity)
            throws ManifestNotMadeException {

        if (content == null || identity == null) {
            throw new ManifestNotMadeException("Parameters missing or null : content or identity");
        }

        return new AssetManifest(invariant, content, prevs, metadata, identity);
    }

    public static AssetManifest createAssetManifest(Content content,
                                                    GUID invariant,
                                                    Collection<GUID> prevs,
                                                    Collection<GUID> metadata,
                                                    String signature)
            throws ManifestNotMadeException {

        AssetManifest manifest = new AssetManifest(invariant, content, prevs, metadata, null);
        manifest.setSignature(signature);

        return manifest;
    }

}
