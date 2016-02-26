package uk.ac.standrews.cs.sos.model.implementations.locations.bundles;

import uk.ac.standrews.cs.sos.model.implementations.locations.Location;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ProvenanceLocationBundle extends LocationBundle {

    private final static String TYPE = "prov";

    public ProvenanceLocationBundle(Location[] locations) {
        this(TYPE, locations);
    }

    private ProvenanceLocationBundle(String type, Location[] locations) {
        super(type, locations);
    }
}
