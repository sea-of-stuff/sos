package uk.ac.standrews.cs.sos.impl.locations;

import uk.ac.standrews.cs.sos.model.Location;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class LocationUtility {

    /**
     * Return an InputStream for the given location.
     * The method calling this function should ensure that the stream is closed.
     *
     * @param location
     * @return
     */
    public static InputStream getInputStreamFromLocation(Location location) {
        InputStream stream;
        try {
            stream = location.getSource();
        } catch (IOException e) {
            return null;
        }

        return stream;
    }
}
