package uk.ac.standrews.cs.sos.model.implementations.utils;

import org.testng.annotations.Test;

import java.net.URISyntaxException;

import static org.testng.Assert.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class LocationTest {

    @Test
    public void testConstructorLocalURL() throws URISyntaxException {
        Location location = new Location("/sos/data/test.txt");
        assertEquals(location.getLocationPath().toString(), "/sos/data/test.txt");
    }

    @Test
    public void testConstructorLocalFileURL() throws URISyntaxException {
        Location location = new Location("file:///sos/data/test.txt");
        // The three forward slashes are compressed into one.
        assertEquals(location.getLocationPath().toString(), "file:///sos/data/test.txt");
    }

    @Test
    public void testConstructorURL() throws URISyntaxException {
        Location location = new Location("http://fakehost.co.uk/sos/data/test.txt");
        assertEquals(location.getLocationPath().toString(), "http://fakehost.co.uk/sos/data/test.txt");
    }
}