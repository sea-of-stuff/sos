package sos.model.implementations.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class URLLocation extends Location {

    private String protocol;
    private URL url;
    private int port;

    public URLLocation(String location) throws MalformedURLException {
        url = new URL(location);
    }

    @Override
    public String getProtocol() {
        return null;
    }

    @Override
    public int getPort() {
        return -1;
    }

    @Override
    public URL getLocationPath() {
        return url;
    }

    @Override
    public InputStream getSource() throws IOException {
        return url.openStream();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        URLLocation that = (URLLocation) o;
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
