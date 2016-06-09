package uk.ac.standrews.cs.sos.model.store;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.interfaces.locations.Location;
import uk.ac.standrews.cs.sos.interfaces.storage.SOSFile;
import uk.ac.standrews.cs.sos.model.Configuration;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.model.locations.bundles.PersistLocationBundle;
import uk.ac.standrews.cs.sos.model.storage.FileBased.FileBasedFile;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class LocationPersist extends LocationStore {

    public LocationPersist(Configuration configuration, Location location) {
        super(configuration, location);
    }

    @Override
    protected LocationBundle getBundle(Location location) {
        return new PersistLocationBundle(location);
    }

    @Override
    protected SOSFile getAtomLocation(IGUID guid) {
        return new FileBasedFile(configuration.getCacheDirectory(), guid.toString());
    }
}
