package uk.ac.standrews.cs.sos.model.storage;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.SourceLocationException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.interfaces.locations.Location;
import uk.ac.standrews.cs.sos.model.Configuration;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.model.storage.FileBased.FileBasedStorage;
import uk.ac.standrews.cs.sos.model.store.*;
import uk.ac.standrews.cs.sos.node.Config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class StorageHelper {

    public static Storage createStorage(Config config) {
        Storage storage = null;

        switch(config.s_type) {
            case Config.S_TYPE_LOCAL:
                storage = new FileBasedStorage(config);
                break;
            case Config.S_TYPE_NETWORK:
                // TODO
                break;
            case Config.S_TYPE_AWS_S3:
                // TODO
                break;
            default:
                System.out.println("I should throw an error, but instead I will just tell you I do not know this type of storage");
                break;
        }

        return storage;
    }

    /**
     * Return an InputStream for the given location.
     * The method calling this function should ensure that the stream is closed.
     *
     * @param location
     * @return
     * @throws SourceLocationException
     */
    public static InputStream getInputStreamFromLocation(Location location) throws SourceLocationException {
        InputStream stream;
        try {
            stream = location.getSource();
        } catch (IOException e) {
            throw new SourceLocationException(location.toString() + " " + e);
        }

        return stream;
    }

    public static IGUID cacheAtomAndUpdateLocationBundles(Configuration configuration, Location location, Collection<LocationBundle> bundles) throws DataStorageException {
        Store cache = new LocationCache(configuration, location);

        return storeAtomAndUpdateLocationBundles(cache, bundles);
    }

    public static IGUID cacheAtomAndUpdateLocationBundles(Configuration configuration, InputStream inputStream, Collection<LocationBundle> bundles) throws DataStorageException {
        Store cache = new StreamCache(configuration, inputStream);

        return storeAtomAndUpdateLocationBundles(cache, bundles);
    }

    public static IGUID persistAtomAndUpdateLocationBundles(Configuration configuration, Location location, Collection<LocationBundle> bundles) throws DataStorageException {
        Store cache = new LocationPersist(configuration, location);

        return storeAtomAndUpdateLocationBundles(cache, bundles);
    }

    public static IGUID persistAtomAndUpdateLocationBundles(Configuration configuration, InputStream inputStream, Collection<LocationBundle> bundles) throws DataStorageException {
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
