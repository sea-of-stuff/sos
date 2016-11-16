package uk.ac.standrews.cs.sos.interfaces.manifests;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;

import java.util.Iterator;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface LocationsIndex {

    void addLocation(IGUID guid, LocationBundle locationBundle);

    Iterator<LocationBundle> findLocations(IGUID guid);

}
