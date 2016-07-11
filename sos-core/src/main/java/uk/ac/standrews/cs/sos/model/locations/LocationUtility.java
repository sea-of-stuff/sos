package uk.ac.standrews.cs.sos.model.locations;

import uk.ac.standrews.cs.sos.exceptions.location.SourceLocationException;
import uk.ac.standrews.cs.sos.interfaces.locations.Location;

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
     * @throws SourceLocationException
     */
    public static InputStream getInputStreamFromLocation(Location location) throws SourceLocationException {
        InputStream stream;
        try {
            stream = location.getSource();
        } catch (IOException e) {
            throw new SourceLocationException("Unable to get data for location: " + location.toString(), e);
        }

        return stream;
    }
}
