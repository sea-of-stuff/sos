package uk.ac.standrews.cs.sos.services;

import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.castore.exceptions.StorageException;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.manifest.AtomNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.impl.manifests.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.model.Atom;
import uk.ac.standrews.cs.sos.model.NodesCollection;
import uk.ac.standrews.cs.sos.model.SecureAtom;

import java.util.Iterator;

/**
 * The Storage roles defines an entry point in the SOS to store data.
 * Atom manifests are stored via the DDS (@see DataDiscoveryService)
 *
 * Data stored via a storage node is available to other nodes in the SOS.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Storage {

    /**
     * Adds data to the Sea of Stuff as an atom.
     * The atom manifest is added to the DDS.
     *
     * @param atomBuilder defines the sources for the atom to be added
     * @return The generated atom manifest. This will contain the locations known to this node prior to any replication.
     *
     * @throws DataStorageException
     * @throws ManifestPersistException
     */
    Atom addAtom(AtomBuilder atomBuilder) throws DataStorageException, ManifestPersistException;

    /**
     * Adds an atom to the SOS as a secure encrypted entity.
     * The Role used for encryption will be the one specified within the builder OR the current active role.
     *
     * @param atomBuilder
     * @return
     * @throws ManifestPersistException
     * @throws ManifestNotMadeException
     * @throws DataStorageException
     */
    SecureAtom addSecureAtom(AtomBuilder atomBuilder) throws ManifestPersistException, ManifestNotMadeException, DataStorageException;

    Atom addData(AtomBuilder atomBuilder, NodesCollection nodes, int replicationFactor) throws StorageException;

    /**
     * Get an atom's data given an AtomManifest.
     *
     * @param atom describing the atom to retrieve.
     * @return Data
     */
    Data getAtomContent(Atom atom) throws AtomNotFoundException;

    /**
     * Get the data for the atom with the specified GUID
     *
     * @param guid
     * @return
     * @throws AtomNotFoundException
     */
    Data getAtomContent(IGUID guid) throws AtomNotFoundException;

    void addLocation(IGUID guid, LocationBundle locationBundle);

    Iterator<LocationBundle> findLocations(IGUID guid);

    boolean challenge(IGUID guid, String challenge);

    /**
     * Flush all indexes and caches managed by the storage actor
     */
    void flush();

}
