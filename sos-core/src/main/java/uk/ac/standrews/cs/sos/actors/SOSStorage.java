package uk.ac.standrews.cs.sos.actors;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.sos.actors.protocol.DDSNotificationInfo;
import uk.ac.standrews.cs.sos.actors.protocol.tasks.ManifestReplication;
import uk.ac.standrews.cs.sos.exceptions.AtomNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.interfaces.actors.DDS;
import uk.ac.standrews.cs.sos.interfaces.actors.NDS;
import uk.ac.standrews.cs.sos.interfaces.actors.Storage;
import uk.ac.standrews.cs.sos.interfaces.locations.Location;
import uk.ac.standrews.cs.sos.interfaces.model.Atom;
import uk.ac.standrews.cs.sos.interfaces.model.Manifest;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.interfaces.policy.DataReplicationPolicy;
import uk.ac.standrews.cs.sos.model.locations.LocationUtility;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.model.locations.bundles.ProvenanceLocationBundle;
import uk.ac.standrews.cs.sos.model.manifests.AtomManifest;
import uk.ac.standrews.cs.sos.model.manifests.ManifestFactory;
import uk.ac.standrews.cs.sos.model.manifests.ManifestType;
import uk.ac.standrews.cs.sos.model.manifests.atom.AtomStorage;
import uk.ac.standrews.cs.sos.model.manifests.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.storage.LocalStorage;
import uk.ac.standrews.cs.sos.tasks.TasksQueue;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;
import uk.ac.standrews.cs.sos.utils.Tuple;
import uk.ac.standrews.cs.storage.exceptions.StorageException;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSStorage implements Storage {

    private DataReplicationPolicy dataReplicationPolicy;

    private NDS nds;
    private DDS dds;

    private AtomStorage atomStorage;

    public SOSStorage(Node node, LocalStorage storage, DataReplicationPolicy dataReplicationPolicy, NDS nds, DDS dds) {
        this.dataReplicationPolicy = dataReplicationPolicy;
        this.nds = nds;
        this.dds = dds;

        atomStorage = new AtomStorage(node.getNodeGUID(), storage);
    }

    @Override
    public Tuple<Atom, Set<Node>> addAtom(AtomBuilder atomBuilder, boolean persist, DDSNotificationInfo ddsNotificationInfo) throws StorageException, ManifestPersistException {
        Set<LocationBundle> bundles = new LinkedHashSet<>();

        IGUID guid = addAtom(atomBuilder, bundles, persist);

        AtomManifest manifest = ManifestFactory.createAtomManifest(guid, bundles);
        dds.addManifest(manifest, false);

        Set<Node> defaultDDSNodes = getDefaultDDSNodesForReplication(ddsNotificationInfo);

        // Run asynchronously
        try {
            replicateData(manifest);
            notifyDDS(ddsNotificationInfo, defaultDDSNodes, manifest);
        } catch (SOSProtocolException | IOException e) {
            SOS_LOG.log(LEVEL.ERROR, "Unable to replicate data/notify DDS nodes correctly: " + e.getMessage());
        }

        // This may return before data is replicated and the DDS nodes are notified
        return new Tuple<>(manifest, defaultDDSNodes);
    }

    /**
     * Return an InputStream for the given Atom.
     * The caller should ensure that the stream is closed.
     *
     * TODO - find other locations
     *
     * @param atom describing the atom to retrieve.
     * @return data referenced by the atom
     */
    @Override
    public InputStream getAtomContent(Atom atom) {
        InputStream dataStream = null;

        Iterator<LocationBundle> it = atomStorage.getLocationsIterator(atom.guid());
        while(it.hasNext()) {
            LocationBundle locationBundle = it.next();

            Location location = locationBundle.getLocation();
            dataStream = LocationUtility.getInputStreamFromLocation(location);

            if (dataStream != null) {
                break;
            }
        }

        return dataStream;
    }

    @Override
    public InputStream getAtomContent(IGUID guid) throws AtomNotFoundException {
        try {
            Manifest manifest = dds.getManifest(guid);

            if (manifest.getType() == ManifestType.ATOM) {
                Atom atom = (Atom) manifest;
                return getAtomContent(atom);
            }
        } catch (ManifestNotFoundException e) {
            throw new AtomNotFoundException();
        }

        return null;
    }

    @Override
    public void flush() {
        atomStorage.flush();
    }

    private IGUID addAtom(AtomBuilder atomBuilder, Set<LocationBundle> bundles, boolean persist) throws StorageException {
        IGUID guid;
        if (atomBuilder.isLocation()) {
            guid = addAtomByLocation(atomBuilder, bundles, persist);
        } else if (atomBuilder.isInputStream()) {
            guid = addAtomByStream(atomBuilder, bundles, persist);
        } else {
            throw new StorageException("AtomBuilder has not been set correctly");
        }

        return guid;
    }

    private IGUID addAtomByLocation(AtomBuilder atomBuilder, Set<LocationBundle> bundles, boolean persist) throws StorageException {
        Location location = atomBuilder.getLocation();
        bundles.add(new ProvenanceLocationBundle(location));
        return store(location, bundles, persist);
    }

    private IGUID addAtomByStream(AtomBuilder atomBuilder, Set<LocationBundle> bundles, boolean persist) throws StorageException {
        InputStream inputStream = atomBuilder.getInputStream();
        return store(inputStream, bundles, persist);
    }

    private IGUID store(Location location, Set<LocationBundle> bundles, boolean persist) throws StorageException {
        if (persist) {
            return atomStorage.persistAtomAndUpdateLocationBundles(location, bundles); // FIXME - this should undo the cache locations(and indeX)
        } else {
            return atomStorage.cacheAtomAndUpdateLocationBundles(location, bundles);
        }
    }

    private IGUID store(InputStream inputStream, Set<LocationBundle> bundles, boolean persist) throws StorageException {
        if (persist) {
            return atomStorage.persistAtomAndUpdateLocationBundles(inputStream, bundles);
        } else {
            return atomStorage.cacheAtomAndUpdateLocationBundles(inputStream, bundles);
        }
    }

    private void replicateData(Atom atom) throws SOSProtocolException, IOException {

        int replicationFactor = dataReplicationPolicy.getReplicationFactor();
        if (replicationFactor > 0) {

            try (InputStream data = getAtomContent(atom)) {

                // FIXME - check if data is already replicated.
                // Note: could also have requests, such that we instruct another node (a storage node) to replicate the data on behalf of this node

                Iterator<Node> storageNodes = nds.getStorageNodesIterator();
                atomStorage.replicate(data, storageNodes, replicationFactor, nds, dds);
            }
        }
    }

    private Set<Node> getDefaultDDSNodesForReplication(DDSNotificationInfo ddsNotificationInfo) {

        Set<Node> retval = Collections.EMPTY_SET;

        if (ddsNotificationInfo.notifyDDSNodes()) {

            if (ddsNotificationInfo.useDefaultDDSNodes()) {
                retval = nds.getDDSNodes();
                SOS_LOG.log(LEVEL.DEBUG,"nodes retrieved: " + retval.size());
            }
        }

        return retval;
    }

    /**
     * Send manifest to DDS nodes
     *
     * TODO - see manifest replication info
     * - embed dds notif info in configuration policies
     * - set a limit on the number of dds nodes
     * @param ddsNotificationInfo
     * @param manifest
     */
    private void notifyDDS(DDSNotificationInfo ddsNotificationInfo, Set<Node> defaultDDSNodes, AtomManifest manifest) throws SOSProtocolException {

        if (ddsNotificationInfo.notifyDDSNodes()) {

            Set<Node> ddsNodes = new HashSet<>(); // Remember that HashSet does not preserve order
            ddsNodes.addAll(defaultDDSNodes);

            if (ddsNotificationInfo.useSuggestedDDSNodes()) {
                Set<Node> suggestedNodes = ddsNotificationInfo.getSuggestedDDSNodes();
                ddsNodes.addAll(suggestedNodes);
            }

            ManifestReplication replicationTask = new ManifestReplication(manifest, ddsNodes.iterator(), ddsNotificationInfo.getMaxDefaultDDSNodes(), dds);
            TasksQueue.instance().performSyncTask(replicationTask);
        }
    }

}
