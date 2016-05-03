package uk.ac.standrews.cs.sos.interfaces.locations;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Location {

    URI getURI() throws URISyntaxException;

    /**
     * Gets an input stream for the source located at the given location.
     * Stream must be closed after usage.
     *
     * @return
     */
    InputStream getSource() throws IOException;

    String toString();
}
