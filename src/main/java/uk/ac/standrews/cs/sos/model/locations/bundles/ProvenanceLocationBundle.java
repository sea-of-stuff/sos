package uk.ac.standrews.cs.sos.model.locations.bundles;

import uk.ac.standrews.cs.sos.interfaces.Location;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ProvenanceLocationBundle extends LocationBundle {

    private final static String TYPE = "prov";

    public ProvenanceLocationBundle(Location location) {
        super(TYPE, location);
    }

}
