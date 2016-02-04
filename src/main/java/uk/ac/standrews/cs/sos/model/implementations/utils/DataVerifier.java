package uk.ac.standrews.cs.sos.model.implementations.utils;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import uk.ac.standrews.cs.sos.model.implementations.locations.OldLocation;

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
    public static boolean verify(OldLocation location, GUID guid) {
        throw new NotImplementedException();
    }
}
