package uk.ac.standrews.cs.sos.impl.datamodel.locations;

import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.SetUpTest;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.bundles.CacheLocationBundle;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.bundles.ExternalLocationBundle;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.model.Location;

import java.net.URISyntaxException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class LocationBundleTest extends SetUpTest {

    private static final String EXPECTED_LOCATION =
            "{\"type\":\"cache\"," +
                    "\"location\":\"http://abc.com/123\"}";

    private static final String EXPECTED_PROV_LOCATION =
            "{\"type\":\"external\"," +
                    "\"location\":\"http://abc.com/123/1\"}";

    @Test
    public void toStringTest() throws URISyntaxException, JSONException {
        Location location = new URILocation("http://abc.com/123");
        LocationBundle bundle = new CacheLocationBundle(location);
        JSONAssert.assertEquals(EXPECTED_LOCATION, bundle.toString(), true);
    }

    @Test
    public void toStringProvLocationTest() throws URISyntaxException, JSONException {
        Location location = new URILocation("http://abc.com/123/1");
        LocationBundle bundle = new ExternalLocationBundle(location);
        JSONAssert.assertEquals(EXPECTED_PROV_LOCATION, bundle.toString(), true);
    }

}