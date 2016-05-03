package uk.ac.standrews.cs.sos.model.data;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.interfaces.locations.Location;

/**
 *
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class DataVerifier {

    // Suppresses default constructor, ensuring non-instantiability.
    private DataVerifier() {}

    /**
     * Verify that the data at a given location and the given GUID match.
     *
     * @param location
     * @param guid
     * @return
     */
    public static boolean verify(Location location, IGUID guid) {
        throw new UnsupportedOperationException();
    }
}
