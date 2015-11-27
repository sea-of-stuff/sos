package IO.sources;

import model.implementations.utils.Location;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

/**
 * The DataSource interface represents a service agnostic source for data.
 * A service implementing this interface should return an InputStream of data,
 * irrespective of where the data comes from, how it is retrieved or any other factor.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface DataSource {

    /**
     * Get the input stream of the data source.
     *
     * @return the input stream of the data source.
     */
    InputStream getInputStream() throws IOException;

    /**
     * Get a collection of locations for this data source.
     *
     * @return collection of locations
     */
    Collection<Location> getLocations();
}
