package IO.sources.implementations;

import IO.sources.DataSource;
import model.implementations.utils.Location;

import java.io.InputStream;
import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class MemorySource implements DataSource {

    public MemorySource() {
        // TODO - persist data to a given location
    }

    @Override
    public InputStream getInputStream() {
        return null;
    }

    @Override
    public Collection<Location> getLocations() {
        return null;
    }

    // Persist source into local storage
    private void persist() {

    }
}
