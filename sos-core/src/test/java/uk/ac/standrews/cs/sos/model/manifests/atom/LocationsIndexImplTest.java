package uk.ac.standrews.cs.sos.model.manifests.atom;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.interfaces.manifests.LocationsIndex;
import uk.ac.standrews.cs.sos.model.locations.URILocation;
import uk.ac.standrews.cs.sos.model.locations.bundles.CacheLocationBundle;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;

import java.net.URISyntaxException;
import java.util.Iterator;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class LocationsIndexImplTest {

    @Test
    public void addGetLocationsTest() throws URISyntaxException {
        LocationsIndex locationsIndex = new LocationsIndexImpl();

        IGUID guid = GUIDFactory.generateRandomGUID();
        LocationBundle locationBundle = new CacheLocationBundle(new URILocation("http://example.org/resource"));

        locationsIndex.addLocation(guid, locationBundle);

        Iterator<LocationBundle> it = locationsIndex.findLocations(guid);
        assertTrue(it.hasNext());

        LocationBundle indexedLocationBundle = it.next();
        assertEquals(indexedLocationBundle, locationBundle);
    }

}