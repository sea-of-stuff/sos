package uk.ac.standrews.cs.sos.model.manifests.atom;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.interfaces.manifests.LocationsIndex;
import uk.ac.standrews.cs.sos.model.locations.URILocation;
import uk.ac.standrews.cs.sos.model.locations.bundles.CacheLocationBundle;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.storage.LocalStorage;
import uk.ac.standrews.cs.storage.StorageFactory;
import uk.ac.standrews.cs.storage.StorageType;
import uk.ac.standrews.cs.storage.exceptions.StorageException;
import uk.ac.standrews.cs.storage.interfaces.Directory;
import uk.ac.standrews.cs.storage.interfaces.File;

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

        Iterator<LocationBundle> it = locationsIndex.findLocations(guid);
        assertTrue(it.hasNext());

        LocationBundle indexedLocationBundle = it.next();
        assertEquals(indexedLocationBundle, locationBundle);
    }

    @Test
    public void persistLocationsIndexTest() throws StorageException, DataStorageException, URISyntaxException, IOException, ClassNotFoundException {
        LocalStorage localStorage = new LocalStorage(StorageFactory.createStorage(StorageType.LOCAL, "~/sos/"));

        LocationsIndex locationsIndex = new LocationsIndexImpl();

        IGUID guid = GUIDFactory.generateRandomGUID();
        LocationBundle locationBundle = new CacheLocationBundle(new URILocation("http://example.org/resource"));

        locationsIndex.addLocation(guid, locationBundle);

        Directory cachesDir = localStorage.getCachesDirectory();
        File file = localStorage.createFile(cachesDir, "locations.index");
        locationsIndex.persist(file);

        LocationsIndex locationsIndexPersisted = LocationsIndexImpl.load(localStorage, file);
        assertNotNull(locationsIndexPersisted);

        Iterator<LocationBundle> it = locationsIndexPersisted.findLocations(guid);
        assertTrue(it.hasNext());
        assertEquals(locationBundle, it.next());
    }


}