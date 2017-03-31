package uk.ac.standrews.cs.sos.interfaces.actors;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.AtomNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.interfaces.model.Atom;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.model.manifests.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.protocol.DDSNotificationInfo;
import uk.ac.standrews.cs.sos.utils.Tuple;
import uk.ac.standrews.cs.storage.exceptions.StorageException;

import java.io.InputStream;
import java.util.Set;

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
     * @return Tuple<Atom, Set<Node>>
     *      - The generated atom manifest. This will contain the locations known to this node prior to any replication.
     *      - A set of DDS nodes
     * @throws StorageException
     * @throws ManifestPersistException
     */
    Tuple<Atom, Set<Node>> addAtom(AtomBuilder atomBuilder, boolean persist, DDSNotificationInfo ddsNotificationInfo) throws StorageException, ManifestPersistException;

    /**
     * Get an atom's data given an AtomManifest.
     *
     * @param atom describing the atom to retrieve.
     * @return InputStream
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

    /**
     * Flush all indexes and caches managed by the storage actor
     */
    void flush();

}
