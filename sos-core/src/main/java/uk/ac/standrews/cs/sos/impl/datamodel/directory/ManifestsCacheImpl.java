package uk.ac.standrews.cs.sos.impl.datamodel.directory;

import uk.ac.standrews.cs.castore.interfaces.IDirectory;
import uk.ac.standrews.cs.castore.interfaces.IFile;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.manifest.*;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.interfaces.manifests.ManifestsCache;
import uk.ac.standrews.cs.sos.model.*;
import uk.ac.standrews.cs.sos.utils.FileUtils;
import uk.ac.standrews.cs.sos.utils.Persistence;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

/**
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ManifestsCacheImpl extends AbstractManifestsDirectory implements ManifestsCache, Serializable {

    // Maximum number of manifests kept in the cache at one time
    private static final int MAX_DEFAULT_SIZE = 8192;

    private int size;
    private transient HashMap<IGUID, Manifest> cache;
    private transient ConcurrentLinkedQueue<IGUID> lru;

    private transient HashMap<IGUID, Set<IGUID>> tips;
    private transient HashMap<IGUID, IGUID> heads;
    private transient HashMap<IGUID, Set<IGUID>> assetsToVersions; // tracks all assetsToVersions for the invariants

    public ManifestsCacheImpl() {
        this(MAX_DEFAULT_SIZE);
    }

    public ManifestsCacheImpl(int size) {
        this.size = size;

        cache = new HashMap<>();
        lru = new ConcurrentLinkedQueue<>();

        tips = new HashMap<>();
        heads = new HashMap<>();
        assetsToVersions = new HashMap<>();
    }

    @Override
    public synchronized void addManifest(Manifest manifest) throws ManifestPersistException {

        IGUID guid = manifest.guid();
        applyLRU(guid);

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

            } catch (ManifestNotFoundException | ManifestNotMadeException e) {
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

        applyReadLRU(guid);

        return cache.get(guid);
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

    @Override
    public Set<IGUID> getVersions(IGUID invariant) {

        if (assetsToVersions.containsKey(invariant)) {
            return assetsToVersions.get(invariant);
        } else {
            return new LinkedHashSet<>();
        }
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

        // Update the [invariant --> [version]] map
        if (!assetsToVersions.containsKey(invariantGUID)) {
            assetsToVersions.put(invariantGUID, new LinkedHashSet<>());
        }
        assetsToVersions.get(invariantGUID).add(versionGUID);
    }

    @Override
    public void advanceTip(Version version) {

        Set<IGUID> previousVersions = version.getPreviousVersions();

        if (previousVersions == null || previousVersions.isEmpty()) {
            advanceTip(version.getInvariantGUID(), version.guid());
        } else {
            advanceTip(version.getInvariantGUID(), version.getPreviousVersions(), version.guid());
        }

    }

    @Override
    public void advanceTip(Context context) {

        IGUID previous = context.previous();
        if (previous == null || previous.isInvalid()) {
            advanceTip(context.invariant(), context.guid());
        } else {
            Set<IGUID> previousVersions = new LinkedHashSet<>();
            previousVersions.add(previous);
            advanceTip(context.invariant(), previousVersions, context.guid());
        }
    }

    private void advanceTip(IGUID invariant, IGUID version) {

        if (!tips.containsKey(invariant)) {
            tips.put(invariant, new LinkedHashSet<>());
        }

        tips.get(invariant).add(version);
    }

    private void advanceTip(IGUID invariant, Set<IGUID> previousVersions, IGUID newVersion) {

        if (tips.containsKey(invariant) && tips.get(invariant).containsAll(previousVersions)) {

            advanceTip(invariant, newVersion);
            tips.get(invariant).removeAll(previousVersions); // Remove the previous tips, which are now replaced by the newVersion
        } else {
            // This is the case when we are adding a tip to a new branch.
            advanceTip(invariant, newVersion);
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
            IFile fileRef = FileUtils.CreateFile(storage, manifestsDir, guid.toMultiHash(), FileUtils.JSON_EXTENSION);
            return FileUtils.ManifestFromFile(fileRef);
        } catch (DataStorageException | ManifestNotFoundException e) {
            return null;
        }

    }

    // This method defines how the cache is serialised
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();

        out.writeInt(lru.size());
        for (IGUID guid : lru) {
            out.writeUTF(guid.toMultiHash());
        }

        out.writeInt(tips.size());
        for(Map.Entry<IGUID, Set<IGUID>> tip : tips.entrySet()) {
            out.writeUTF(tip.getKey().toMultiHash());
            out.writeInt(tip.getValue().size());

            for(IGUID t:tip.getValue()) {
                out.writeUTF(t.toMultiHash());
            }
        }

        out.writeInt(heads.size());
        for(Map.Entry<IGUID, IGUID> head : heads.entrySet()) {
            out.writeUTF(head.getKey().toMultiHash());
            out.writeUTF(head.getValue().toMultiHash());
        }

        out.writeInt(assetsToVersions.size());
        for(Map.Entry<IGUID, Set<IGUID>> versions : assetsToVersions.entrySet()) {
            out.writeUTF(versions.getKey().toMultiHash());
            out.writeInt(versions.getValue().size()); // Number of versions per invariant

            for(IGUID version:versions.getValue()) {
                out.writeUTF(version.toMultiHash());
            }
        }
    }

    // This method defines how the cache is de-serialised
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        try {
            int lruSize = in.readInt();
            lru = new ConcurrentLinkedQueue<>();
            for (int i = 0; i < lruSize; i++) {
                String guid = in.readUTF();
                lru.add(GUIDFactory.recreateGUID(guid));
            }

            cache = new HashMap<>();

            tips = new HashMap<>();
            int tipsSize = in.readInt();
            for (int i = 0; i < tipsSize; i++) {
                IGUID invariant = GUIDFactory.recreateGUID(in.readUTF());
                tips.put(invariant, new LinkedHashSet<>());

                int numberOfTipsPerInvariant = in.readInt();
                for (int j = 0; j < numberOfTipsPerInvariant; j++) {
                    String version = in.readUTF();
                    tips.get(invariant).add(GUIDFactory.recreateGUID(version));
                }
            }

            heads = new HashMap<>();
            int headsSize = in.readInt();
            for(int i = 0; i < headsSize; i++) {
                IGUID invariant = GUIDFactory.recreateGUID(in.readUTF());
                IGUID version = GUIDFactory.recreateGUID(in.readUTF());
                heads.put(invariant, version);
            }

            assetsToVersions = new HashMap<>();
            int assetsToVersionsSize = in.readInt();
            for (int i = 0; i < assetsToVersionsSize; i++) {
                IGUID invariant = GUIDFactory.recreateGUID(in.readUTF());
                assetsToVersions.put(invariant, new LinkedHashSet<>());

                int numberOfVersionsPerInvariant = in.readInt();
                for (int j = 0; j < numberOfVersionsPerInvariant; j++) {
                    String version = in.readUTF();
                    assetsToVersions.get(invariant).add(GUIDFactory.recreateGUID(version));
                }
            }

        } catch (GUIDGenerationException e) {
            SOS_LOG.log(LEVEL.WARN, "Manifest cache loading - unable to recreated some of the GUIDs");
        }
    }
}
