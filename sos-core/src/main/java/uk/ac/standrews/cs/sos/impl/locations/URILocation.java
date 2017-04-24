package uk.ac.standrews.cs.sos.impl.locations;

import uk.ac.standrews.cs.sos.model.Location;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

import static uk.ac.standrews.cs.sos.constants.LocationSchemes.*;
import static uk.ac.standrews.cs.sos.constants.SOSConstants.USER_AGENT;

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
        HttpURLConnection httpcon = (HttpURLConnection) uri.toURL().openConnection();
        httpcon.addRequestProperty("User-Agent", USER_AGENT);

        return httpcon.getInputStream();
    }

    private InputStream getHTTPSSource() throws IOException {
        HttpsURLConnection httpcon = (HttpsURLConnection) uri.toURL().openConnection();
        httpcon.addRequestProperty("User-Agent", USER_AGENT);

        return httpcon.getInputStream();
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
