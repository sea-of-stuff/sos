package uk.ac.standrews.cs.sos.actors;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestsDirectoryException;
import uk.ac.standrews.cs.sos.interfaces.locations.Location;
import uk.ac.standrews.cs.sos.interfaces.manifests.Atom;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;
import uk.ac.standrews.cs.sos.interfaces.manifests.ManifestsDirectory;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.interfaces.policy.ReplicationPolicy;
import uk.ac.standrews.cs.sos.interfaces.sos.Storage;
import uk.ac.standrews.cs.sos.model.locations.LocationUtility;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.model.locations.bundles.ProvenanceLocationBundle;
import uk.ac.standrews.cs.sos.model.manifests.AtomManifest;
import uk.ac.standrews.cs.sos.model.manifests.ManifestFactory;
import uk.ac.standrews.cs.sos.model.manifests.ManifestType;
import uk.ac.standrews.cs.sos.model.manifests.atom.AtomStorage;
import uk.ac.standrews.cs.sos.model.manifests.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.node.directory.LocalNodesDirectory;
import uk.ac.standrews.cs.sos.storage.LocalStorage;
import uk.ac.standrews.cs.storage.exceptions.StorageException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSStorage implements Storage {

    private LocalNodesDirectory localNodesDirectory;
    private ReplicationPolicy replicationPolicy;
    private ManifestsDirectory manifestsDirectory;

    private AtomStorage atomStorage;

    public SOSStorage(Node node, LocalNodesDirectory localNodesDirectory, LocalStorage storage,
                      ReplicationPolicy replicationPolicy, ManifestsDirectory manifestsDirectory) {

        this.localNodesDirectory = localNodesDirectory;
        this.replicationPolicy = replicationPolicy;
        this.manifestsDirectory = manifestsDirectory;

        atomStorage = new AtomStorage(node.getNodeGUID(), storage);
    }

    @Override
    public Atom addAtom(AtomBuilder atomBuilder, boolean persist) throws StorageException, ManifestPersistException {
        Collection<LocationBundle> bundles = new ArrayList<>();

        IGUID guid;
        if (atomBuilder.isLocation()) {
            Location location = atomBuilder.getLocation();
            bundles.add(new ProvenanceLocationBundle(location));
            guid = store(location, bundles, persist);
        } else if (atomBuilder.isInputStream()) {
            InputStream inputStream = atomBuilder.getInputStream();
            guid = store(inputStream, bundles, persist);
        } else {
            throw new StorageException("AtomBuilder has not been set correctly");
        }

        AtomManifest manifest = ManifestFactory.createAtomManifest(guid, bundles);
        manifestsDirectory.addManifest(manifest);

        replicateData(manifest, bundles);
        try {
            manifestsDirectory.updateAtom(manifest);
        } catch (ManifestsDirectoryException | ManifestNotFoundException e) {
            throw new StorageException("Unable to update atom manifest after replication");
        }

        return manifest;
    }


    /**
     * Return an InputStream for the given Atom.
     * The caller should ensure that the stream is closed.
     *
     * @param atom describing the atom to retrieve.
     * @return
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
    public InputStream getAtomContent(IGUID guid) {
        try {
            Manifest manifest = manifestsDirectory.findManifest(guid);

            if (manifest.getManifestType() == ManifestType.ATOM) {
                Atom atom = (Atom) manifest;
                return getAtomContent(atom);
            }
        } catch (ManifestNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void flush() {
        atomStorage.flush();
    }

    protected IGUID store(Location location, Collection<LocationBundle> bundles, boolean persist) throws StorageException {
        if (persist) {
            return atomStorage.persistAtomAndUpdateLocationBundles(location, bundles); // FIXME - this should undo the cache locations(and indeX)
        } else {
            return atomStorage.cacheAtomAndUpdateLocationBundles(location, bundles);
        }
    }

    protected IGUID store(InputStream inputStream, Collection<LocationBundle> bundles, boolean persist) throws StorageException {
        if (persist) {
            return atomStorage.persistAtomAndUpdateLocationBundles(inputStream, bundles);
        } else {
            return atomStorage.cacheAtomAndUpdateLocationBundles(inputStream, bundles);
        }
    }

    // TODO - move this method in a Replication class
    private void replicateData(AtomManifest manifest, Collection<LocationBundle> bundles) {

        try (InputStream atomContent = getAtomContent(manifest)) {
            if (replicationPolicy.getReplicationFactor() > 0) {

                Runnable replicator = () -> {
                    Iterator<Node> storageNodes = localNodesDirectory.getStorageNodes().iterator();
                    // NOTE: contact NDS for storage nodes: NDS_GET_NODE by role

                    if (storageNodes.hasNext()) {
                        Node replicaNode = storageNodes.next();
                        try {
                            atomStorage.persistAtomToRemote(replicaNode, atomContent, bundles);
                        } catch (StorageException e) {
                            e.printStackTrace();
                        }
                    }
                };

                replicator.run();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
