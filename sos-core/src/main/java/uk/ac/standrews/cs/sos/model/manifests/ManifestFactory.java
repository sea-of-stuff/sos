package uk.ac.standrews.cs.sos.model.manifests;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.interfaces.identity.Identity;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;

import java.util.Collection;

/**
 * This factory is used to create manifests for atoms, compounds and versions.
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
     */
    public static AtomManifest createAtomManifest(IGUID guid,
                                                  Collection<LocationBundle> locations) {

        return new AtomManifest(guid, locations);
    }

    /**
     *
     * @param type
     * @param contents
     * @param identity
     * @return a compound manifest
     * @throws ManifestNotMadeException
     */
    public static CompoundManifest createCompoundManifest(CompoundType type,
                                                          Collection<Content> contents,
                                                          Identity identity)
            throws ManifestNotMadeException {

        if (type == null) {
            throw new ManifestNotMadeException("No compound type specified");
        }

        return new CompoundManifest(type, contents, identity);
    }

    /**
     * Creates an VersionManifest given the content of the asset,
     * the GUID of a previous asset's version, the GUID of metadata associated to this
     * asset and an identity which will be used to sign the manifest.
     *
     * @param content - required
     * @param invariant
     * @param prevs
     * @param metadata
     * @param identity
     * @return an asset manifest
     * @throws ManifestNotMadeException
     */
    public static VersionManifest createVersionManifest(IGUID content,
                                                        IGUID invariant,
                                                        Collection<IGUID> prevs,
                                                        Collection<IGUID> metadata,
                                                        Identity identity)
            throws ManifestNotMadeException {

        if (content == null) {
            throw new ManifestNotMadeException("Content parameters missing or null");
        }

        return new VersionManifest(invariant, content, prevs, metadata, identity);
    }

}
