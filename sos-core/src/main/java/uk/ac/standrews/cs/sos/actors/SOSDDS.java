package uk.ac.standrews.cs.sos.actors;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestsCacheMissException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.interfaces.actors.DDS;
import uk.ac.standrews.cs.sos.interfaces.actors.NDS;
import uk.ac.standrews.cs.sos.interfaces.manifests.ManifestsCache;
import uk.ac.standrews.cs.sos.interfaces.manifests.ManifestsDirectory;
import uk.ac.standrews.cs.sos.interfaces.model.Asset;
import uk.ac.standrews.cs.sos.interfaces.model.Manifest;
import uk.ac.standrews.cs.sos.model.manifests.directory.DDSIndex;
import uk.ac.standrews.cs.sos.model.manifests.directory.LocalManifestsDirectory;
import uk.ac.standrews.cs.sos.model.manifests.directory.ManifestsCacheImpl;
import uk.ac.standrews.cs.sos.model.manifests.directory.RemoteManifestsDirectory;
import uk.ac.standrews.cs.sos.storage.LocalStorage;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;
import uk.ac.standrews.cs.storage.interfaces.Directory;
import uk.ac.standrews.cs.storage.interfaces.File;

import java.io.IOException;
import java.util.List;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSDDS implements DDS {

    private static final String CACHE_FILE = "manifests.cache";
    private static final String DDS_INDEX_FILE = "dds.index";

    private ManifestsCache cache;
    private LocalManifestsDirectory local;
    private RemoteManifestsDirectory remote;
    private LocalStorage localStorage;

    private DDSIndex ddsIndex;

    public SOSDDS(LocalStorage localStorage, NDS nds) {
        this.localStorage = localStorage;

        loadOrCreateCache();
        loadOrCreateDDSIndex();

        local = new LocalManifestsDirectory(localStorage);
        remote = new RemoteManifestsDirectory(ddsIndex, nds, this);
    }

    @Override
    public void addManifest(Manifest manifest, boolean recursive) throws ManifestPersistException {
        cache.addManifest(manifest);
        local.addManifest(manifest);
        remote.addManifest(manifest); // will run in async mode
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
    public List<Asset> getAllAssets() {
        // TODO - return only the ones from the cache for the moment, but should be able to differentiate
        return cache.getAllAsset();
    }

    @Override
    public void flush() {

        try {
            Directory cacheDir = localStorage.getCachesDirectory();

            File cacheFile = localStorage.createFile(cacheDir, CACHE_FILE);
            cache.persist(cacheFile);

            File ddsIndexFile = localStorage.createFile(cacheDir, DDS_INDEX_FILE);
            ddsIndex.persist(ddsIndexFile);

        } catch (DataStorageException | IOException e) {
            SOS_LOG.log(LEVEL.ERROR, "Unable to persist the DDS cache and/or index");
        }
    }

    private void loadOrCreateCache() {
        try {
            Directory cacheDir = localStorage.getCachesDirectory();
            File file = localStorage.createFile(cacheDir, CACHE_FILE);
            if (file.exists()) {
                cache = ManifestsCacheImpl.load(localStorage, file, localStorage.getManifestDirectory());
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
            Directory cacheDir = localStorage.getCachesDirectory();
            File file = localStorage.createFile(cacheDir, DDS_INDEX_FILE);
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
