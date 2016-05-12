package uk.ac.standrews.cs.sos.model.storage;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.SourceLocationException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.interfaces.locations.Location;
import uk.ac.standrews.cs.sos.model.SeaConfiguration;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.model.store.*;

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
            throw new SourceLocationException(location.toString() + " " + e);
        }

        return stream;
    }

    public static IGUID cacheAtomAndUpdateLocationBundles(SeaConfiguration configuration, Location location, Collection<LocationBundle> bundles) throws DataStorageException {
        Store cache = new LocationCache(configuration, location);

        return storeAtomAndUpdateLocationBundles(cache, bundles);
    }

    public static IGUID cacheAtomAndUpdateLocationBundles(SeaConfiguration configuration, InputStream inputStream, Collection<LocationBundle> bundles) throws DataStorageException {
        Store cache = new StreamCache(configuration, inputStream);

        return storeAtomAndUpdateLocationBundles(cache, bundles);
    }

    public static IGUID persistAtomAndUpdateLocationBundles(SeaConfiguration configuration, Location location, Collection<LocationBundle> bundles) throws DataStorageException {
        Store cache = new LocationPersist(configuration, location);

        return storeAtomAndUpdateLocationBundles(cache, bundles);
    }

    public static IGUID persistAtomAndUpdateLocationBundles(SeaConfiguration configuration, InputStream inputStream, Collection<LocationBundle> bundles) throws DataStorageException {
        Store cache = new StreamPersist(configuration, inputStream);

        return storeAtomAndUpdateLocationBundles(cache, bundles);
    }

    private static IGUID storeAtomAndUpdateLocationBundles(Store store, Collection<LocationBundle> bundles) throws DataStorageException {
        StorageManager storageManager = new StorageManager(store);
        IGUID guid = storageManager.storeAtom();
        if (bundles!= null && guid != null) {
            bundles.add(storageManager.getLocationBundle());
        }

        return guid;
    }

}
