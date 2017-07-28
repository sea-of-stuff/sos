package uk.ac.standrews.cs.sos.impl.manifests.directory;

import uk.ac.standrews.cs.castore.interfaces.IDirectory;
import uk.ac.standrews.cs.castore.interfaces.IFile;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.manifest.HEADNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.exceptions.manifest.TIPNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.interfaces.manifests.ManifestsCache;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.model.Version;
import uk.ac.standrews.cs.sos.utils.FileUtils;
import uk.ac.standrews.cs.sos.utils.Persistence;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

/**
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ManifestsCacheImpl extends AbstractManifestsDirectory implements ManifestsCache, Serializable {

    // Maximum number of manifests kept in the cache at one time
    private static final int MAX_DEFAULT_SIZE = 1024;

    private int size;
    private transient HashMap<IGUID, Manifest> cache;
    private transient ConcurrentLinkedQueue<IGUID> lru;

    private transient HashMap<IGUID, Set<IGUID>> tips;

    // Invariant --> Role --> HEAD
    private transient HashMap<IGUID, IGUID> heads;

    public ManifestsCacheImpl() {
        this(MAX_DEFAULT_SIZE);
    }

    public ManifestsCacheImpl(int size) {
        this.size = size;

        cache = new HashMap<>();
        lru = new ConcurrentLinkedQueue<>();

        tips = new HashMap<>();
        heads = new HashMap<>();
    }

    @Override
    public synchronized void addManifest(Manifest manifest) throws ManifestPersistException {

        IGUID guid = manifest.guid();
        applyLRU(guid);
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

        applyReadLRU(guid);

        return cache.get(guid);
    }

    @Override
    public Set<IGUID> getTips(IGUID invariant) throws TIPNotFoundException {

        if (tips.containsKey(invariant)) {
            return tips.get(invariant);
        }

        throw new TIPNotFoundException();
    }

    @Override
    public IGUID getHead(IGUID invariant) throws HEADNotFoundException {

        if (heads.containsKey(invariant)) {
            return heads.get(invariant);
        }

        throw new HEADNotFoundException();
    }

    @Override
    public void setHead(Version version) {

        IGUID invariantGUID = version.getInvariantGUID();
        IGUID versionGUID = version.guid();

        heads.put(invariantGUID, versionGUID);
    }

    @Override
    public void flush() {
        // NOTE: This method is not implemented, as we use the persist method to actually flush the cache
    }

    @Override
    public ConcurrentLinkedQueue<IGUID> getLRU() {
        return lru;
    }

    @Override
    public Set<IGUID> getAllAssets() {
        return cache.values()
                .stream()
                .filter(m -> m.getType() == ManifestType.VERSION)
                .map(m -> ((Version) m).getInvariantGUID())
                .distinct()
                .collect(Collectors.toSet());
    }

    public void advanceTip(IGUID invariant, IGUID version) {

        if (!tips.containsKey(invariant)) {
            tips.put(invariant, new LinkedHashSet<>());
        }

        tips.get(invariant).add(version);
    }

    public void advanceTip(IGUID invariant, Set<IGUID> previousVersions, IGUID newVersion) {

        if (tips.containsKey(invariant) && tips.get(invariant).containsAll(previousVersions)) {

            advanceTip(invariant, newVersion);
            tips.get(invariant).removeAll(previousVersions);
        }
    }

    public static ManifestsCache load(LocalStorage storage, IFile file, IDirectory manifestsDir) throws IOException, ClassNotFoundException {

        ManifestsCache persistedCache = (ManifestsCache) Persistence.Load(file);

        if (persistedCache == null) throw new ClassNotFoundException();
        if (persistedCache.getLRU() == null) return persistedCache;

        // Re-build cache
        ConcurrentLinkedQueue<IGUID> lru = new ConcurrentLinkedQueue<>(persistedCache.getLRU());

        IGUID guid;
        while ((guid = lru.poll()) != null) {
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

    private static Manifest loadManifest(LocalStorage storage, IDirectory manifestsDir, IGUID guid) {
        try {
            IFile file = FileUtils.CreateFile(storage, manifestsDir, guid.toString(), FileUtils.JSON_EXTENSION);
            return FileUtils.ManifestFromFile(file);
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

        // TODO - tips and heads should be stored to disk?
        tips = new HashMap<>();
        heads = new HashMap<>();
    }
}
