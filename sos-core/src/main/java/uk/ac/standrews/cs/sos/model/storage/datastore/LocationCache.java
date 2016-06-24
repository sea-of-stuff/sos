package uk.ac.standrews.cs.sos.model.storage.datastore;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.interfaces.locations.Location;
import uk.ac.standrews.cs.sos.interfaces.storage.SOSFile;
import uk.ac.standrews.cs.sos.model.Configuration;
import uk.ac.standrews.cs.sos.model.locations.bundles.CacheLocationBundle;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.model.storage.FileBased.FileBasedFile;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class LocationCache extends LocationStore {

    public LocationCache(Configuration configuration, Location location) {
        super(configuration, location);
    }

    @Override
    protected LocationBundle getBundle(Location location) {
        return new CacheLocationBundle(location);
    }

    @Override
    protected SOSFile getAtomLocation(IGUID guid) {
        return new FileBasedFile(configuration.getDataDirectory(), guid.toString());
    }
}
