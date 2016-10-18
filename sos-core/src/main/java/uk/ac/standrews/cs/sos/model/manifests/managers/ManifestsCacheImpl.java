package uk.ac.standrews.cs.sos.model.manifests.managers;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestsCacheMissException;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;
import uk.ac.standrews.cs.sos.interfaces.manifests.ManifestsCache;

import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * TODO - make it serializable, same for guid, manifest?
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ManifestsCacheImpl implements ManifestsCache, Serializable {

    private int MAX_SIZE = 10000;
    private HashMap<IGUID, Manifest> cache;
    private ConcurrentLinkedQueue<IGUID> lru;


    public ManifestsCacheImpl() {
        cache = new HashMap<>();
        lru = new ConcurrentLinkedQueue<>();
    }

    @Override
    public synchronized void addManifest(Manifest manifest) {

        IGUID guid = manifest.getContentGUID();
        applyLRU(guid);
        cache.put(guid, manifest);
    }

    @Override
    public Manifest getManifest(IGUID guid) throws ManifestsCacheMissException {

        if (!cache.containsKey(guid)) {
            throw new ManifestsCacheMissException("Unable to find manifest for GUID: " + guid.toString());
        }

        applyReadLRU(guid);

        return cache.get(guid);
    }

    private void applyLRU(IGUID guid) {
        int currentCacheSize = cache.size();
        if (currentCacheSize > MAX_SIZE) {
            IGUID leastUsedGUID = lru.poll();
            cache.remove(leastUsedGUID);
        }

        applyReadLRU(guid);
    }

    private void applyReadLRU(IGUID guid) {
        lru.remove(guid);
        lru.add(guid);
    }
}
