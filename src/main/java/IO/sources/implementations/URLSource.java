package IO.sources.implementations;

import IO.sources.DataSource;
import model.implementations.utils.Location;
import model.implementations.utils.StringLocation;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class URLSource implements DataSource {

    private String url;

    public URLSource(String url) {
        this.url = url;

    }

    @Override
    public InputStream getInputStream() throws IOException {

        URLConnection urlConnection;
        InputStream ret;
        try {
            urlConnection = new URL(url).openConnection();
            ret = urlConnection.getInputStream();
        } catch (IOException e) {
            throw new IOException();
        }

        return ret;
    }

    @Override
    public Collection<Location> getLocations() {

        if (url == null || url.isEmpty())
            return null;

        List<Location> locations = new ArrayList<Location>();

        Location loc = new StringLocation(url);
        locations.add(loc);

        return locations;
    }
}
