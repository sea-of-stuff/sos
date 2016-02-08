package uk.ac.standrews.cs.sos.model.implementations.locations;

import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.SetUpTest;

import java.net.URISyntaxException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class LocationBundleTest extends SetUpTest {

    private static final String EXPECTED_LOCATIONS =
            "{\"cache\":"+
                    "[\"http://abc.com/123\"]}";

    private static final String EXPECTED_MULTIPLE_LOCATIONS =
            "{\"prov\":"+
                    "[\"http://abc.com/123/1\", \"http://abc.com/123/2\"]}";


    @Test
    public void toStringTest() throws URISyntaxException, JSONException {
        Location[] locations = new Location[] {new URILocation("http://abc.com/123")};
        LocationBundle bundle = new LocationBundle("cache", locations);
        JSONAssert.assertEquals(EXPECTED_LOCATIONS, bundle.toString(), true);
    }

    @Test
    public void toStringMultipleLocationsTest() throws URISyntaxException, JSONException {
        Location[] locations = new Location[] {new URILocation("http://abc.com/123/1"), new URILocation("http://abc.com/123/2")};
        LocationBundle bundle = new LocationBundle("prov", locations);
        JSONAssert.assertEquals(EXPECTED_MULTIPLE_LOCATIONS, bundle.toString(), true);
    }


}