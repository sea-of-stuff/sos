package uk.ac.standrews.cs.sos.impl.manifest;

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.impl.datamodel.*;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.model.*;

import java.util.HashMap;
import java.util.Set;

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
    public static Atom createAtomManifest(IGUID guid, Set<LocationBundle> locations) {

        return new AtomManifest(guid, locations);
    }

    public static SecureAtom createSecureAtomManifest(IGUID guid, Set<LocationBundle> locations, HashMap<IGUID, String> rolesToKeys) {

        return new SecureAtomManifest(guid, locations, rolesToKeys);
    }

    /**
     *
     * @param type
     * @param contents
     * @param role
     * @return a compound manifest
     * @throws ManifestNotMadeException
     */
    public static Compound createCompoundManifest(CompoundType type,
                                                          Set<Content> contents,
                                                          Role role) throws ManifestNotMadeException {

        if (type == null) {
            throw new ManifestNotMadeException("No compound type specified");
        }

        return new CompoundManifest(type, contents, role);
    }

    public static SecureCompound createSecureCompoundManifest(CompoundType type,
                                                  Set<Content> contents,
                                                  Role role) throws ManifestNotMadeException {

        if (type == null) {
            throw new ManifestNotMadeException("No compound type specified");
        }

        return new SecureCompoundManifest(type, contents, role);
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
     * @param role
     * @return an asset manifest
     * @throws ManifestNotMadeException
     */
    public static Version createVersionManifest(IGUID content,
                                                        IGUID invariant,
                                                        Set<IGUID> prevs,
                                                        IGUID metadata,
                                                        Role role) throws ManifestNotMadeException {

        if (content == null) {
            throw new ManifestNotMadeException("Content parameters missing or null");
        }

        return new VersionManifest(invariant, content, prevs, metadata, role);
    }

}
