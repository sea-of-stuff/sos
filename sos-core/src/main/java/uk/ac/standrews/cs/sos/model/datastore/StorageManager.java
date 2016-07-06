package uk.ac.standrews.cs.sos.model.datastore;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.storage.exceptions.StorageException;

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
     * @throws StorageException
     */
    public IGUID storeAtom() throws StorageException {
        return store.store();
    }

    public LocationBundle getLocationBundle() {
        return store.getLocationBundle();
    }

}
