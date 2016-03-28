package uk.ac.standrews.cs.sos.interfaces.manifests;

import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;

import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Atom extends Manifest {

    Collection<LocationBundle> getLocations();
}
