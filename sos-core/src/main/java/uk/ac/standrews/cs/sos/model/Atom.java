package uk.ac.standrews.cs.sos.model;

import uk.ac.standrews.cs.sos.impl.locations.bundles.LocationBundle;

import java.io.InputStream;
import java.util.Set;

/**
 * This is the interface for the Atom manifest.
 * An atom is the basic building block for the SOS and it is used to abstract data over locations.
 *
 * Example:
 *
 * {
 *  "Type" : "Atom",
 *  "GUID" : "da39a3ee5e6b4b0d3255bfef95601890afd80709",
 *  "Locations" : [
 *      {
 *      "Type" : "cache",
 *      "Location" : "sos://3c9bfd93ab9a6e2ed501fc583685088cca66bac2/da39a3ee5e6b4b0d3255bfef95601890afd80709"
 *      }
 *  ]
 * }
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Atom extends Manifest {

    /**
     * Get the locations for this atom.
     *
     * @return locations of this atom
     */
    Set<LocationBundle> getLocations();

    /**
     * Return the atom's data
     * @return
     */
    InputStream getData();
}
