package uk.ac.standrews.cs.sos.model.implementations.components.manifests;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.IO.utils.StreamsUtils;
import uk.ac.standrews.cs.SetUpTest;
import uk.ac.standrews.cs.sos.configurations.SeaConfiguration;
import uk.ac.standrews.cs.sos.exceptions.SeaConfigurationException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.model.implementations.locations.Location;
import uk.ac.standrews.cs.sos.model.implementations.locations.SOSURLStreamHandlerFactory;
import uk.ac.standrews.cs.sos.model.implementations.locations.URILocation;
import uk.ac.standrews.cs.sos.model.implementations.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.model.implementations.locations.bundles.ProvenanceLocationBundle;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUID;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUIDsha1;
import uk.ac.standrews.cs.utils.Helper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class DataStorageHelperTest extends SetUpTest {

    private SeaConfiguration configuration;

    @BeforeMethod
    public void setUp() throws IOException, SeaConfigurationException {
        configuration = SeaConfiguration.getInstance();
        configuration.setNodeId(new GUIDsha1("123456"));

        try {
            URL.setURLStreamHandlerFactory(new SOSURLStreamHandlerFactory());
        } catch (Error e) {
            // Error is thrown if factory was already setup in previous tests
        }
    }

    @AfterMethod
    public void tearDown() throws IOException {
        Helper.cleanDirectory(configuration.getCacheDataPath());
        Helper.cleanDirectory(configuration.getDataPath());
    }

    @Test
    public void testStoreAtom() throws Exception {
        Collection<LocationBundle> locations = new ArrayList<>();
        LocationBundle bundle = Helper.createDummyDataFile(configuration);
        locations.add(bundle);

        GUID guid = DataStorageHelper.cacheAtomAndUpdateLocationBundles(configuration, locations);
        assertNotNull(guid);
        assertTrue(locations.contains(bundle));
        assertEquals(locations.size(), 2);
    }

    @Test
    public void testStoreRemoteAtom() throws Exception {
        Collection<LocationBundle> locations = new ArrayList<>();
        Location location = new URILocation("http://www.eastcottvets.co.uk/uploads/Animals/gingerkitten.jpg");
        LocationBundle bundle = new ProvenanceLocationBundle(location);
        locations.add(bundle);

        GUID guid = DataStorageHelper.cacheAtomAndUpdateLocationBundles(configuration, locations);
        assertNotNull(guid);
        assertEquals(locations.size(), 2);
    }

    @Test
    public void testStoreAtomFromStream() throws Exception {
        Collection<LocationBundle> locations = new ArrayList<>();
        InputStream inputStream = StreamsUtils.StringToInputStream("Test-String");
        GUID guid = DataStorageHelper.cacheAtomAndUpdateLocationBundles(configuration, inputStream, locations);
        assertNotNull(guid);
        assertEquals(locations.size(), 1);
    }

    @Test (expectedExceptions = DataStorageException.class)
    public void testStoreAtomFromNullStream() throws Exception {
        Collection<LocationBundle> locations = new ArrayList<>();
        InputStream inputStream = null;
        DataStorageHelper.cacheAtomAndUpdateLocationBundles(configuration, inputStream, locations);
    }

    @Test
    public void testStoreAtomFromTwoEqualStreams() throws Exception {
        Collection<LocationBundle> locations = new ArrayList<>();
        InputStream inputStream = StreamsUtils.StringToInputStream("Test-String");
        GUID guid = DataStorageHelper.cacheAtomAndUpdateLocationBundles(configuration, inputStream, locations);
        assertNotNull(guid);
        assertEquals(locations.size(), 1);

        Collection<LocationBundle> newLocations = new ArrayList<>();
        InputStream twinInputStream = StreamsUtils.StringToInputStream("Test-String");
        GUID newGUID = DataStorageHelper.cacheAtomAndUpdateLocationBundles(configuration, twinInputStream, newLocations);
        assertNotNull(guid);
        assertEquals(newGUID, guid);
        assertEquals(newLocations.size(), 1);
    }

}