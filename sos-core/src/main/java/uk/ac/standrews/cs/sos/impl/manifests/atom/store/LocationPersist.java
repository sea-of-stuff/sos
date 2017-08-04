package uk.ac.standrews.cs.sos.impl.manifests.atom.store;

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.impl.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.impl.locations.bundles.PersistLocationBundle;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.model.Location;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class LocationPersist extends LocationStore {

    public LocationPersist(IGUID nodeGUID, LocalStorage storage, Location location) {
        super(nodeGUID, storage, location, true);
    }

    @Override
    protected LocationBundle getBundle(Location location) {
        return new PersistLocationBundle(location);
    }

}
