package uk.ac.standrews.cs.sos.SOSImpl;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.location.SourceLocationException;
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
import uk.ac.standrews.cs.sos.model.manifests.atom.AtomStorage;
import uk.ac.standrews.cs.sos.model.manifests.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.node.NodesDirectory;
import uk.ac.standrews.cs.sos.storage.LocalStorage;
import uk.ac.standrews.cs.storage.exceptions.StorageException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSStorage implements Storage {

    private NodesDirectory nodesDirectory;
    private ReplicationPolicy replicationPolicy;
    private ManifestsDirectory manifestsDirectory;

    private AtomStorage atomStorage;

    public SOSStorage(Node node, NodesDirectory nodesDirectory, LocalStorage storage,
                ReplicationPolicy replicationPolicy, ManifestsDirectory manifestsDirectory) {

        this.nodesDirectory = nodesDirectory;
        this.replicationPolicy = replicationPolicy;
        this.manifestsDirectory = manifestsDirectory;

        atomStorage = new AtomStorage(node.getNodeGUID(), storage);
    }

    // TODO - use different stores based on persist boolean
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
        Collection<LocationBundle> locations = atom.getLocations();
        for(LocationBundle location:locations) {

            try {
                dataStream = LocationUtility.getInputStreamFromLocation(location.getLocation());
            } catch (SourceLocationException e) {
                continue;
            }

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

            if (manifest instanceof Atom) { // TODO - this comparison could be improved, with the manifest returning the type
                Atom atom = (Atom) manifest;
                return getAtomContent(atom);
            }
        } catch (ManifestNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    protected IGUID store(Location location, Collection<LocationBundle> bundles, boolean persist) throws StorageException {
        if (persist) {
            return atomStorage.persistAtomAndUpdateLocationBundles(location, bundles); // FIXME - this should undo the cache locations!
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

    private void replicateData(AtomManifest manifest, Collection<LocationBundle> bundles) {
        InputStream atomContent = getAtomContent(manifest);
        if (replicationPolicy.getReplicationFactor() > 0) {

            Runnable replicator = () -> {
                Iterator<Node> storageNodes = nodesDirectory.getStorageNodes().iterator();
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
    }
}
