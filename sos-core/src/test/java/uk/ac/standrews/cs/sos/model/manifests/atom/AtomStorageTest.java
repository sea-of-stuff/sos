package uk.ac.standrews.cs.sos.model.manifests.atom;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.interfaces.locations.Location;
import uk.ac.standrews.cs.sos.model.locations.URILocation;
import uk.ac.standrews.cs.sos.model.locations.bundles.BundleTypes;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.model.locations.sos.SOSURLStreamHandlerFactory;
import uk.ac.standrews.cs.sos.model.storage.InternalStorage;
import uk.ac.standrews.cs.sos.network.RequestsManager;
import uk.ac.standrews.cs.sos.node.NodeManager;
import uk.ac.standrews.cs.sos.utils.HelperTest;
import uk.ac.standrews.cs.storage.StorageFactory;
import uk.ac.standrews.cs.storage.StorageType;
import uk.ac.standrews.cs.storage.exceptions.StorageException;

import java.io.InputStream;
import java.net.URL;
import java.net.URLStreamHandlerFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.AssertJUnit.assertNull;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AtomStorageTest {

    private AtomStorage atomStorage;
    private InternalStorage internalStorage;

    @BeforeMethod
    public void setUp() throws Exception {

        try {
            String location = System.getProperty("user.home") + "/sos";
            internalStorage =
                    new InternalStorage(StorageFactory
                            .createStorage(StorageType.LOCAL, location, true));
        } catch (StorageException | DataStorageException e) {
            throw new SOSException(e);
        }

        atomStorage = new AtomStorage(GUIDFactory.generateRandomGUID(), internalStorage);

        registerSOSProtocol();
    }

    private void registerSOSProtocol() throws SOSProtocolException {
        NodeManager nodeManagerMock = mock(NodeManager.class);
        RequestsManager requestsManagerMock = mock(RequestsManager.class);

        try {
            if (!SOSURLStreamHandlerFactory.URLStreamHandlerFactoryIsSet) {
                URLStreamHandlerFactory urlStreamHandlerFactory =
                        new SOSURLStreamHandlerFactory(internalStorage, nodeManagerMock, requestsManagerMock);
                URL.setURLStreamHandlerFactory(urlStreamHandlerFactory);
            }
        } catch (Error e) {
            throw new SOSProtocolException(e);
        }
    }

    @AfterMethod
    public void tearDown() throws DataStorageException {
        internalStorage.destroy();
    }
    
    @Test
    public void testStoreAtomNullBundles() throws Exception {
        Location location = HelperTest.createDummyDataFile(internalStorage);
        Collection<LocationBundle> bundles = null;

        IGUID guid = atomStorage.cacheAtomAndUpdateLocationBundles(location, bundles);
        assertNotNull(guid);
        assertNull(bundles);
    }

    @Test
    public void testStoreAtomNullBundlesStream() throws Exception {
        InputStream inputStream = HelperTest.StringToInputStream("Test-String");
        Collection<LocationBundle> bundles = null;

        IGUID guid = atomStorage.cacheAtomAndUpdateLocationBundles(inputStream, bundles);
        assertNotNull(guid);
        assertNull(bundles);
    }

    @Test
    public void testStoreAtom() throws Exception {
        Location location = HelperTest.createDummyDataFile(internalStorage);
        Collection<LocationBundle> bundles = new ArrayList<>();

        IGUID guid = atomStorage.cacheAtomAndUpdateLocationBundles(location, bundles);
        assertNotNull(guid);
        assertEquals(bundles.size(), 1);
    }

    @Test
    public void testStoreRemoteAtom() throws Exception {
        Location location = new URILocation("http://www.eastcottvets.co.uk/uploads/Animals/gingerkitten.jpg");
        Collection<LocationBundle> bundles = new ArrayList<>();

        IGUID guid = atomStorage.cacheAtomAndUpdateLocationBundles(location, bundles);
        assertNotNull(guid);
        assertEquals(bundles.size(), 1);
    }

    @Test (expectedExceptions = StorageException.class)
    public void testStoreAtomFromNullLocation() throws Exception {
        Location location = null;
        Collection<LocationBundle> bundles = new ArrayList<>();

        IGUID guid = atomStorage.cacheAtomAndUpdateLocationBundles(location, bundles);
        assertNotNull(guid);
        assertEquals(bundles.size(), 1);
    }


    @Test
    public void testStoreAtomFromStream() throws Exception {
        Collection<LocationBundle> bundles = new ArrayList<>();
        InputStream inputStream = HelperTest.StringToInputStream("Test-String");
        IGUID guid = atomStorage.cacheAtomAndUpdateLocationBundles(inputStream, bundles);
        assertNotNull(guid);
        assertEquals(bundles.size(), 1);
    }

    @Test (expectedExceptions = StorageException.class)
    public void testStoreAtomFromNullStream() throws Exception {
        Collection<LocationBundle> locations = new ArrayList<>();
        InputStream inputStream = null;
        atomStorage.cacheAtomAndUpdateLocationBundles(inputStream, locations);
    }

    @Test
    public void testStoreAtomFromEmptyStream() throws Exception {
        Collection<LocationBundle> bundles = new ArrayList<>();
        InputStream inputStream = HelperTest.StringToInputStream("");
        IGUID guid = atomStorage.cacheAtomAndUpdateLocationBundles(inputStream, bundles);
        assertNotNull(guid);
        assertEquals(bundles.size(), 1);
    }

    @Test
    public void testStoreAtomFromTwoEqualStreams() throws Exception {
        Collection<LocationBundle> bundles = new ArrayList<>();
        InputStream inputStream = HelperTest.StringToInputStream("Test-String");
        IGUID guid = atomStorage.cacheAtomAndUpdateLocationBundles(inputStream, bundles);
        assertNotNull(guid);
        assertEquals(bundles.size(), 1);

        Collection<LocationBundle> newBundles = new ArrayList<>();
        InputStream twinInputStream = HelperTest.StringToInputStream("Test-String");
        IGUID newGUID = atomStorage.cacheAtomAndUpdateLocationBundles(twinInputStream, newBundles);
        assertNotNull(guid);
        assertEquals(newGUID, guid);
        assertEquals(newBundles.size(), 1);
    }

    @Test
    public void testStorePersistAtom() throws Exception {
        Location location = HelperTest.createDummyDataFile(internalStorage);
        Collection<LocationBundle> bundles = new ArrayList<>();

        IGUID guid = atomStorage.persistAtomAndUpdateLocationBundles(location, bundles);
        assertNotNull(guid);
        assertEquals(bundles.size(), 1);

        Iterator<LocationBundle> it = bundles.iterator();
        assertEquals(it.next().getType(), BundleTypes.PERSISTENT);
    }

    @Test
    public void testStorePersistAtomFromStream() throws Exception {
        Collection<LocationBundle> bundles = new ArrayList<>();
        InputStream inputStream = HelperTest.StringToInputStream("Test-String");
        IGUID guid = atomStorage.persistAtomAndUpdateLocationBundles(inputStream, bundles);
        assertNotNull(guid);
        assertEquals(bundles.size(), 1);

        Iterator<LocationBundle> it = bundles.iterator();
        assertEquals(it.next().getType(), BundleTypes.PERSISTENT);
    }

}