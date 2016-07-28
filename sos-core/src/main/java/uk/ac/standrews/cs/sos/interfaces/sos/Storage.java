package uk.ac.standrews.cs.sos.interfaces.sos;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.interfaces.locations.Location;
import uk.ac.standrews.cs.sos.interfaces.manifests.Atom;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;
import uk.ac.standrews.cs.storage.exceptions.StorageException;

import java.io.InputStream;

/**
 * The Storage roles defines an entry point in the SOS to store data.
 *
 * Data stored via a storage node is available to other nodes in the SOS.
 *
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
     * Get an atom's data given an AtomManifest.
     *
     * @param atom describing the atom to retrieve.
     * @return InputStream
     */
    InputStream getAtomContent(Atom atom);

    /**
     *
     * @param guid
     * @return
     */
    InputStream getAtomContent(IGUID guid);

    /**
     * Adds the manifest to the SOS
     * @param manifest
     * @throws ManifestPersistException
     */
    void addManifest(Manifest manifest) throws ManifestPersistException;

    Manifest getManifest(IGUID guid);
}
