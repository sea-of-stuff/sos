package uk.ac.standrews.cs.sos.model.locations.bundles;

import uk.ac.standrews.cs.sos.interfaces.model.Location;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ProvenanceLocationBundle extends LocationBundle {

    public ProvenanceLocationBundle(Location location) {
        super(BundleTypes.PROVENANCE, location);
    }

}
