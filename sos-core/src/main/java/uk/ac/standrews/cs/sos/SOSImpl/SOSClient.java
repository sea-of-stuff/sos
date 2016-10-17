package uk.ac.standrews.cs.sos.SOSImpl;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.location.SourceLocationException;
import uk.ac.standrews.cs.sos.exceptions.manifest.*;
import uk.ac.standrews.cs.sos.exceptions.metadata.SOSMetadataException;
import uk.ac.standrews.cs.sos.interfaces.identity.Identity;
import uk.ac.standrews.cs.sos.interfaces.locations.Location;
import uk.ac.standrews.cs.sos.interfaces.manifests.Atom;
import uk.ac.standrews.cs.sos.interfaces.manifests.Compound;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;
import uk.ac.standrews.cs.sos.interfaces.manifests.Version;
import uk.ac.standrews.cs.sos.interfaces.manifests.managers.ManifestsManager;
import uk.ac.standrews.cs.sos.interfaces.metadata.MetadataEngine;
import uk.ac.standrews.cs.sos.interfaces.metadata.SOSMetadata;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.interfaces.policy.PolicyManager;
import uk.ac.standrews.cs.sos.interfaces.sos.Client;
import uk.ac.standrews.cs.sos.model.locations.LocationUtility;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.model.locations.bundles.ProvenanceLocationBundle;
import uk.ac.standrews.cs.sos.model.manifests.*;
import uk.ac.standrews.cs.sos.model.manifests.atom.AtomStorage;
import uk.ac.standrews.cs.sos.model.manifests.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.model.manifests.builders.VersionBuilder;
import uk.ac.standrews.cs.sos.model.storage.InternalStorage;
import uk.ac.standrews.cs.sos.node.NodeManager;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;
import uk.ac.standrews.cs.storage.data.InputStreamData;
import uk.ac.standrews.cs.storage.exceptions.StorageException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Stream;

/**
 * Implementation class for the SeaOfStuff interface.
 * The purpose of this class is to delegate jobs to the appropriate manifests
 * of the sea of stuff.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSClient implements Client {

    private NodeManager nodeManager;
    private PolicyManager policyManager;
    private Identity identity;
    private ManifestsManager manifestsManager;
    private MetadataEngine metadataEngine;

    private AtomStorage atomStorage;

    public SOSClient(Node node, NodeManager nodeManager, InternalStorage storage, ManifestsManager manifestsManager,
                     Identity identity, PolicyManager policyManager, MetadataEngine metadataEngine) {

        this.nodeManager = nodeManager;
        this.manifestsManager = manifestsManager;
        this.identity = identity;
        this.policyManager = policyManager;
        this.metadataEngine = metadataEngine;

        atomStorage = new AtomStorage(node.getNodeGUID(), storage);
    }

    @Override
    public PolicyManager getPolicyManager() {
        return policyManager;
    }

    @Override
    public Atom addAtom(AtomBuilder atomBuilder) throws StorageException, ManifestPersistException {
        SOS_LOG.log(LEVEL.INFO, "Adding atom: " + atomBuilder.toString());
        long start = System.nanoTime();

        Collection<LocationBundle> bundles = new ArrayList<>();

        IGUID guid;
        if (atomBuilder.isLocation()) {
            Location location = atomBuilder.getLocation();
            bundles.add(new ProvenanceLocationBundle(location));
            guid = store(location, bundles);
        } else if (atomBuilder.isInputStream()) {
            InputStream inputStream = atomBuilder.getInputStream();
            guid = store(inputStream, bundles);
        } else {
            throw new StorageException("AtomBuilder has not been set correctly");
        }

        AtomManifest manifest = ManifestFactory.createAtomManifest(guid, bundles);
        manifestsManager.addManifest(manifest);

        long end = System.nanoTime();
        SOS_LOG.log(LEVEL.INFO, "Atom: " + manifest.getContentGUID()
                +" added in " + (end - start) / 1000000000.0 + " seconds");

        SOS_LOG.log(LEVEL.INFO, "Replicating atom");
        replicateData(manifest);

        return manifest;
    }

    @Override
    public Compound addCompound(CompoundType type, Collection<Content> contents)
            throws ManifestNotMadeException, ManifestPersistException {
        SOS_LOG.log(LEVEL.INFO, "Adding compound");

        CompoundManifest manifest = ManifestFactory.createCompoundManifest(type, contents, identity);
        manifestsManager.addManifest(manifest);

        SOS_LOG.log(LEVEL.INFO, "Compound added: " + manifest.getContentGUID());

        return manifest;
    }

    // TODO - process metadata given the content, see policy
    @Override
    public Version addVersion(VersionBuilder versionBuilder)
            throws ManifestNotMadeException, ManifestPersistException {
        SOS_LOG.log(LEVEL.INFO, "Adding version");

        IGUID content = versionBuilder.getContent();
        IGUID invariant = versionBuilder.getInvariant();
        Collection<IGUID> prevs = versionBuilder.getPreviousCollection();
        Collection<IGUID> metadata = versionBuilder.getMetadataCollection();

        VersionManifest manifest = ManifestFactory.createVersionManifest(content, invariant, prevs, metadata, identity);
        manifestsManager.addManifest(manifest);

        SOS_LOG.log(LEVEL.INFO, "Version added: " + manifest.getContentGUID());

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
        SOS_LOG.log(LEVEL.INFO, "Getting content for atom: " + atom.getContentGUID());

        InputStream dataStream = null;
        Collection<LocationBundle> locations = atom.getLocations();
        for (LocationBundle location : locations) {

            try {
                dataStream = LocationUtility.getInputStreamFromLocation(location.getLocation());
            } catch (SourceLocationException e) {
                continue;
            }

            if (dataStream != null) {
                break;
            }
        }

        SOS_LOG.log(LEVEL.INFO, "Returning atom " + atom.getContentGUID() + "data stream");

        return dataStream;
    }

    @Override
    public void addManifest(Manifest manifest, boolean recursive) throws ManifestPersistException {
        SOS_LOG.log(LEVEL.INFO, "Adding manifest " + manifest.getContentGUID());

        manifestsManager.addManifest(manifest);

        // TODO - recursively look for other manifests to add to the NodeManager
        if (recursive) {
            throw new UnsupportedOperationException();
        }

        SOS_LOG.log(LEVEL.INFO, "Added manifest " + manifest.getContentGUID());
    }

    @Override
    public Manifest getManifest(IGUID guid) throws ManifestNotFoundException {
        SOS_LOG.log(LEVEL.INFO, "Getting manifest " + guid);

        return manifestsManager.findManifest(guid);
    }

    @Override
    public Version getHEAD(IGUID invariant) throws HEADNotFoundException {
        return manifestsManager.getHEAD(invariant);
    }

    @Override
    public void setHEAD(IGUID version) throws HEADNotSetException {
        manifestsManager.setHEAD(version);
    }

    @Override
    public boolean verifyManifest(Identity identity, Manifest manifest) throws ManifestVerificationException {
        SOS_LOG.log(LEVEL.INFO, "Verifying manifest " + manifest.getContentGUID());

        boolean success = manifest.verify(identity);

        SOS_LOG.log(LEVEL.INFO, "Manifest " + manifest.getContentGUID() + " verified. Result: " + success);

        return success;
    }

    @Override
    public Stream<Manifest> getAllManifests() {
        SOS_LOG.log(LEVEL.INFO, "Retrieving all manifests");

        return manifestsManager.getAllManifests();
    }

    @Override // FIXME - add metadata
    public SOSMetadata processMetadata(InputStream inputStream) throws SOSMetadataException {

        InputStreamData data = new InputStreamData(inputStream);
        return metadataEngine.processData(data);
    }

    protected IGUID store(Location location, Collection<LocationBundle> bundles) throws StorageException {
        return atomStorage.cacheAtomAndUpdateLocationBundles(location, bundles);
    }

    protected IGUID store(InputStream inputStream, Collection<LocationBundle> bundles) throws StorageException {
        return atomStorage.cacheAtomAndUpdateLocationBundles(inputStream, bundles);
    }

    private void replicateData(AtomManifest manifest) {
        InputStream atomContent = getAtomContent(manifest);
        if (policyManager.getReplicationPolicy().getReplicationFactor() > 0) {

            Runnable replicator = () -> {
                Iterator<Node> storageNodes = nodeManager.getStorageNodes().iterator();
                // NOTE: Can also contact NDS for storage nodes: NDS_GET_NODE by role
                if (storageNodes.hasNext()) {
                    Node replicaNode = storageNodes.next();
                    try {
                        atomStorage.persistAtomToRemote(replicaNode, atomContent);
                    } catch (StorageException e) {
                        e.printStackTrace();
                    }
                }
            };

            replicator.run();
        }
    }
}
