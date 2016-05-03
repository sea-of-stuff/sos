package uk.ac.standrews.cs.sos.model.storage;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.SetUpTest;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.interfaces.locations.Location;
import uk.ac.standrews.cs.sos.model.locations.URILocation;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.utils.Helper;
import uk.ac.standrews.cs.utils.StreamsUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.AssertJUnit.assertNull;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class DataStorageHelperTest extends SetUpTest {

    @AfterMethod
    public void tearDown() throws IOException {
        Helper.cleanDirectory(configuration.getCacheDirectory());
        Helper.cleanDirectory(configuration.getDataDirectory());
    }

    @Test
    public void testStoreAtomNullBundles() throws Exception {
        Location location = Helper.createDummyDataFile(configuration);
        Collection<LocationBundle> bundles = null;

        IGUID guid = DataStorageHelper.cacheAtomAndUpdateLocationBundles(configuration, location, bundles);
        assertNotNull(guid);
        assertNull(bundles);
    }

    @Test
    public void testStoreAtomNullBundlesStream() throws Exception {
        InputStream inputStream = StreamsUtils.StringToInputStream("Test-String");
        Collection<LocationBundle> bundles = null;

        IGUID guid = DataStorageHelper.cacheAtomAndUpdateLocationBundles(configuration, inputStream, bundles);
        assertNotNull(guid);
        assertNull(bundles);
    }

    @Test
    public void testStoreAtom() throws Exception {
        Location location = Helper.createDummyDataFile(configuration);
        Collection<LocationBundle> bundles = new ArrayList<>();

        IGUID guid = DataStorageHelper.cacheAtomAndUpdateLocationBundles(configuration, location, bundles);
        assertNotNull(guid);
        assertEquals(bundles.size(), 1);
    }

    @Test
    public void testStoreRemoteAtom() throws Exception {
        Location location = new URILocation("http://www.eastcottvets.co.uk/uploads/Animals/gingerkitten.jpg");
        Collection<LocationBundle> bundles = new ArrayList<>();

        IGUID guid = DataStorageHelper.cacheAtomAndUpdateLocationBundles(configuration, location, bundles);
        assertNotNull(guid);
        assertEquals(bundles.size(), 1);
    }

    @Test (expectedExceptions = DataStorageException.class)
    public void testStoreAtomFromNullLocation() throws Exception {
        Location location = null;
        Collection<LocationBundle> bundles = new ArrayList<>();

        IGUID guid = DataStorageHelper.cacheAtomAndUpdateLocationBundles(configuration, location, bundles);
        assertNotNull(guid);
        assertEquals(bundles.size(), 1);
    }


    @Test
    public void testStoreAtomFromStream() throws Exception {
        Collection<LocationBundle> bundles = new ArrayList<>();
        InputStream inputStream = StreamsUtils.StringToInputStream("Test-String");
        IGUID guid = DataStorageHelper.cacheAtomAndUpdateLocationBundles(configuration, inputStream, bundles);
        assertNotNull(guid);
        assertEquals(bundles.size(), 1);
    }

    @Test (expectedExceptions = DataStorageException.class)
    public void testStoreAtomFromNullStream() throws Exception {
        Collection<LocationBundle> locations = new ArrayList<>();
        InputStream inputStream = null;
        DataStorageHelper.cacheAtomAndUpdateLocationBundles(configuration, inputStream, locations);
    }

    @Test
    public void testStoreAtomFromEmptyStream() throws Exception {
        Collection<LocationBundle> bundles = new ArrayList<>();
        InputStream inputStream = StreamsUtils.StringToInputStream("");
        IGUID guid = DataStorageHelper.cacheAtomAndUpdateLocationBundles(configuration, inputStream, bundles);
        assertNotNull(guid);
        assertEquals(bundles.size(), 1);
    }

    @Test
    public void testStoreAtomFromTwoEqualStreams() throws Exception {
        Collection<LocationBundle> bundles = new ArrayList<>();
        InputStream inputStream = StreamsUtils.StringToInputStream("Test-String");
        IGUID guid = DataStorageHelper.cacheAtomAndUpdateLocationBundles(configuration, inputStream, bundles);
        assertNotNull(guid);
        assertEquals(bundles.size(), 1);

        Collection<LocationBundle> newBundles = new ArrayList<>();
        InputStream twinInputStream = StreamsUtils.StringToInputStream("Test-String");
        IGUID newGUID = DataStorageHelper.cacheAtomAndUpdateLocationBundles(configuration, twinInputStream, newBundles);
        assertNotNull(guid);
        assertEquals(newGUID, guid);
        assertEquals(newBundles.size(), 1);
    }

}