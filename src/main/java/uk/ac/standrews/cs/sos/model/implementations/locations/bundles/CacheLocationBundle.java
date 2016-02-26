package uk.ac.standrews.cs.sos.model.implementations.locations.bundles;

import uk.ac.standrews.cs.sos.model.implementations.locations.Location;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class CacheLocationBundle extends LocationBundle {

    private final static String TYPE = "cache";

    public CacheLocationBundle(Location[] locations) {
        this(TYPE, locations);
    }

    private CacheLocationBundle(String type, Location[] locations) {
        super(type, locations);
    }
}
