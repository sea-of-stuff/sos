package uk.ac.standrews.cs.sos.impl.services;

import uk.ac.standrews.cs.castore.interfaces.IDirectory;
import uk.ac.standrews.cs.castore.interfaces.IFile;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.guid.impl.keys.InvalidID;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.SettingsConfiguration;
import uk.ac.standrews.cs.sos.exceptions.manifest.HEADNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.exceptions.manifest.TIPNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.node.NodesCollectionException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.datamodel.directory.*;
import uk.ac.standrews.cs.sos.impl.manifest.ManifestParam;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.impl.node.NodesCollectionImpl;
import uk.ac.standrews.cs.sos.instrument.InstrumentFactory;
import uk.ac.standrews.cs.sos.instrument.StatsTYPE;
import uk.ac.standrews.cs.sos.interfaces.manifests.ManifestsCache;
import uk.ac.standrews.cs.sos.interfaces.manifests.ManifestsIndex;
import uk.ac.standrews.cs.sos.interfaces.node.NodeType;
import uk.ac.standrews.cs.sos.model.*;
import uk.ac.standrews.cs.sos.services.ManifestsDataService;
import uk.ac.standrews.cs.sos.services.NodeDiscoveryService;
import uk.ac.standrews.cs.sos.utils.Persistence;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.*;

import static uk.ac.standrews.cs.sos.constants.Internals.*;
import static uk.ac.standrews.cs.sos.model.NodesCollectionType.ANY;
import static uk.ac.standrews.cs.sos.model.NodesCollectionType.LOCAL;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSManifestsDataService implements ManifestsDataService {

    private SettingsConfiguration.Settings.AdvanceServicesSettings.DDSSettings ddsSettings;

    // We have three layers for storing and getting manifests:
    // 1. memory cache
    // 2. local disk
    // 3. remote nodes (i.e. other Data Discovery services)
    private ManifestsCache inMemoryCache;
    private LocalManifestsDirectory local;
    private RemoteManifestsDirectory remote;

    // Store info about manifests, tips, invariants, etc
    private ManifestsIndex index;

    // Uses to store/get manifests into the local disk
    private LocalStorage localStorage;

    // Maps ManifestGUID --> [ Nodes that might have it ]
    private ManifestsLocationsIndex manifestsLocationsIndex;

    private IGUID localNode;

    public SOSManifestsDataService(SettingsConfiguration.Settings.AdvanceServicesSettings.DDSSettings ddsSettings, LocalStorage localStorage, NodeDiscoveryService nodeDiscoveryService) {

        this.ddsSettings = ddsSettings;

        this.localStorage = localStorage;

        loadOrCreateCache();
        loadOrCreateManifestsLocationsIndex();
        loadOrCreateIndex();

        local = new LocalManifestsDirectory(localStorage);
        remote = new RemoteManifestsDirectory(manifestsLocationsIndex, nodeDiscoveryService, this);

        localNode = nodeDiscoveryService.getThisNode().guid();
    }

    @Override
    public void addManifest(Manifest manifest) throws ManifestPersistException {

        long start = System.nanoTime();

        inMemoryCache.addManifest(manifest);
        local.addManifest(manifest);

        // Add/Update TIP
        ManifestType manifestType = manifest.getType();
        switch(manifestType) {

            case VERSION:
            {
                Version version = (Version) manifest;
                index.advanceTip(version);

                // Make sure that this node has at least one HEAD, otherwise keep the current head.
                try {
                    getHead(version.invariant());
                } catch (HEADNotFoundException e) {
                    setHead(version);
                }
            }
            break;

            case CONTEXT:
            {
                Context context = (Context) manifest;
                index.advanceTip(context);
            }
            break;

            default:
                // DO NOTHING
                break;
        }

        index.track(manifest);

        long duration = System.nanoTime() - start;
        InstrumentFactory.instance().measure(StatsTYPE.io, StatsTYPE.add_manifest, Integer.toString(manifest.size()), duration);
    }

    @Override
    public void addManifest(Manifest manifest, NodesCollection nodes, int replicationFactor, boolean limitReplication, boolean storeLocally) throws ManifestPersistException {

        if (storeLocally) {
            addManifest(manifest);
        }

        if (limitReplication) {
            replicationFactor = (replicationFactor - 1) <= ddsSettings.getMaxReplication() ? (replicationFactor - 1) : ddsSettings.getMaxReplication();
        }

        if (replicationFactor > 0) {
            remote.addManifest(manifest, nodes, replicationFactor); // This is an async operation
        }

        // TODO - IF MANIFEST IS VERSION - should notify nodes that have PREVIOUS versions. See notebook at page 92
    }

    @Override
    public void addManifestNodeMapping(IGUID manifest, IGUID node) {
        manifestsLocationsIndex.addEntry(manifest, node);
    }

    @Override
    public void delete(IGUID guid) throws ManifestNotFoundException {


        Manifest manifest = getManifest(guid);

        inMemoryCache.delete(guid);
        local.delete(guid);
        index.delete(manifest);
        manifestsLocationsIndex.evictEntry(guid, localNode);
    }

    @Override
    public void deleteLocalLocation(IGUID guid) throws ManifestNotFoundException {

        // TODO
        // update atom manifest both in the cache and in disk. not remotely
        // index
        manifestsLocationsIndex.evictEntry(guid, localNode);
    }

    @Override
    public Manifest getManifest(IGUID guid) throws ManifestNotFoundException {

        return getManifest(guid, NodeType.DDS);
    }

    @Override
    public Manifest getManifest(IGUID guid, NodeType nodeTypeFilter) throws ManifestNotFoundException {

        Manifest manifest;
        try {
            manifest = getManifest(new NodesCollectionImpl(ANY), nodeTypeFilter, guid);
        } catch (NodesCollectionException e) {
            throw new ManifestNotFoundException("Manifest not found");
        }

        return manifest;
    }

    @Override
    public IGUID challenge(IGUID guid, String challenge) {

        try {
            Manifest manifest = getManifest(guid);

            List<InputStream> streams = Arrays.asList(manifest.contentToHash(), new ByteArrayInputStream(challenge.getBytes()));
            InputStream combinedStream = new SequenceInputStream(Collections.enumeration(streams));

            return GUIDFactory.generateGUID(GUID_ALGORITHM, combinedStream);

        } catch (ManifestNotFoundException | IOException | GUIDGenerationException e) {

            return new InvalidID();
        }

    }

    @Override
    public Manifest getManifest(NodesCollection nodes, NodeType nodeTypeFilter, IGUID guid) throws ManifestNotFoundException {

        long start = System.nanoTime();

        if (guid == null || guid.isInvalid()) {
            throw new ManifestNotFoundException("GUID was invalid");
        }

        try {
            Manifest manifest;

            try {
                manifest = inMemoryCache.findManifest(guid);
            } catch (ManifestNotFoundException e) {
                manifest = null;
            }

            if (manifest == null) {

                try {
                    manifest = local.findManifest(guid);
                    if (manifest != null) {
                        inMemoryCache.addManifest(manifest);
                    }
                } catch (ManifestNotFoundException e) {
                    manifest = null;
                }

            }

            if (manifest == null && !nodes.type().equals(LOCAL)) {

                manifest = remote.findManifest(nodes, nodeTypeFilter, guid);
                if (manifest != null) {
                    inMemoryCache.addManifest(manifest);
                    local.addManifest(manifest);
                }
            }

            if (manifest == null) {
                throw new ManifestNotFoundException("Unable to find manifest in inMemoryCache, local, remote. GUID: " + guid.toShortString());
            }

            long duration = System.nanoTime() - start;
            InstrumentFactory.instance().measure(StatsTYPE.io, StatsTYPE.read_manifest, Integer.toString(manifest.size()), duration);


            return manifest;
        } catch (ManifestPersistException e) {
            SOS_LOG.log(LEVEL.ERROR, "MDS - Unable to persist manifest to cache/local");
        }

        throw new ManifestNotFoundException("Manifest not found");
    }

    @Override
    public Set<IGUID> getTips(IGUID invariant) throws TIPNotFoundException {

        return index.getTips(invariant);
    }

    @Override
    public IGUID getHead(IGUID invariant) throws HEADNotFoundException {

        return index.getHead(invariant);
    }

    @Override
    public void setHead(Version version) {

        index.setHead(version);
    }

    @Override
    public Set<IGUID> getVersions(IGUID invariant) {


        try {
            return getVersions(new NodesCollectionImpl(NodesCollectionType.LOCAL), invariant);
        } catch (NodesCollectionException e) {
            return new LinkedHashSet<>();
        }

    }

    @Override
    public Set<IGUID> getVersions(NodesCollection nodesCollection, IGUID invariant) {

        Set<IGUID> versions = new LinkedHashSet<>(index.getVersions(invariant));

        if (!nodesCollection.type().equals(NodesCollectionType.LOCAL)) {

            Set<IGUID> remotes = remote.getVersions(nodesCollection, invariant);
            versions.addAll(remotes);
        }

        return versions;

    }

    @Override
    public Set<IGUID> getManifests(ManifestType type) {

        return index.getManifests(type);
    }

    @Override
    public Set<IGUID> searchVersionableManifests(ManifestType type, List<ManifestParam> params) {

        Set<IGUID> input = index.getManifests(type);
        return local.getManifests(input, params);
    }

    @Override
    public void flush() {

        try {
            IDirectory cacheDir = localStorage.getNodeDirectory();

            IFile cacheFile = localStorage.createFile(cacheDir, MANIFESTS_CACHE_FILE);
            Persistence.persist(inMemoryCache, cacheFile);

            IFile mdsIndexFile = localStorage.createFile(cacheDir, MDS_INDEX_FILE);
            Persistence.persist(manifestsLocationsIndex, mdsIndexFile);

            IFile indexFile = localStorage.createFile(cacheDir, MANIFESTS_INDEX_FILE);
            Persistence.persist(index, indexFile);

        } catch (DataStorageException | IOException e) {
            SOS_LOG.log(LEVEL.ERROR, "Unable to persist the MDS inMemoryCache and/or index");
        }
    }

    @Override
    public void shutdown() {
        inMemoryCache.clear();
        manifestsLocationsIndex.clear();
        index.clear();
    }

    private void loadOrCreateCache() {
        try {
            IDirectory cacheDir = localStorage.getNodeDirectory();
            IFile file = localStorage.createFile(cacheDir, MANIFESTS_CACHE_FILE);
            if (file.exists()) {
                inMemoryCache = ManifestsCacheImpl.load(localStorage, file, localStorage.getManifestsDirectory());
            }
        } catch (DataStorageException | ClassNotFoundException | IOException e) {
            SOS_LOG.log(LEVEL.ERROR, "Unable to load the MDS inMemoryCache");
        }

        if (inMemoryCache == null) {
            inMemoryCache = new ManifestsCacheImpl();
        }
    }

    private void loadOrCreateManifestsLocationsIndex() {
        try {
            IDirectory cacheDir = localStorage.getNodeDirectory();
            IFile file = localStorage.createFile(cacheDir, MDS_INDEX_FILE);
            if (file.exists()) {
                manifestsLocationsIndex = (ManifestsLocationsIndex) Persistence.load(file);
            }
        } catch (DataStorageException | ClassNotFoundException | IOException e) {
            SOS_LOG.log(LEVEL.ERROR, "Unable to load the MDS index");
        }

        if (manifestsLocationsIndex == null) {
            manifestsLocationsIndex = new ManifestsLocationsIndex();
        }
    }

    private void loadOrCreateIndex() {
        try {
            IDirectory cacheDir = localStorage.getNodeDirectory();
            IFile file = localStorage.createFile(cacheDir, MANIFESTS_INDEX_FILE);
            if (file.exists()) {
                index = (ManifestsIndex) Persistence.load(file);
            }
        } catch (DataStorageException | ClassNotFoundException | IOException e) {
            SOS_LOG.log(LEVEL.ERROR, "Unable to load the MDS inMemoryCache");
        }

        if (index == null) {
            index = new ManifestsIndexImpl();
        }
    }

}
