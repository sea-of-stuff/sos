package model.implementations.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Represents a location in the data-space within the Sea of Stuff.
 * This can be either a location in the local disk or a remote location.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class Location {

    public abstract String getProtocol();

    public abstract String getPort();

    /**
     * @return the location of the data
     */
    public abstract URL getLocationPath();

    /**
     * Gets an input stream for the source located at the given location.
     * Stream must be closed after usage.
     *
     * @return
     */
    public abstract InputStream getSource() throws IOException;

}
