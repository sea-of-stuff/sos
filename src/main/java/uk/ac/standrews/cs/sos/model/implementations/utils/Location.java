package uk.ac.standrews.cs.sos.model.implementations.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

/**
 * Represents a location in the data-space within the Sea of Stuff.
 * This can be either a location in the local disk or a remote location.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Location {

    private transient String protocol;
    private URL url;
    private transient int port;

    public Location(String location) throws MalformedURLException {
        url = new URL(location);
    }

    public String getProtocol() {
        return null;
    }

    public int getPort() {
        return -1;
    }

    public URL getLocationPath() {
        return url;
    }

    /**
     * Gets an input stream for the source located at the given location.
     * Stream must be closed after usage.
     *
     * @return
     */
    public InputStream getSource() throws IOException {
        return url.openStream();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location that = (Location) o;
        return Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url);
    }

    public String toString() {
        return url.toString();
        // return protocol + ":" + url.toString() + ":" + port;
    }
}
