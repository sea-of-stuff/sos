package uk.ac.standrews.cs.sos.model.locations;

import uk.ac.standrews.cs.sos.interfaces.locations.Location;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

/**
 * supported schemes: http, https, file, ftp, etc
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class URILocation implements Location {

    private URI uri;

    public URILocation(String location) throws URISyntaxException {
        if (location.startsWith("/")) // assume this to be a local path
            location = "file://" + location;
        uri = new URI(location);
    }

    @Override
    public URI getURI() {
        return uri;
    }

    public InputStream getSource() throws IOException {
        HttpURLConnection httpcon = (HttpURLConnection) uri.toURL().openConnection();
        httpcon.addRequestProperty("User-Agent", "sos-core/0.1");

        return httpcon.getInputStream();

        // return uri.toURL().openStream();
    }

    @Override
    public String toString() {
        return uri.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        URILocation that = (URILocation) o;
        return Objects.equals(uri, that.uri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uri);
    }
}
