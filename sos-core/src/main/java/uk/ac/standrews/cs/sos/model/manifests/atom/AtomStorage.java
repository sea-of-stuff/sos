package uk.ac.standrews.cs.sos.model.manifests.atom;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.interfaces.locations.Location;
import uk.ac.standrews.cs.sos.interfaces.manifests.LocationsIndex;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.model.manifests.atom.store.*;
import uk.ac.standrews.cs.sos.storage.LocalStorage;
import uk.ac.standrews.cs.storage.exceptions.StorageException;
import uk.ac.standrews.cs.storage.interfaces.Directory;
import uk.ac.standrews.cs.storage.interfaces.File;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AtomStorage {

    private IGUID nodeGUID;
    private LocalStorage storage;

    private LocationsIndex locationIndex;

    public AtomStorage(IGUID nodeGUID, LocalStorage storage) {
        this.nodeGUID = nodeGUID;
        this.storage = storage;


        try {
            Directory cacheDir = storage.getCachesDirectory();
            File file = storage.createFile(cacheDir, "locations.index");
            if (file.exists()) {
                locationIndex = LocationsIndexImpl.load(file);
            }
        } catch (DataStorageException | ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }

        if (locationIndex == null) {
            locationIndex = new LocationsIndexImpl();
        }

    }

    public void flush() {
        try {
            Directory cacheDir = storage.getCachesDirectory();
            File file = storage.createFile(cacheDir, "locations.index");
            locationIndex.persist(file);
        } catch (IOException | DataStorageException e) {
            e.printStackTrace();
        }
    }

    public Iterator<LocationBundle> getLocationsIterator(IGUID guid) {
        return locationIndex.findLocations(guid);
    }

    public IGUID cacheAtomAndUpdateLocationBundles(Location location,
                                                   Collection<LocationBundle> bundles) throws StorageException {

        Store cache = new LocationCache(nodeGUID, storage, location);
        return storeAtomAndUpdateLocationBundles(cache, bundles);
    }

    public IGUID cacheAtomAndUpdateLocationBundles(InputStream inputStream,
                                                   Collection<LocationBundle> bundles) throws StorageException {

        Store cache = new StreamCache(nodeGUID, storage, inputStream);
        return storeAtomAndUpdateLocationBundles(cache, bundles);
    }

    public IGUID persistAtomAndUpdateLocationBundles(Location location,
                                                     Collection<LocationBundle> bundles) throws StorageException {

        Store persistance = new LocationPersist(nodeGUID, storage, location);
        return storeAtomAndUpdateLocationBundles(persistance, bundles);
    }

    public IGUID persistAtomAndUpdateLocationBundles(InputStream inputStream,
                                                     Collection<LocationBundle> bundles) throws StorageException {

        Store persistance = new StreamPersist(nodeGUID, storage, inputStream);
        return storeAtomAndUpdateLocationBundles(persistance, bundles);
    }

    public IGUID persistAtomToRemote(Node node, InputStream inputStream, Collection<LocationBundle> bundles) throws StorageException {

        Store remote = new RemoteStore(node, inputStream);
        return storeAtomAndUpdateLocationBundles(remote, bundles);
    }

    private IGUID storeAtomAndUpdateLocationBundles(Store store, Collection<LocationBundle> bundles) throws StorageException {

        IGUID guid = store.store();
        if (bundles!= null && guid != null) {
            LocationBundle locationBundle = store.getLocationBundle();
            bundles.add(locationBundle);

            locationIndex.addLocation(guid, locationBundle);
        }

        return guid;
    }

}
