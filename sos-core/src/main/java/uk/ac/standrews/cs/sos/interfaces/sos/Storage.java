package uk.ac.standrews.cs.sos.interfaces.sos;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.interfaces.locations.Location;
import uk.ac.standrews.cs.sos.interfaces.manifests.Atom;
import uk.ac.standrews.cs.sos.interfaces.manifests.Compound;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;
import uk.ac.standrews.cs.sos.interfaces.manifests.Version;
import uk.ac.standrews.cs.sos.model.manifests.CompoundType;
import uk.ac.standrews.cs.sos.model.manifests.Content;
import uk.ac.standrews.cs.storage.exceptions.StorageException;

import java.io.InputStream;
import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Storage extends SeaOfStuff {

    /**
     * Adds an atom to the Sea of Stuff.
     * The atom is stored locally and replicated according to the policy used by this instance.
     *
     * @param location of the data for the atom.
     * @return the added atom.
     * @throws StorageException
     * @throws ManifestPersistException
     *
     * @see Manifest
     */
    Atom addAtom(Location location)
            throws StorageException, ManifestPersistException;

    /**
     * Adds a stream of data to the Sea of Stuff as an atom.
     * The atom is stored locally and replicated according to the policy used by this instance.
     *
     * @param inputStream for this atom
     * @return the added atom
     * @throws StorageException
     * @throws ManifestPersistException
     */
    Atom addAtom(InputStream inputStream)
            throws StorageException, ManifestPersistException;

    /**
     * Adds a CompoundManifest to the Sea of Stuff.
     *
     * @param contents of this compound.
     * @return Compound for the added compound.
     * @throws ManifestNotMadeException
     * @throws ManifestPersistException
     *
     * @see Manifest
     */
    Compound addCompound(CompoundType type, Collection<Content> contents)
            throws ManifestNotMadeException, ManifestPersistException;

    /**
     * Adds a version of an asset to the Sea of Stuff.
     *
     * @param content of this version.
     * @param invariant guid of this asset
     * @param prevs version of this asset.
     * @param metadata of this version.
     * @return Version for the added asset.
     * @throws ManifestNotMadeException
     * @throws ManifestPersistException
     *
     */
    Version addVersion(IGUID content, IGUID invariant,
                       Collection<IGUID> prevs, Collection<IGUID> metadata)
            throws ManifestNotMadeException, ManifestPersistException;

    /**
     * Get an atom's data given an AtomManifest.
     *
     * @param atom describing the atom to retrieve.
     * @return InputStream
     */
    InputStream getAtomContent(Atom atom);

}
