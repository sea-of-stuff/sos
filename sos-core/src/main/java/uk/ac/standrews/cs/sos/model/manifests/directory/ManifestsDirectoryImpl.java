package uk.ac.standrews.cs.sos.model.manifests.directory;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.manifest.*;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.interfaces.actors.DDS;
import uk.ac.standrews.cs.sos.interfaces.actors.NDS;
import uk.ac.standrews.cs.sos.interfaces.manifests.Asset;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;
import uk.ac.standrews.cs.sos.interfaces.manifests.ManifestsCache;
import uk.ac.standrews.cs.sos.interfaces.manifests.ManifestsDirectory;
import uk.ac.standrews.cs.sos.interfaces.policy.ManifestPolicy;
import uk.ac.standrews.cs.sos.storage.LocalStorage;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;
import uk.ac.standrews.cs.storage.interfaces.Directory;
import uk.ac.standrews.cs.storage.interfaces.File;

import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ManifestsDirectoryImpl implements ManifestsDirectory {

    private static final String CACHE_FILE = "manifests.cache";
    private static final String DDS_INDEX_FILE = "dds.index";

    private ManifestsCache cache;
    private LocalManifestsDirectory local;
    private RemoteManifestsDirectory remote;
    private LocalStorage localStorage;

    private DDSIndex ddsIndex;

    public ManifestsDirectoryImpl(ManifestPolicy manifestPolicy, LocalStorage localStorage, NDS nds, DDS dds) {

        this.localStorage = localStorage;

        loadOrCreateCache();
        loadOrCreateDDSIndex();

        local = new LocalManifestsDirectory(manifestPolicy, localStorage);
        remote = new RemoteManifestsDirectory(manifestPolicy, nds, dds);
    }

    @Override
    public void addManifest(Manifest manifest) throws ManifestPersistException {
        cache.addManifest(manifest);
        local.addManifest(manifest);
        remote.addManifest(manifest);
    }

    @Override
    public void addManifestDDSMapping(IGUID manifestGUID, IGUID ddsNodeGUID) {
        ddsIndex.addEntry(manifestGUID, ddsNodeGUID);
    }

    @Override
    public Manifest findManifest(IGUID guid) throws ManifestNotFoundException {

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

        // Make sure manifest is cached and saved locally
        cache.addManifest(manifest);
        try {
            local.addManifest(manifest);
        } catch (ManifestPersistException e) {
            SOS_LOG.log(LEVEL.WARN, "ManifestsDirectory :: Unable to save manifest to local directory");
        }

        return manifest;
    }

    @Override
    public Asset getHEAD(IGUID invariant) throws HEADNotFoundException {
        return local.getHEAD(invariant);
    }

    @Override
    public void setHEAD(IGUID version) throws HEADNotSetException {
        local.setHEAD(version);
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            System.out.println(e.getMessage());
        }
        return manifest;
    }

    private Manifest findManifest(ManifestsDirectory directory, IGUID guid) {
        Manifest manifest = null;
        try {
            manifest = directory.findManifest(guid);
            cache.addManifest(manifest);
        } catch (ManifestNotFoundException e) {
            System.out.println(e.getMessage());
        }

        return manifest;
    }

}
