package uk.ac.standrews.cs.sos.impl.actors;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.castore.interfaces.IDirectory;
import uk.ac.standrews.cs.castore.interfaces.IFile;
import uk.ac.standrews.cs.sos.actors.DataDiscoveryService;
import uk.ac.standrews.cs.sos.actors.NodeDiscoveryService;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestsCacheMissException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.manifests.directory.DDSIndex;
import uk.ac.standrews.cs.sos.impl.manifests.directory.LocalManifestsDirectory;
import uk.ac.standrews.cs.sos.impl.manifests.directory.ManifestsCacheImpl;
import uk.ac.standrews.cs.sos.impl.manifests.directory.RemoteManifestsDirectory;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.interfaces.manifests.ManifestsCache;
import uk.ac.standrews.cs.sos.interfaces.manifests.ManifestsDirectory;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.Version;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static uk.ac.standrews.cs.sos.constants.Internals.CACHE_FILE;
import static uk.ac.standrews.cs.sos.constants.Internals.DDS_INDEX_FILE;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSDataDiscoveryService implements DataDiscoveryService {

    private ManifestsCache cache;
    private LocalManifestsDirectory local;
    private RemoteManifestsDirectory remote;
    private LocalStorage localStorage;

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
        cache.addManifest(manifest);
        local.addManifest(manifest);

        // TODO - is a manifest replicated to a remote node based on what? based on a context? or something else?
        // TODO - should this be dealt (1) within a scope and (2) by contexts?
        remote.addManifest(manifest); // will apply in async mode
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

        Manifest manifest = findManifestCache(guid);
        if (manifest == null) {
            manifest = findManifest(local, guid);
        }
        if (manifest == null) {
            manifest = findManifest(remote, guid);
        }
        if (manifest == null) {
            throw new ManifestNotFoundException("Unable to find manifest in cache, local, remote. GUID: " + guid.toString());
        }

        return manifest;
    }

    @Override
    public Set<Version> getAllVersions() {

        // TODO - returning only the ones from the cache for the moment
        return new HashSet<>(cache.getAllAsset());
    }

    @Override
    public void flush() {

        try {
            IDirectory cacheDir = localStorage.getNodeDirectory();

            IFile cacheFile = localStorage.createFile(cacheDir, CACHE_FILE);
            cache.persist(cacheFile);

            IFile ddsIndexFile = localStorage.createFile(cacheDir, DDS_INDEX_FILE);
            ddsIndex.persist(ddsIndexFile);

        } catch (DataStorageException | IOException e) {
            SOS_LOG.log(LEVEL.ERROR, "Unable to persist the DDS cache and/or index");
        }
    }

    private void loadOrCreateCache() {
        try {
            IDirectory cacheDir = localStorage.getNodeDirectory();
            IFile file = localStorage.createFile(cacheDir, CACHE_FILE);
            if (file.exists()) {
                cache = ManifestsCacheImpl.load(localStorage, file, localStorage.getManifestsDirectory());
            }
        } catch (DataStorageException | ClassNotFoundException | IOException e) {
            SOS_LOG.log(LEVEL.ERROR, "Unable to load the DDS cache");
        }

        if (cache == null) {
            cache = new ManifestsCacheImpl();
        }
    }

    private void loadOrCreateDDSIndex() {
        try {
            IDirectory cacheDir = localStorage.getNodeDirectory();
            IFile file = localStorage.createFile(cacheDir, DDS_INDEX_FILE);
            if (file.exists()) {
                ddsIndex = DDSIndex.load(file);
            }
        } catch (DataStorageException | ClassNotFoundException | IOException e) {
            SOS_LOG.log(LEVEL.ERROR, "Unable to load the DDS index");
        }

        if (ddsIndex == null) {
            ddsIndex = new DDSIndex();
        }
    }

    private Manifest findManifestCache(IGUID guid) {
        Manifest manifest = null;
        try {
            manifest = cache.getManifest(guid);
        } catch (ManifestsCacheMissException e) {
            SOS_LOG.log(LEVEL.WARN, e.getMessage());
        }
        return manifest;
    }

    private Manifest findManifest(ManifestsDirectory directory, IGUID guid) {
        Manifest manifest = null;
        try {
            manifest = directory.findManifest(guid);

            // Manifest is cached and saved to local disk for faster access in the future
            cache.addManifest(manifest);
            local.addManifest(manifest);

        } catch (ManifestNotFoundException e) {
            SOS_LOG.log(LEVEL.WARN, e.getMessage());
        } catch (ManifestPersistException e) {
            SOS_LOG.log(LEVEL.WARN, "ManifestsDirectory :: Unable to save manifest to local directory");
        }

        return manifest;
    }
}
