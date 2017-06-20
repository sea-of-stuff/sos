package uk.ac.standrews.cs.sos.impl.actors;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.castore.interfaces.IDirectory;
import uk.ac.standrews.cs.castore.interfaces.IFile;
import uk.ac.standrews.cs.sos.actors.DataDiscoveryService;
import uk.ac.standrews.cs.sos.actors.NodeDiscoveryService;
import uk.ac.standrews.cs.sos.exceptions.manifest.HEADNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.exceptions.manifest.TIPNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.manifests.directory.DDSIndex;
import uk.ac.standrews.cs.sos.impl.manifests.directory.LocalManifestsDirectory;
import uk.ac.standrews.cs.sos.impl.manifests.directory.ManifestsCacheImpl;
import uk.ac.standrews.cs.sos.impl.manifests.directory.RemoteManifestsDirectory;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.interfaces.manifests.ManifestsCache;
import uk.ac.standrews.cs.sos.interfaces.manifests.ManifestsDirectory;
import uk.ac.standrews.cs.sos.model.*;
import uk.ac.standrews.cs.sos.utils.Persistence;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.IOException;
import java.util.Set;

import static uk.ac.standrews.cs.sos.constants.Internals.CACHE_FILE;
import static uk.ac.standrews.cs.sos.constants.Internals.DDS_INDEX_FILE;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSDataDiscoveryService implements DataDiscoveryService {

    // We have three layers for storing and getting manifests:
    // 1. memory cache
    // 2. local disk
    // 3. remote nodes (i.e. other Data Discovery services)
    private ManifestsCache inMemoryCache;
    private LocalManifestsDirectory local;
    private RemoteManifestsDirectory remote;

    // Uses to store/get manifests into the local disk
    private LocalStorage localStorage;

    // Maps ManifestGUID --> [ DDS Nodes that might have it ]
    private DDSIndex ddsIndex;

    public SOSDataDiscoveryService(LocalStorage localStorage, NodeDiscoveryService nodeDiscoveryService) {
        this.localStorage = localStorage;

        loadOrCreateCache();
        loadOrCreateDDSIndex();

        local = new LocalManifestsDirectory(localStorage);
        remote = new RemoteManifestsDirectory(ddsIndex, nodeDiscoveryService, this);
    }

    @Override
    public void addManifest(Manifest manifest) throws ManifestPersistException {
        inMemoryCache.addManifest(manifest);
        local.addManifest(manifest);

        if (manifest.getType() == ManifestType.VERSION) {

            Version version = (Version) manifest;
            Set<IGUID> previousVersions = version.getPreviousVersions();
            if (previousVersions == null || previousVersions.isEmpty()) {

                inMemoryCache.advanceTip(version);
                local.advanceTip(version);
            } else {

                inMemoryCache.advanceTip(version);
                local.advanceTip(version);
            }
        }
    }

    @Override
    public void addManifest(Manifest manifest, NodesCollection nodes, int replication) throws ManifestPersistException {
        addManifest(manifest);

        // TODO - is a manifest replicated to a remote node based on what? based on a context? or something else?
        // TODO - should this be dealt (1) within a scope and (2) by contexts?
        remote.addManifest(manifest); // will apply in async mode

        // TODO - IF MANIFEST IS VERSION - should notify nodes that have PREVIOUS versions. See notebook a page 92
    }

    @Override
    public void addManifestDDSMapping(IGUID manifest, IGUID ddsNode) {
        ddsIndex.addEntry(manifest, ddsNode);
    }

    @Override
    public Manifest getManifest(IGUID guid) throws ManifestNotFoundException {

        if (guid == null || guid.isInvalid()) {
            throw new ManifestNotFoundException("GUID was invalid");
        }

        // TODO - improve logic about manifest being added to directories if missing
        try {
            Manifest manifest = findManifest(inMemoryCache, guid);
            if (manifest == null) {
                manifest = findManifest(local, guid);

                if (manifest != null) inMemoryCache.addManifest(manifest);
            }
            if (manifest == null) {
                manifest = findManifest(remote, guid);

                if (manifest != null) {
                    inMemoryCache.addManifest(manifest);
                    local.addManifest(manifest);
                }
            }
            if (manifest == null) {
                throw new ManifestNotFoundException("Unable to find manifest in inMemoryCache, local, remote. GUID: " + guid.toString());
            }

            return manifest;
        } catch (ManifestPersistException e) {
            e.printStackTrace();
        }

        throw new ManifestNotFoundException("Manifest not found");
    }

    @Override
    public Set<IGUID> getAllAssets() {

        // NOTE - returning only the ones from the inMemoryCache for the moment
        return inMemoryCache.getAllAssets();
    }

    @Override
    public Set<IGUID> getTips(IGUID invariant) throws TIPNotFoundException {

        try {

            return inMemoryCache.getTips(invariant);

        } catch (TIPNotFoundException e) {

            return local.getTips(invariant);
        }

    }

    @Override
    public IGUID getHead(Role role, IGUID invariant) throws HEADNotFoundException {

        try {

            return inMemoryCache.getHead(role, invariant);

        } catch (HEADNotFoundException e) {

            return local.getHead(role, invariant);
        }

    }

    @Override
    public void setHead(Role role, Version version) {

        inMemoryCache.setHead(role, version);
        local.setHead(role, version);
    }

    @Override
    public void flush() {

        try {
            IDirectory cacheDir = localStorage.getNodeDirectory();

            IFile cacheFile = localStorage.createFile(cacheDir, CACHE_FILE);
            Persistence.Persist(inMemoryCache, cacheFile);

            IFile ddsIndexFile = localStorage.createFile(cacheDir, DDS_INDEX_FILE);
            Persistence.Persist(ddsIndex, ddsIndexFile);

        } catch (DataStorageException | IOException e) {
            SOS_LOG.log(LEVEL.ERROR, "Unable to persist the DDS inMemoryCache and/or index");
        }
    }

    private void loadOrCreateCache() {
        try {
            IDirectory cacheDir = localStorage.getNodeDirectory();
            IFile file = localStorage.createFile(cacheDir, CACHE_FILE);
            if (file.exists()) {
                inMemoryCache = ManifestsCacheImpl.load(localStorage, file, localStorage.getManifestsDirectory());
            }
        } catch (DataStorageException | ClassNotFoundException | IOException e) {
            SOS_LOG.log(LEVEL.ERROR, "Unable to load the DDS inMemoryCache");
        }

        if (inMemoryCache == null) {
            inMemoryCache = new ManifestsCacheImpl();
        }
    }

    private void loadOrCreateDDSIndex() {
        try {
            IDirectory cacheDir = localStorage.getNodeDirectory();
            IFile file = localStorage.createFile(cacheDir, DDS_INDEX_FILE);
            if (file.exists()) {
                ddsIndex = (DDSIndex) Persistence.Load(file);
            }
        } catch (DataStorageException | ClassNotFoundException | IOException e) {
            SOS_LOG.log(LEVEL.ERROR, "Unable to load the DDS index");
        }

        if (ddsIndex == null) {
            ddsIndex = new DDSIndex();
        }
    }

    private Manifest findManifest(ManifestsDirectory directory, IGUID guid) {
        Manifest manifest = null;
        try {
            manifest = directory.findManifest(guid);

        } catch (ManifestNotFoundException e) {
            SOS_LOG.log(LEVEL.WARN, e.getMessage());
        }

        return manifest;
    }
}