package uk.ac.standrews.cs.sos.model.datastore;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.interfaces.locations.Location;
import uk.ac.standrews.cs.sos.model.Configuration;
import uk.ac.standrews.cs.sos.model.locations.bundles.CacheLocationBundle;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.storage.interfaces.Directory;
import uk.ac.standrews.cs.sos.storage.interfaces.File;
import uk.ac.standrews.cs.sos.storage.interfaces.Storage;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class LocationCache extends LocationStore {

    private Storage storage;

    public LocationCache(Configuration configuration, Storage storage, Location location) {
        super(configuration, location);
        this.storage = storage;
    }

    @Override
    protected LocationBundle getBundle(Location location) {
        return new CacheLocationBundle(location);
    }

    @Override
    protected File getAtomLocation(IGUID guid) {
        Directory dataDirectory = storage.getDataDirectory();
        return storage.createFile(dataDirectory, guid.toString());
    }
}
