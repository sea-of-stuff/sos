package uk.ac.standrews.cs.sos.model.implementations.locations;

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
public class OldLocation {

    private URI uri;


    // TODO - make OldLocation abstract and implement different types of locations
    // e.g. repo, prov, index, etc

    public OldLocation(String location) throws URISyntaxException {
        if (location.startsWith("/"))
            location = "file://" + location;
        uri = new URI(location);
    }

    public String getProtocol() {
        return uri.getScheme();
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
        return uri.toURL().openStream();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OldLocation that = (OldLocation) o;
        return Objects.equals(uri, that.uri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uri);
    }

    public String toString() {
        return uri.toString();
    }

}
