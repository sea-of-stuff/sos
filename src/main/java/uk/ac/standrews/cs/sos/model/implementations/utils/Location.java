package uk.ac.standrews.cs.sos.model.implementations.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

/**
 * Represents a location in the data-space within the Sea of Stuff.
 * This can be either a location in the local disk or a remote location.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Location {

    private transient String protocol;
    private URI uri;
    private transient int port;

    public Location(String location) throws URISyntaxException {
        uri = new URI(location); // TODO - store uri FIXME
    }

    public String getProtocol() {
        return null;
    }

    public int getPort() {
        return -1;
    }

    public URI getLocationPath() {
        return uri;
    }

    /**
     * Gets an input stream for the source located at the given location.
     * Stream must be closed after usage.
     *
     * @return
     */
    public InputStream getSource() throws IOException {
        // TODO - check if this can work for both local and remote paths!
        return uri.toURL().openStream();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location that = (Location) o;
        return Objects.equals(uri, that.uri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uri);
    }

    public String toString() {
        return uri.toString();
        // return protocol + ":" + url.toString() + ":" + port;
    }
}
