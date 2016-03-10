package uk.ac.standrews.cs.sos.model.implementations.components.manifests;

import org.apache.commons.io.FileUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.SetUpTest;
import uk.ac.standrews.cs.sos.configurations.SeaConfiguration;
import uk.ac.standrews.cs.sos.model.implementations.locations.Location;
import uk.ac.standrews.cs.sos.model.implementations.locations.URILocation;
import uk.ac.standrews.cs.sos.model.implementations.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.model.implementations.locations.bundles.ProvenanceLocationBundle;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUID;
import uk.ac.standrews.cs.utils.Helper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class CacheDataStorageTest extends SetUpTest {

    private SeaConfiguration configuration;

    @BeforeMethod
    public void setUp() throws IOException {
        configuration = SeaConfiguration.getInstance();
    }

    @AfterMethod
    public void tearDown() throws IOException {
        FileUtils.cleanDirectory(new File(configuration.getCacheDataPath()));
        FileUtils.cleanDirectory(new File(configuration.getDataPath()));
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

}