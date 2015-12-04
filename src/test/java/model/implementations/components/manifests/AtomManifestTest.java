package model.implementations.components.manifests;


import IO.utils.StreamsUtils;
import constants.Hashes;
import model.exceptions.ManifestNotMadeException;
import model.implementations.utils.Location;
import model.implementations.utils.URLLocation;
import org.skyscreamer.jsonassert.JSONAssert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AtomManifestTest {

    private static final String EXPECT_JSON_MANIFEST =
            "{\"Type\":\"Atom\"," +
                    "\"ManifestGUID\":\"b57ac21f9edc88e961ed8c60700e1b5f9d202aa1\"," +
                    "\"ContentGUID\":" + Hashes.TEST_HTTP_BIN_STRING_HASHES + "," +
                    "\"Locations\":[\"" + Hashes.TEST_HTTP_BIN_URL + "\"]" +
                    "}";

    @Test (expectedExceptions = ManifestNotMadeException.class)
    public void testNoLocations() throws IOException, ManifestNotMadeException {
        Collection<Location> locations = (Collection<Location>) mock(Collection.class);
        when(locations.iterator())
                .thenReturn(Collections.emptyIterator());

        AtomManifest atomManifest = ManifestFactory.createAtomManifest(locations);
    }

    @Test
    public void testGetGUIDContent() throws Exception {

    }

    @Test
    public void testGetLocations() throws Exception {
        Location locationMocked = mock(Location.class);

        InputStream inputStreamFake = StreamsUtils.StringToInputStream(Hashes.TEST_STRING);
        when(locationMocked.getSource())
                .thenReturn(inputStreamFake);

        Collection<Location> locations = new ArrayList<Location>();
        locations.add(locationMocked);
        AtomManifest atomManifest = ManifestFactory.createAtomManifest(locations);

        assertEquals(atomManifest.getLocations(), locations);
    }

    @Test
    public void testToJSON() throws Exception {
        Location location = new URLLocation(Hashes.TEST_HTTP_BIN_URL);

        Collection<Location> locations = new ArrayList<Location>();
        locations.add(location);
        AtomManifest atomManifest = ManifestFactory.createAtomManifest(locations);

        JSONAssert.assertEquals(EXPECT_JSON_MANIFEST, atomManifest.toJSON().toString(), true);
    }
}