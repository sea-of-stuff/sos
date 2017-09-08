package uk.ac.standrews.cs.sos.impl.manifests.atom;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.castore.CastoreBuilder;
import uk.ac.standrews.cs.castore.CastoreFactory;
import uk.ac.standrews.cs.castore.CastoreType;
import uk.ac.standrews.cs.castore.exceptions.StorageException;
import uk.ac.standrews.cs.castore.interfaces.IDirectory;
import uk.ac.standrews.cs.castore.interfaces.IFile;
import uk.ac.standrews.cs.castore.interfaces.IStorage;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.locations.URILocation;
import uk.ac.standrews.cs.sos.impl.locations.bundles.CacheLocationBundle;
import uk.ac.standrews.cs.sos.impl.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.impl.locations.bundles.PersistLocationBundle;
import uk.ac.standrews.cs.sos.impl.manifests.directory.LocationsIndexImpl;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.interfaces.manifests.LocationsIndex;
import uk.ac.standrews.cs.sos.utils.Persistence;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Iterator;

import static org.testng.Assert.*;

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

        Iterator<LocationBundle> it = locationsIndex.findLocations(guid).iterator();
        assertTrue(it.hasNext());

        LocationBundle indexedLocationBundle = it.next();
        assertEquals(indexedLocationBundle, locationBundle);
    }

    @Test
    public void noLocationsTest() throws URISyntaxException {
        LocationsIndex locationsIndex = new LocationsIndexImpl();

        IGUID guid = GUIDFactory.generateRandomGUID();

        Iterator<LocationBundle> it = locationsIndex.findLocations(guid).iterator();
        assertFalse(it.hasNext());
    }

    @Test
    public void persistLocationsIndexTest() throws StorageException, DataStorageException, URISyntaxException, IOException, ClassNotFoundException {

        String root = System.getProperty("user.home") + "/sos/";

        CastoreBuilder castoreBuilder = new CastoreBuilder()
                .setType(CastoreType.LOCAL)
                .setRoot(root);
        IStorage stor = CastoreFactory.createStorage(castoreBuilder);
        LocalStorage localStorage = new LocalStorage(stor);

        LocationsIndex locationsIndex = new LocationsIndexImpl();

        IGUID guid = GUIDFactory.generateRandomGUID();
        LocationBundle locationBundle = new CacheLocationBundle(new URILocation("http://example.org/resource"));

        locationsIndex.addLocation(guid, locationBundle);

        IDirectory cachesDir = localStorage.getNodeDirectory();
        IFile file = localStorage.createFile(cachesDir, "locations.index");
        locationsIndex.persist(file);


        LocationsIndex locationsIndexPersisted = (LocationsIndex) Persistence.Load(file);
        assertNotNull(locationsIndexPersisted);

        Iterator<LocationBundle> it = locationsIndexPersisted.findLocations(guid).iterator();
        assertTrue(it.hasNext());
        assertEquals(locationBundle, it.next());
    }

    @Test
    public void iteratorOrderingTest() throws URISyntaxException {
        LocationsIndex locationsIndex = new LocationsIndexImpl();

        IGUID guid = GUIDFactory.generateRandomGUID();
        LocationBundle locationBundle = new CacheLocationBundle(new URILocation("http://example.org/resource"));
        LocationBundle locationBundlePersist = new PersistLocationBundle(new URILocation("http://example.org/persist"));

        locationsIndex.addLocation(guid, locationBundlePersist);
        locationsIndex.addLocation(guid, locationBundle);

        Iterator<LocationBundle> it = locationsIndex.findLocations(guid).iterator();
        assertTrue(it.hasNext());

        LocationBundle indexedLocationBundle = it.next();
        assertEquals(indexedLocationBundle, locationBundle);

        LocationBundle indexedLocationBundle2 = it.next();
        assertEquals(indexedLocationBundle2, locationBundlePersist);
    }

}
