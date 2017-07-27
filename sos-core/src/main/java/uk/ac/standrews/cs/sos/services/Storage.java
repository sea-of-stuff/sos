package uk.ac.standrews.cs.sos.services;

import uk.ac.standrews.cs.castore.exceptions.StorageException;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.AtomNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.impl.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.impl.manifests.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.model.Atom;
import uk.ac.standrews.cs.sos.model.NodesCollection;
import uk.ac.standrews.cs.sos.protocol.DDSNotificationInfo;

import java.io.InputStream;
import java.util.Iterator;

/**
 * The Storage roles defines an entry point in the SOS to store data.
 *
 * Data stored via a storage node is available to other nodes in the SOS.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Storage {

    /**
     * Adds data to the Sea of Stuff as an atom.
     *
     * @param atomBuilder defines the sources for the atom to be added
     * @param persist if true the atom is persisted in this node, otherwise it is cached (e.g. it can be later purged)
     * @param ddsNotificationInfo information used by the storage actor to notify any DDS services about the atom manifest
     * @return The generated atom manifest. This will contain the locations known to this node prior to any replication.
     *
     * @throws StorageException
     * @throws ManifestPersistException
     */
    Atom addAtom(AtomBuilder atomBuilder, boolean persist, DDSNotificationInfo ddsNotificationInfo) throws StorageException, ManifestPersistException;

    Atom addData(AtomBuilder atomBuilder, NodesCollection nodes, int replicationFactor) throws StorageException;

    /**
     * Get an atom's data given an AtomManifest.
     *
     * @param atom describing the atom to retrieve.
     * @return InputStream // TODO - return Data - see Castore
     */
    InputStream getAtomContent(Atom atom);

    /**
     * Get the data for the atom with the specified GUID
     *
     * @param guid
     * @return
     * @throws AtomNotFoundException
     */
    InputStream getAtomContent(IGUID guid) throws AtomNotFoundException;

    void addLocation(IGUID guid, LocationBundle locationBundle);

    Iterator<LocationBundle> findLocations(IGUID guid);

    boolean challenge(IGUID guid, String challenge);

    /**
     * Flush all indexes and caches managed by the storage actor
     */
    void flush();

}
