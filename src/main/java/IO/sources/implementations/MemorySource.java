package IO.sources.implementations;

import IO.sources.DataSource;

import java.io.InputStream;

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

    // Persist source into local storage
    private void persist() {

    }
}
