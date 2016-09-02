package uk.ac.standrews.cs.sos.model.locations;

import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.SetUpTest;
import uk.ac.standrews.cs.sos.interfaces.locations.Location;
import uk.ac.standrews.cs.sos.model.locations.bundles.CacheLocationBundle;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.model.locations.bundles.ProvenanceLocationBundle;

import java.net.URISyntaxException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class LocationBundleTest extends SetUpTest {

    private static final String EXPECTED_LOCATION =
            "{\"Type\":\"cache\"," +
                    "\"Location\":\"http://abc.com/123\"}";

    private static final String EXPECTED_PROV_LOCATION =
            "{\"Type\":\"provenance\"," +
                    "\"Location\":\"http://abc.com/123/1\"}";

    @Test
    public void toStringTest() throws URISyntaxException, JSONException {
        Location location = new URILocation("http://abc.com/123");
        LocationBundle bundle = new CacheLocationBundle(location);
        JSONAssert.assertEquals(EXPECTED_LOCATION, bundle.toString(), true);
    }

    @Test
    public void toStringProvLocationTest() throws URISyntaxException, JSONException {
        Location location = new URILocation("http://abc.com/123/1");
        LocationBundle bundle = new ProvenanceLocationBundle(location);
        JSONAssert.assertEquals(EXPECTED_PROV_LOCATION, bundle.toString(), true);
    }

}