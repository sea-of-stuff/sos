package model.implementations.utils;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * FIXME - rename. It seems like this contentverifier is strictly linked to #Content class
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ContentVerifier {

    // Suppresses default constructor, ensuring non-instantiability.
    private ContentVerifier() {}

    /**
     * Verify that the data at a given location and the given GUID match.
     *
     * @param location
     * @param guid
     * @return
     */
    public static boolean verify(Location location, GUID guid) {
        throw new NotImplementedException();
    }
}
