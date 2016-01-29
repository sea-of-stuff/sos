package uk.ac.standrews.cs.sos.model.implementations.components.manifests;

import org.apache.commons.io.FileUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.configurations.SeaConfiguration;
import uk.ac.standrews.cs.sos.configurations.TestConfiguration;
import uk.ac.standrews.cs.sos.model.implementations.utils.Location;
import uk.ac.standrews.cs.utils.Helper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class DataStorageTest {

    private SeaConfiguration configuration;

    @BeforeMethod
    public void setUp() {
        configuration = new TestConfiguration();
    }

    @AfterMethod
    public void tearDown() throws IOException {
        FileUtils.cleanDirectory(new File(configuration.getCacheDataPath()));
        FileUtils.cleanDirectory(new File(configuration.getDataPath()));
    }

    @Test
    public void testStoreAtom() throws Exception {
        Collection<Location> locations = new ArrayList<Location>();
        Location location = Helper.createDummyDataFile(configuration);
        locations.add(location);

        Collection<Location> newLocations = DataStorage.storeAtom(configuration, locations);
        assertFalse(newLocations.contains(location));
        assertEquals(newLocations.size(), 1);
    }

    @Test
    public void testStoreRemoteAtom() throws Exception {
        Collection<Location> locations = new ArrayList<Location>();
        Location location = new Location("http://www.eastcottvets.co.uk/uploads/Animals/gingerkitten.jpg");
        locations.add(location);

        Collection<Location> newLocations = DataStorage.storeAtom(configuration, locations);
        assertEquals(newLocations.size(), 2);
    }

    @Test
    public void testStoreAtomAlreadyCached() throws Exception {
        Collection<Location> locations = new ArrayList<Location>();
        Location location = Helper.createDummyDataFile(configuration);
        locations.add(location);

        DataStorage.storeAtom(configuration, locations);
        Collection<Location> newLocations = DataStorage.storeAtom(configuration, locations);

        assertFalse(newLocations.contains(location));
        assertEquals(newLocations.size(), 1);
    }

}