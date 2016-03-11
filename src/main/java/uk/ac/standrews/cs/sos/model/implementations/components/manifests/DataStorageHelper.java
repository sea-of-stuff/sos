package uk.ac.standrews.cs.sos.model.implementations.components.manifests;

import uk.ac.standrews.cs.sos.configurations.SeaConfiguration;
import uk.ac.standrews.cs.sos.exceptions.SourceLocationException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.model.implementations.locations.Location;
import uk.ac.standrews.cs.sos.model.implementations.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUID;

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
            throw new SourceLocationException("CacheDataStorage " + location.toString() + " " + e);
        }

        return stream;
    }

    public static GUID cacheAtomAndUpdateLocationBundles(SeaConfiguration configuration, Collection<LocationBundle> locations) throws DataStorageException {
        GUID guid = null;
        for(LocationBundle location:locations) {
            CacheDataStorage cacheDataStorage = new CacheDataStorage(configuration, location);
            guid = cacheDataStorage.cacheAtom();
            if (guid != null) {
                locations.add(cacheDataStorage.getCacheLocationBundle());
                break;
            }
        }
        return guid;
    }

    public static GUID cacheAtomAndUpdateLocationBundles(SeaConfiguration configuration, InputStream inputStream, Collection<LocationBundle> locations) throws DataStorageException {
        CacheDataStorage cacheDataStorage = new CacheDataStorage(configuration, inputStream);
        GUID guid = cacheDataStorage.cacheAtom();
        if (guid != null) {
            locations.add(cacheDataStorage.getCacheLocationBundle());
        }

        return guid;
    }

}
