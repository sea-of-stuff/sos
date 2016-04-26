package uk.ac.standrews.cs.sos.model.storage;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.SourceLocationException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.interfaces.locations.Location;
import uk.ac.standrews.cs.sos.model.SeaConfiguration;
import uk.ac.standrews.cs.sos.model.cache.CacheManager;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class DataStorageHelper {

    public static InputStream getInputStreamFromLocation(Location location) throws SourceLocationException {
        InputStream stream;
        try {
            stream = location.getSource();
        } catch (IOException e) {
            throw new SourceLocationException("CacheManager " + location.toString() + " " + e);
        }

        return stream;
    }

    public static IGUID cacheAtomAndUpdateLocationBundles(SeaConfiguration configuration, Location location, Collection<LocationBundle> bundles) throws DataStorageException {
        CacheManager cacheManager = new CacheManager(configuration, location);
        IGUID guid = cacheManager.cacheAtom();
        if (bundles!= null && guid != null) {
            bundles.add(cacheManager.getCacheLocationBundle());
        }

        return guid;
    }

    public static IGUID cacheAtomAndUpdateLocationBundles(SeaConfiguration configuration, InputStream inputStream, Collection<LocationBundle> bundles) throws DataStorageException {
        CacheManager cacheManager = new CacheManager(configuration, inputStream);
        IGUID guid = cacheManager.cacheAtom();
        if (bundles!= null && guid != null) {
            bundles.add(cacheManager.getCacheLocationBundle());
        }

        return guid;
    }

}
