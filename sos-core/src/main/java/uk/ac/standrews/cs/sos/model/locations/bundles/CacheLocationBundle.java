package uk.ac.standrews.cs.sos.model.locations.bundles;

import uk.ac.standrews.cs.sos.interfaces.model.Location;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class CacheLocationBundle extends LocationBundle {

    public CacheLocationBundle(Location location) {
        super(BundleTypes.CACHE, location);
    }

}
