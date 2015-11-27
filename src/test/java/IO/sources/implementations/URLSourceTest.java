package IO.sources.implementations;

import IO.sources.DataSource;
import model.implementations.utils.Location;
import model.implementations.utils.StringLocation;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;

import static org.testng.Assert.*;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class URLSourceTest {

    private static final String HTTP_BIN_SERVICE = "https://httpbin.org/";

    @Test (expectedExceptions = IOException.class)
    public void testNullSource() throws Exception {
        DataSource source = new URLSource(null);

        assertNull(source.getLocations());
        assertNull(source.getInputStream());
    }

    @Test (expectedExceptions = IOException.class)
    public void testEmptySource() throws Exception {
        DataSource source = new URLSource("");

        assertNull(source.getLocations());
        assertNull(source.getInputStream());
    }

    @Test
    public void testGetInputStream() throws Exception {
        DataSource source = new URLSource(HTTP_BIN_SERVICE);

        BufferedReader in = new BufferedReader(new InputStreamReader(source.getInputStream()));
        assertFalse(in.readLine().isEmpty());
    }

    @Test
    public void testGetLocations() throws Exception {
        DataSource source = new URLSource(HTTP_BIN_SERVICE);

        Collection<Location> locations = source.getLocations();
        assertTrue(locations.contains(new StringLocation(HTTP_BIN_SERVICE)));
    }
}