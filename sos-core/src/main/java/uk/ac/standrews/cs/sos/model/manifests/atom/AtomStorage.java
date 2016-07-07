package uk.ac.standrews.cs.sos.model.manifests.atom;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.interfaces.locations.Location;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.model.storage.InternalStorage;
import uk.ac.standrews.cs.storage.exceptions.StorageException;

import java.io.InputStream;
import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AtomStorage {

    public static IGUID cacheAtomAndUpdateLocationBundles(InternalStorage storage, Location location, Collection<LocationBundle> bundles) throws StorageException {
        Store cache = new LocationCache(storage, location);

        return storeAtomAndUpdateLocationBundles(cache, bundles);
    }

    public static IGUID cacheAtomAndUpdateLocationBundles(InternalStorage storage, InputStream inputStream, Collection<LocationBundle> bundles) throws StorageException {
        Store cache = new StreamCache(storage, inputStream);

        return storeAtomAndUpdateLocationBundles(cache, bundles);
    }

    public static IGUID persistAtomAndUpdateLocationBundles(InternalStorage storage, Location location, Collection<LocationBundle> bundles) throws StorageException {
        Store cache = new LocationPersist(storage, location);

        return storeAtomAndUpdateLocationBundles(cache, bundles);
    }

    public static IGUID persistAtomAndUpdateLocationBundles(InternalStorage storage, InputStream inputStream, Collection<LocationBundle> bundles) throws StorageException {
        Store cache = new StreamPersist(storage, inputStream);

        return storeAtomAndUpdateLocationBundles(cache, bundles);
    }

    private static IGUID storeAtomAndUpdateLocationBundles(Store store, Collection<LocationBundle> bundles) throws StorageException {
        AtomStorageManager atomStorageManager = new AtomStorageManager(store);
        IGUID guid = atomStorageManager.storeAtom();
        if (bundles!= null && guid != null) {
            bundles.add(atomStorageManager.getLocationBundle());
        }

        return guid;
    }

}
