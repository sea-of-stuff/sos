package uk.ac.standrews.cs.sos.model.manifests.directory;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestsCacheMissException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.interfaces.manifests.ManifestsCache;
import uk.ac.standrews.cs.sos.interfaces.model.Manifest;
import uk.ac.standrews.cs.sos.interfaces.model.ManifestType;
import uk.ac.standrews.cs.sos.interfaces.model.Version;
import uk.ac.standrews.cs.sos.storage.LocalStorage;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;
import uk.ac.standrews.cs.storage.exceptions.PersistenceException;
import uk.ac.standrews.cs.storage.interfaces.Directory;
import uk.ac.standrews.cs.storage.interfaces.File;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

/**
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ManifestsCacheImpl implements ManifestsCache, Serializable {

    // Maximum number of manifests kept in the cache at one time
    private static final int MAX_DEFAULT_SIZE = 1024;

    private int size;
    private transient HashMap<IGUID, Manifest> cache;
    private transient ConcurrentLinkedQueue<IGUID> lru;

    public ManifestsCacheImpl() {
        this(MAX_DEFAULT_SIZE);
    }

    public ManifestsCacheImpl(int size) {
        this.size = size;

        cache = new HashMap<>();
        lru = new ConcurrentLinkedQueue<>();
    }

    @Override
    public synchronized void addManifest(Manifest manifest) {

        IGUID guid = manifest.guid();
        applyLRU(guid);
        cache.put(guid, manifest);
    }

    @Override
    public Manifest getManifest(IGUID guid) throws ManifestsCacheMissException {

        if (cache == null) {
            throw new ManifestsCacheMissException("Cache has not been initialised");
        }

        if (!cache.containsKey(guid)) {
            throw new ManifestsCacheMissException("Unable to find manifest for GUID: " + guid.toString());
        }

        applyReadLRU(guid);

        return cache.get(guid);
    }

    @Override
    public void persist(File file) throws IOException {
        if (!file.exists()) {
            try {
                file.persist();
            } catch (PersistenceException e) {
                throw new IOException(e);
            }
        }

        FileOutputStream ostream = new FileOutputStream(file.toFile());
        ObjectOutputStream p = new ObjectOutputStream(ostream);

        p.writeObject(this);
        p.flush();
        ostream.close();
    }

    @Override
    public ConcurrentLinkedQueue<IGUID> getLRU() {
        return lru;
    }

    @Override
    public List<Version> getAllAsset() {
        return cache.values()
                .stream()
                .filter(m -> m.getType() == ManifestType.VERSION)
                .map(m -> (Version) m)
                .collect(Collectors.toList());
    }

    public static ManifestsCache load(LocalStorage storage, File file, Directory manifestsDir) throws IOException, ClassNotFoundException {

        // Check that file is not empty
        BufferedReader br = new BufferedReader(new FileReader(file.getPathname()));
        if (br.readLine() == null) {
            return null;
        }

        FileInputStream istream = new FileInputStream(file.toFile());
        ObjectInputStream q = new ObjectInputStream(istream);

        ManifestsCache persistedCache = (ManifestsCache)q.readObject();

        ConcurrentLinkedQueue<IGUID> lru = new ConcurrentLinkedQueue<>(persistedCache.getLRU());

        IGUID guid;
        while ((guid = lru.poll()) != null) {
            Manifest manifest = loadManifest(storage, manifestsDir, guid);
            if (manifest != null) {
                persistedCache.addManifest(manifest);
            }
        }

        return persistedCache;
    }

    private void applyLRU(IGUID guid) {
        int currentCacheSize = cache.size();
        if (currentCacheSize >= size) {
            IGUID leastUsedGUID = lru.poll();
            cache.remove(leastUsedGUID);
        }

        applyReadLRU(guid);
    }

    private void applyReadLRU(IGUID guid) {
        lru.remove(guid);
        lru.add(guid);
    }

    private static Manifest loadManifest(LocalStorage storage, Directory manifestsDir, IGUID guid) {
        try {
            File file = ManifestsUtils.ManifestFile(storage, manifestsDir, guid.toString());
            return ManifestsUtils.ManifestFromFile(file);
        } catch (DataStorageException | ManifestNotFoundException e) {
            return null;
        }

    }

    // This method defines how the cache is serialised
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();

        out.writeInt(lru.size());
        for (IGUID guid : lru) {
            out.writeUTF(guid.toString());
        }
    }

    // This method defines how the cache is de-serialised
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        int lruSize = in.readInt();
        lru = new ConcurrentLinkedQueue<>();
        for(int i = 0; i < lruSize; i++) {
            String guid = in.readUTF();
            try {
                lru.add(GUIDFactory.recreateGUID(guid));
            } catch (GUIDGenerationException e) {
                SOS_LOG.log(LEVEL.WARN, "Manifest cache loading - unable to created GUID for entry: " + guid);
            }
        }

        cache = new HashMap<>();
    }
}
