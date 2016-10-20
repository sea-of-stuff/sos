package uk.ac.standrews.cs.sos.interfaces.manifests;

import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;

import java.io.InputStream;
import java.util.Collection;

/**
 * This is the interface for the Atom manifest.
 * An atom is the basic building block for the SOS and it is used to abstract data over locations.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Atom extends Manifest {

    /**
     * Get the locations for this atom.
     *
     * @return locations of this atom
     */
    Collection<LocationBundle> getLocations();

    /**
     * Return the atom's data
     * @return
     */
    InputStream getData();
}
