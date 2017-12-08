package uk.ac.standrews.cs.sos.impl.datamodel.directory;

import uk.ac.standrews.cs.castore.interfaces.IDirectory;
import uk.ac.standrews.cs.castore.interfaces.IFile;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.impl.utils.LRU_GUID;
import uk.ac.standrews.cs.sos.interfaces.manifests.ManifestsCache;
import uk.ac.standrews.cs.sos.model.Atom;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.model.SecureAtom;
import uk.ac.standrews.cs.sos.utils.FileUtils;
import uk.ac.standrews.cs.sos.utils.Persistence;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ManifestsCacheImpl extends AbstractManifestsDirectory implements ManifestsCache, Serializable {

    private transient HashMap<IGUID, Manifest> cache;
    private transient LRU_GUID lru;

    public ManifestsCacheImpl() {
        cache = new HashMap<>();
        lru = new LRU_GUID();
    }

    public ManifestsCacheImpl(int size) {
        cache = new HashMap<>();
        lru = new LRU_GUID(size);
    }

    @Override
    public synchronized void addManifest(Manifest manifest) {

        IGUID guid = manifest.guid();

        IGUID guidToRemove = lru.applyLRU(guid);
        if (!guidToRemove.isInvalid()) {
            cache.remove(guidToRemove);
        }

        if (manifest.getType().equals(ManifestType.ATOM)) {

            // Check if there is already an atom in the cache.
            try {
                Atom retrievedManifest = (Atom) findManifest(guid);
                manifest = mergeManifests(guid, (Atom) manifest, retrievedManifest);

            } catch (ManifestNotFoundException e) {
                // DO NOTHING
            }
        } else if (manifest.getType().equals(ManifestType.ATOM_PROTECTED)) {

            // Check if there is already an atom in the cache.
            try {
                SecureAtom retrievedManifest = (SecureAtom) findManifest(guid);
                manifest = mergeManifests(guid, (SecureAtom) manifest, retrievedManifest);

            } catch (ManifestNotFoundException e) {
                // DO NOTHING
            }
        }

        cache.put(guid, manifest);
    }

    @Override
    public Manifest findManifest(IGUID guid) throws ManifestNotFoundException {

        if (cache == null) {
            throw new ManifestNotFoundException("Cache has not been initialised");
        }

        if (!cache.containsKey(guid)) {
            throw new ManifestNotFoundException("Unable to find manifest for GUID: " + guid.toShortString() + " in the cache");
        }

        lru.applyReadLRU(guid);

        return cache.get(guid);
    }

    @Override
    public void flush() {
        // NOTE: This method is not implemented, as we use the persist method to actually flush the cache
    }

    @Override
    public LRU_GUID getLRU() {
        return lru;
    }

    @Override
    public void clear() {

        cache = new HashMap<>();
        lru = new LRU_GUID();
    }

    public static ManifestsCache load(LocalStorage storage, IFile file, IDirectory manifestsDir) throws IOException, ClassNotFoundException {

        ManifestsCache persistedCache = (ManifestsCache) Persistence.Load(file);

        if (persistedCache == null) throw new ClassNotFoundException();
        if (persistedCache.getLRU() == null) return persistedCache;

        // Reload manifests this way rather than through serialization.
        // Make a copy of the queue, so that the LRU queue is not changed by the poll() method
        ConcurrentLinkedQueue<IGUID> lruQueue = new  ConcurrentLinkedQueue<>(persistedCache.getLRU().getQueue());
        IGUID guid;
        while ((guid = lruQueue.poll()) != null) {
            Manifest manifest = loadManifest(storage, manifestsDir, guid);
            if (manifest != null) {
                try {
                    persistedCache.addManifest(manifest);
                } catch (ManifestPersistException e) {
                    throw new IOException("Unable to load manifest correctly " + manifest);
                }
            }
        }

        return persistedCache;
    }

    private static Manifest loadManifest(LocalStorage storage, IDirectory manifestsDir, IGUID guid) {
        try {
            IFile fileRef = FileUtils.CreateFile(storage, manifestsDir, guid.toMultiHash(), FileUtils.JSON_EXTENSION);
            return FileUtils.ManifestFromFile(fileRef);
        } catch (DataStorageException | ManifestNotFoundException e) {
            return null;
        }

    }

    // This method defines how the cache is serialised
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();

        out.writeObject(lru);
    }

    // This method defines how the cache is de-serialised
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        lru = (LRU_GUID) in.readObject();
        cache = new HashMap<>();
    }
}
