package uk.ac.standrews.cs.sos.impl.datamodel.locations.bundles;

import uk.ac.standrews.cs.sos.model.Location;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class PersistLocationBundle extends LocationBundle {

    public PersistLocationBundle(Location location) {
        super(BundleTypes.PERSISTENT, location);
    }
}
