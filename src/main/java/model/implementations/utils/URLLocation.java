package model.implementations.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class URLLocation extends Location {

    private URL url;

    public URLLocation(String location) throws MalformedURLException {
        url = new URL(location);
    }

    @Override
    public URL getLocationPath() {
        return url;
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
}
