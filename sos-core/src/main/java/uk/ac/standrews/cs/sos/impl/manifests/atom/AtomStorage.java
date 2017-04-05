package uk.ac.standrews.cs.sos.impl.manifests.atom;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.actors.DDS;
import uk.ac.standrews.cs.sos.actors.NDS;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.impl.manifests.atom.store.*;
import uk.ac.standrews.cs.sos.impl.storage.LocalStorage;
import uk.ac.standrews.cs.sos.interfaces.manifests.LocationsIndex;
import uk.ac.standrews.cs.sos.model.Location;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.protocol.TasksQueue;
import uk.ac.standrews.cs.sos.protocol.tasks.DataReplication;
import uk.ac.standrews.cs.storage.exceptions.StorageException;
import uk.ac.standrews.cs.storage.interfaces.Directory;
import uk.ac.standrews.cs.storage.interfaces.File;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Set;

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

    /**
     * Return an iterator of locations for data matching the given guid
     * @param guid
     * @return
     */
    public Iterator<LocationBundle> getLocationsIterator(IGUID guid) {
        return locationIndex.findLocations(guid);
    }

    public IGUID cacheAtomAndUpdateLocationBundles(Location location, Set<LocationBundle> bundles) throws StorageException {

        Store cache = new LocationCache(nodeGUID, storage, location);
        return storeAtomAndUpdateLocationBundles(cache, bundles);
    }

    public IGUID cacheAtomAndUpdateLocationBundles(InputStream inputStream, Set<LocationBundle> bundles) throws StorageException {

        Store cache = new StreamCache(nodeGUID, storage, inputStream);
        return storeAtomAndUpdateLocationBundles(cache, bundles);
    }

    public IGUID persistAtomAndUpdateLocationBundles(Location location, Set<LocationBundle> bundles) throws StorageException {

        Store persistance = new LocationPersist(nodeGUID, storage, location);
        return storeAtomAndUpdateLocationBundles(persistance, bundles);
    }

    public IGUID persistAtomAndUpdateLocationBundles(InputStream inputStream, Set<LocationBundle> bundles) throws StorageException {

        Store persistance = new StreamPersist(nodeGUID, storage, inputStream);
        return storeAtomAndUpdateLocationBundles(persistance, bundles);
    }

    public void replicate(InputStream data, Iterator<Node> nodes, int replicationFactor, NDS nds, DDS dds) throws SOSProtocolException {

        DataReplication replicationTask = new DataReplication(data, nodes, replicationFactor, locationIndex, nds, dds);
        TasksQueue.instance().performSyncTask(replicationTask); // TODO - NOT SURE IF THIS CAN BE CHANGED TO ASYNC FOR THE MOMENT, since we may risk to close the stream before using it
    }

    private IGUID storeAtomAndUpdateLocationBundles(Store store, Set<LocationBundle> bundles) throws StorageException {

        IGUID guid = store.store();
        if (bundles!= null && guid != null) {
            LocationBundle locationBundle = store.getLocationBundle();
            bundles.add(locationBundle);

            locationIndex.addLocation(guid, locationBundle);
        }

        return guid;
    }

}
