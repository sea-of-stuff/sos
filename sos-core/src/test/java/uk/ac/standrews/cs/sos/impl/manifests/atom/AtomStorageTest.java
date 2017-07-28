package uk.ac.standrews.cs.sos.impl.manifests.atom;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.castore.exceptions.StorageException;
import uk.ac.standrews.cs.guid.ALGORITHM;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.SetUpTest;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.locations.URILocation;
import uk.ac.standrews.cs.sos.impl.locations.bundles.BundleTypes;
import uk.ac.standrews.cs.sos.impl.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.model.Location;
import uk.ac.standrews.cs.sos.utils.HelperTest;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.AssertJUnit.assertNull;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AtomStorageTest extends SetUpTest {

    private final static IGUID NODE_GUID = GUIDFactory.generateRandomGUID(ALGORITHM.SHA256);

    private AtomStorage atomStorage;

    @BeforeMethod
    public void setUp(Method method) throws Exception {
        super.setUp(method);

        atomStorage = new AtomStorage(NODE_GUID, localStorage);
    }

    @AfterMethod
    public void tearDown() throws DataStorageException {
        localStorage.destroy();
    }
    
    @Test
    public void testStoreAtomNullBundles() throws Exception {
        Location location = HelperTest.createDummyDataFile(localStorage);
        Set<LocationBundle> bundles = null;

        IGUID guid = atomStorage.cacheAtomAndUpdateLocationBundles(location, bundles);
        assertNotNull(guid);
        assertNull(bundles);
    }

    @Test
    public void testStoreAtomNullBundlesStream() throws Exception {
        InputStream inputStream = HelperTest.StringToInputStream("Test-String");
        Set<LocationBundle> bundles = null;

        IGUID guid = atomStorage.cacheAtomAndUpdateLocationBundles(inputStream, bundles);
        assertNotNull(guid);
        assertNull(bundles);
    }

    @Test
    public void testStoreAtom() throws Exception {
        Location location = HelperTest.createDummyDataFile(localStorage);
        Set<LocationBundle> bundles = new LinkedHashSet<>();

        IGUID guid = atomStorage.cacheAtomAndUpdateLocationBundles(location, bundles);
        assertNotNull(guid);
        assertEquals(bundles.size(), 1);
    }

    @Test
    public void testStoreRemoteAtom() throws Exception {
        Location location = new URILocation("http://www.eastcottvets.co.uk/uploads/Animals/gingerkitten.jpg");
        Set<LocationBundle> bundles = new LinkedHashSet<>();

        IGUID guid = atomStorage.cacheAtomAndUpdateLocationBundles(location, bundles);
        assertNotNull(guid);
        assertEquals(bundles.size(), 1);
    }

    @Test (expectedExceptions = StorageException.class)
    public void testStoreAtomFromNullLocation() throws Exception {
        Location location = null;
        Set<LocationBundle> bundles = new LinkedHashSet<>();

        IGUID guid = atomStorage.cacheAtomAndUpdateLocationBundles(location, bundles);
        assertNotNull(guid);
        assertEquals(bundles.size(), 1);
    }


    @Test
    public void testStoreAtomFromStream() throws Exception {
        Set<LocationBundle> bundles = new LinkedHashSet<>();
        InputStream inputStream = HelperTest.StringToInputStream("Test-String");
        IGUID guid = atomStorage.cacheAtomAndUpdateLocationBundles(inputStream, bundles);
        assertNotNull(guid);
        assertEquals(bundles.size(), 1);
    }

    @Test (expectedExceptions = StorageException.class)
    public void testStoreAtomFromNullStream() throws Exception {
        Set<LocationBundle> locations = new LinkedHashSet<>();
        InputStream inputStream = null;
        atomStorage.cacheAtomAndUpdateLocationBundles(inputStream, locations);
    }

    @Test
    public void testStoreAtomFromEmptyStream() throws Exception {
        Set<LocationBundle> bundles = new LinkedHashSet<>();
        InputStream inputStream = HelperTest.StringToInputStream("");
        IGUID guid = atomStorage.cacheAtomAndUpdateLocationBundles(inputStream, bundles);
        assertNotNull(guid);
        assertEquals(bundles.size(), 1);
    }

    @Test
    public void testStoreAtomFromTwoEqualStreams() throws Exception {
        Set<LocationBundle> bundles = new LinkedHashSet<>();
        InputStream inputStream = HelperTest.StringToInputStream("Test-String");
        IGUID guid = atomStorage.cacheAtomAndUpdateLocationBundles(inputStream, bundles);
        assertNotNull(guid);
        assertEquals(bundles.size(), 1);

        Set<LocationBundle> newBundles = new LinkedHashSet<>();
        InputStream twinInputStream = HelperTest.StringToInputStream("Test-String");
        IGUID newGUID = atomStorage.cacheAtomAndUpdateLocationBundles(twinInputStream, newBundles);
        assertNotNull(guid);
        assertEquals(newGUID, guid);
        assertEquals(newBundles.size(), 1);
    }

    @Test
    public void testStorePersistAtom() throws Exception {
        Location location = HelperTest.createDummyDataFile(localStorage);
        Set<LocationBundle> bundles = new LinkedHashSet<>();

        IGUID guid = atomStorage.persistAtomAndUpdateLocationBundles(location, bundles);
        assertNotNull(guid);
        assertEquals(bundles.size(), 1);

        Iterator<LocationBundle> it = bundles.iterator();
        assertEquals(it.next().getType(), BundleTypes.PERSISTENT);
    }

    @Test
    public void testStorePersistAtomFromStream() throws Exception {
        Set<LocationBundle> bundles = new LinkedHashSet<>();
        InputStream inputStream = HelperTest.StringToInputStream("Test-String");
        IGUID guid = atomStorage.persistAtomAndUpdateLocationBundles(inputStream, bundles);
        assertNotNull(guid);
        assertEquals(bundles.size(), 1);

        Iterator<LocationBundle> it = bundles.iterator();
        assertEquals(it.next().getType(), BundleTypes.PERSISTENT);
    }

}