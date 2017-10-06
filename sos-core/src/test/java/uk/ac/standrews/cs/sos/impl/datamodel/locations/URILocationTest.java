package uk.ac.standrews.cs.sos.impl.datamodel.locations;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.SetUpTest;
import uk.ac.standrews.cs.sos.model.Location;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.testng.Assert.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class URILocationTest extends SetUpTest {

    @Test
    public void testConstructorLocalURL() throws URISyntaxException, IOException {
        Location location = new URILocation("/sos/data/test.txt");
        assertEquals(location.getURI().toString(), "file://localhost/sos/data/test.txt");
    }

    @Test
    public void testConstructorLocalFileURL() throws URISyntaxException, IOException {
        Location location = new URILocation("file:///sos/data/test.txt");
        assertEquals(location.getURI().toString(), "file://localhost/sos/data/test.txt");
    }

    @Test
    public void testConstructorURL() throws URISyntaxException, IOException {
        Location location = new URILocation("http://fakehost.co.uk/sos/data/test.txt");
        assertEquals(location.getURI().toString(), "http://fakehost.co.uk/sos/data/test.txt");
    }
}