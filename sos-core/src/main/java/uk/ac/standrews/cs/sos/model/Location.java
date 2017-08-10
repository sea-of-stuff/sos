package uk.ac.standrews.cs.sos.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import uk.ac.standrews.cs.sos.json.LocationDeserializer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@JsonDeserialize(using = LocationDeserializer.class)
public interface Location {

    /**
     * Get the URI representation for this location.
     *
     * @return the URI of this location
     * @throws URISyntaxException
     */
    URI getURI() throws URISyntaxException;

    /**
     * Gets an input stream for the source located at the given location.
     * Stream must be closed after usage.
     *
     * @return the stream source for the data in this location
     */
    InputStream getSource() throws IOException;

    /**
     * Get a string representation for this location.
     *
     * @return string representation of this location
     */
    String toString();
}
