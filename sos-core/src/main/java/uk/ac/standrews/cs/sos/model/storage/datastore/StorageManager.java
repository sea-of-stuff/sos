package uk.ac.standrews.cs.sos.model.storage.datastore;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;

/**
 * Store an atom - given its location or data stream - to this NodeManager node.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class StorageManager {

    private final Store store;

    public StorageManager(Store store) {
        this.store = store;
    }

    /**
     * Store the data at the Location Bundles in the storeLocation
     *
     * @return GUID generated from the data at the location bundles
     * @throws DataStorageException
     */
    public IGUID storeAtom() throws DataStorageException {
        return store.store();
    }

    public LocationBundle getLocationBundle() {
        return store.getLocationBundle();
    }

}
