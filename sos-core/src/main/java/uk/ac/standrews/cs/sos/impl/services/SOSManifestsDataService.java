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
import uk.ac.standrews.cs.sos.impl.protocol.TasksQueue;
import uk.ac.standrews.cs.sos.impl.protocol.tasks.ManifestDeletion;
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
import static uk.ac.standrews.cs.sos.model.NodesCollectionType.*;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSManifestsDataService implements ManifestsDataService {

    private SettingsConfiguration.Settings.AdvanceServicesSettings.MDSSettings mdsSettings;

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

    private final IGUID localNode;
    private final NodeDiscoveryService nodeDiscoveryService;

    public SOSManifestsDataService(SettingsConfiguration.Settings.AdvanceServicesSettings.MDSSettings mdsSettings, LocalStorage localStorage, NodeDiscoveryService nodeDiscoveryService) {

        this.mdsSettings = mdsSettings;

        this.localStorage = localStorage;

        loadOrCreateCache();
        loadOrCreateManifestsLocationsIndex();
        loadOrCreateIndex();

        local = new LocalManifestsDirectory(localStorage);
        remote = new RemoteManifestsDirectory(manifestsLocationsIndex, nodeDiscoveryService, this);

        this.nodeDiscoveryService = nodeDiscoveryService;
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
    public void addManifest(Manifest manifest, boolean storeLocally, NodesCollection nodes, int replicationFactor, boolean limitReplication) throws ManifestPersistException {

        if (storeLocally) {
            addManifest(manifest);
        }

        if (limitReplication) {
            replicationFactor = replicationFactor <= mdsSettings.getMaxReplication() ? replicationFactor : mdsSettings.getMaxReplication();
        }

        if (replicationFactor > 0) {
            remote.addManifest(manifest, nodes, replicationFactor); // This is an async operation
        }

        // TODO - IF MANIFEST IS VERSION - should notify nodes that have PREVIOUS versions. See notebook at page 92
    }

    @Override
    public void addManifestNodeMapping(IGUID manifest, IGUID mdsNode) {
        manifestsLocationsIndex.addEntry(manifest, mdsNode);
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
    public void delete(IGUID guid, NodesCollection nodesCollection, boolean localyCopy) throws ManifestNotFoundException {

        Manifest manifest = getManifest(guid);

        if (localyCopy) {
            delete(guid);
        }

        if (nodesCollection.type() == SPECIFIED) {

            ManifestDeletion manifestDeletion = new ManifestDeletion(nodeDiscoveryService, nodesCollection, manifest);
            TasksQueue.instance().performAsyncTask(manifestDeletion);
        }
    }

    @Override
    public void deleteLocalLocation(IGUID guid) {

        // TODO - specify what location to delete (by node guid) or all locations

        // TODO
        // update atom manifest both in the cache and in disk. not remotely
        manifestsLocationsIndex.evictEntry(guid, localNode);
    }

    @Override
    public Manifest getManifest(IGUID guid) throws ManifestNotFoundException {

        return getManifest(guid, NodeType.MDS);
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

    /**
     *
     * Examples:
     *
     * guid-version/guid-atom
     * guid-version/guid-compound
     * guid-version/guid-compound/guid-atom
     * guid-version/guid-compound/label-atom
     * guid-version/guid-compound/label-compound
     * guid-compound/label-atom
     * guid-compound/label-compound
     * guid-compound/label-version
     * guid-version/guid-compound/label-compound/label-compound/label-atom
     *
     * TODO - similar method for protected entities?
     *
     * @param path of the form guid/guid/guid or guid/label/label or a mix
     * @return manifest matching the path
     * @throws ManifestNotFoundException if manifest could not be found
     */
    @Override
    public Manifest resolvePath(String path) throws ManifestNotFoundException {

        Manifest current = null;
        String[] parts = path.split("/");
        for(int i = 0; i < parts.length; i++) {
            String part = parts[i];

            try {
                IGUID manifestGUID = GUIDFactory.recreateGUID(part);
                if (current == null) {
                    current = getManifest(manifestGUID);
                } else {

                    if (current.getType() == ManifestType.COMPOUND) {
                        Compound compound = (Compound) current;

                        Content content = compound.getContent(manifestGUID);
                        if (content != null) {
                            IGUID contentGUID = content.getGUID();
                            current = getManifest(contentGUID);
                        } // else skip

                    } else if (current.getType() == ManifestType.VERSION) {
                        Version version = (Version) current;
                        if (!manifestGUID.equals(version.content())) {
                            manifestGUID = version.content();
                            i--;
                        }

                        current = getManifest(manifestGUID);
                    }

                }

            } catch (GUIDGenerationException e) {

                if (current != null) {

                    if (current.getType() == ManifestType.COMPOUND) {
                        Compound compound = (Compound) current;

                        IGUID contentGUID = compound.getContent(part).getGUID();
                        current = getManifest(contentGUID);
                    } else if (current.getType() == ManifestType.VERSION) {

                        Version version = (Version) current;
                        current = getManifest(version.content());
                        i--;
                    }

                } else {
                    throw new ManifestNotFoundException("Unable to resolve path: " + path);
                }
            }

        }

        if (current == null) {
            throw new ManifestNotFoundException("Unable to resolve path: " + path);
        }

        return current;
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
