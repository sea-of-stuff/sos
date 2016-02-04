package uk.ac.standrews.cs.sos.model.implementations.locations;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.model.implementations.locations.OldLocation;

import java.net.URISyntaxException;

import static org.testng.Assert.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class URILocationTest {

    @Test
    public void testConstructorLocalURL() throws URISyntaxException {
        Location location = new URILocation("/sos/data/test.txt");
        assertEquals(location.getURI().toString(), "/sos/data/test.txt");
    }

    @Test
    public void testConstructorLocalFileURL() throws URISyntaxException {
        Location location = new URILocation("file:///sos/data/test.txt");
        assertEquals(location.getURI().toString(), "file:///sos/data/test.txt");
    }

    @Test
    public void testConstructorURL() throws URISyntaxException {
        Location location = new URILocation("http://fakehost.co.uk/sos/data/test.txt");
        assertEquals(location.getURI().toString(), "http://fakehost.co.uk/sos/data/test.txt");
    }
}