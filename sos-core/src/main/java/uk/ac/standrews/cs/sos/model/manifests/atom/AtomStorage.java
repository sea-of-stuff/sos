package uk.ac.standrews.cs.sos.model.manifests.atom;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.interfaces.locations.Location;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.model.storage.InternalStorage;
import uk.ac.standrews.cs.sos.model.store.*;
import uk.ac.standrews.cs.sos.network.RequestsManager;
import uk.ac.standrews.cs.storage.exceptions.StorageException;

import java.io.InputStream;
import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AtomStorage {

    private IGUID nodeGUID;
    private InternalStorage storage;


    public AtomStorage(IGUID nodeGUID, InternalStorage storage) {
        this.nodeGUID = nodeGUID;
        this.storage = storage;
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

    public IGUID persistAtomToRemote(RequestsManager requestsManager, Node node, InputStream inputStream) throws StorageException {

        Store remote = new RemoteStore(requestsManager, node, inputStream);

        return storeAtomAndUpdateLocationBundles(remote,null);
    }

    private IGUID storeAtomAndUpdateLocationBundles(Store store, Collection<LocationBundle> bundles) throws StorageException {
        AtomStorageManager atomStorageManager = new AtomStorageManager(store);
        IGUID guid = atomStorageManager.storeAtom();
        if (bundles!= null && guid != null) {
            bundles.add(atomStorageManager.getLocationBundle());
        }

        return guid;
    }

}
