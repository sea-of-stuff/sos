package uk.ac.standrews.cs.sos.actors;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.castore.exceptions.StorageException;
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
public interface Storage extends SeaOfStuff {

    /**
     * Adds data to the Sea of Stuff as an atom.
     *
     * @param atomBuilder defines the sources for the atom to be added
     * @param persist if true the atom is persisted in this node, otherwise it is cached (e.g. it can be later purged)
     * @param ddsNotificationInfo information used by the storage actor to notify any DDS actors about the atom manifest
     * @return The generated atom manifest. This will contain the locations known to this node prior to any replication.
     *
     * @throws StorageException
     * @throws ManifestPersistException
     */
    Atom addAtom(AtomBuilder atomBuilder, boolean persist, DDSNotificationInfo ddsNotificationInfo) throws StorageException, ManifestPersistException;

    Atom addData(AtomBuilder atomBuilder, NodesCollection nodes, int replicationFactor);

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


    // FIXME - challenge()
    /**
     * Challenge this node the integrity of the entity matching the given GUID
     *
     * Hash-based verification ensures that a file has not been corrupted by
     * comparing the data's hash value to a previously calculated value.
     * If these values match, the data is presumed to be unmodified.
     * Due to the nature of hash functions, hash collisions may result
     * in false positives, but the likelihood of collisions is
     * often negligible with random corruption. (https://en.wikipedia.org/wiki/File_verification)
     *
     * @param manifest                      to be verified
     * @return <code>true</code>            if the GUID of the manifest matches
     *                                      the content referred by the manifest.
     */
    boolean verifyIntegrity(IGUID guid, String challenge);

    /**
     * Flush all indexes and caches managed by the storage actor
     */
    void flush();

}
