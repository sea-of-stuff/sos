package uk.ac.standrews.cs.sos.model.cache;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.interfaces.locations.Location;
import uk.ac.standrews.cs.sos.model.SeaConfiguration;
import uk.ac.standrews.cs.sos.model.locations.bundles.CacheLocationBundle;

import java.io.InputStream;

/**
 * Cache an atom - given its location or data stream - to this SOSNodeManager node.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class CacheManager {

    private final Cache cache;

    public CacheManager(SeaConfiguration configuration,
                        Location origin) {
        cache = new LocationCache(configuration, origin);
    }

    public CacheManager(SeaConfiguration configuration,
                        InputStream inputStream) {
        cache = new StreamCache(configuration, inputStream);
    }

    /**
     * Store the data at the Location Bundles in the cache
     *
     * @return GUID generated from the data at the location bundles
     * @throws DataStorageException
     */
    public IGUID cacheAtom() throws DataStorageException {
        return cache.cache();
    }

    public CacheLocationBundle getCacheLocationBundle() {
        return cache.getCacheLocationBundle();
    }

}
