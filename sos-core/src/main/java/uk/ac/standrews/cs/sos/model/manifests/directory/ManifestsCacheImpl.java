package uk.ac.standrews.cs.sos.model.manifests.directory;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestsCacheMissException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;
import uk.ac.standrews.cs.sos.interfaces.manifests.ManifestsCache;
import uk.ac.standrews.cs.sos.storage.LocalStorage;
import uk.ac.standrews.cs.storage.interfaces.Directory;
import uk.ac.standrews.cs.storage.interfaces.File;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ManifestsCacheImpl implements ManifestsCache, Serializable {

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

    public static ManifestsCache load(LocalStorage storage, File file, Directory manifestsDir) throws IOException, ClassNotFoundException {
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
            Manifest manifest = ManifestsUtils.ManifestFromFile(file);

            return  manifest;
        } catch (DataStorageException | ManifestNotFoundException e) {
            return null;
        }

    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();

        out.writeInt(lru.size());
        Iterator<IGUID> it = lru.iterator();
        while (it.hasNext()) {
            IGUID guid = it.next();
            out.writeUTF(guid.toString());
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        int lruSize = in.readInt();
        lru = new ConcurrentLinkedQueue<>();
        for(int i = 0; i < lruSize; i++) {
            try {
                lru.add(GUIDFactory.recreateGUID(in.readUTF()));
            } catch (GUIDGenerationException e) {
                e.printStackTrace();
            }
        }

        cache = new HashMap<>();
    }
}