package uk.ac.standrews.cs.sos.impl.locations;

import uk.ac.standrews.cs.sos.impl.network.HTTPMethod;
import uk.ac.standrews.cs.sos.impl.network.RequestsManager;
import uk.ac.standrews.cs.sos.impl.network.ResponseType;
import uk.ac.standrews.cs.sos.impl.network.SyncRequest;
import uk.ac.standrews.cs.sos.interfaces.network.Response;
import uk.ac.standrews.cs.sos.model.Location;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

import static uk.ac.standrews.cs.sos.constants.LocationSchemes.*;

/**
 * supported schemes: http, https, file, ftp, etc
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class URILocation implements Location {

    private URI uri;

    public URILocation(String location) throws URISyntaxException {
        if (location.startsWith("/")) // assume this to be a local path
            location = FILE_SCHEME + "://" + location;
        uri = new URI(location);
    }

    @Override
    public URI getURI() {
        return uri;
    }

    public InputStream getSource() throws IOException {
        String scheme = uri.getScheme();

        switch(scheme) {
            case FILE_SCHEME:
                return getFileSource();
            case HTTP_SCHEME:
                return getHTTPSource();
            case HTTPS_SCHEME:
                return getHTTPSSource();
            default:
                throw new IOException("Scheme " + scheme + " not supported");
        }

    }

    private InputStream getHTTPSource() throws IOException {

        SyncRequest request = new SyncRequest(HTTPMethod.GET, uri.toURL(), ResponseType.BINARY);
        Response response = RequestsManager.getInstance().playSyncRequest(request);

        return response.getBody();
    }

    private InputStream getHTTPSSource() throws IOException {

        SyncRequest request = new SyncRequest(HTTPMethod.GET, uri.toURL(), ResponseType.BINARY);
        Response response = RequestsManager.getInstance().playSyncRequest(request);

        return response.getBody();
    }

    private InputStream getFileSource() throws IOException {
        return uri.toURL().openStream();
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
