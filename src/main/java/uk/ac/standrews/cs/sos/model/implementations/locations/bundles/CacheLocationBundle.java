package uk.ac.standrews.cs.sos.model.implementations.locations.bundles;

import uk.ac.standrews.cs.sos.model.implementations.locations.Location;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class CacheLocationBundle extends LocationBundle {

    private final static String TYPE = "cache";

    public CacheLocationBundle(Location location) {
        this(TYPE, location);
    }

    private CacheLocationBundle(String type, Location location) {
        super(type, location);
    }
}
