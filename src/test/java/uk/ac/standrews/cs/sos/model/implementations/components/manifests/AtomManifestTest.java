package uk.ac.standrews.cs.sos.model.implementations.components.manifests;


import com.google.gson.JsonArray;
import org.apache.commons.io.FileUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.constants.Hashes;
import uk.ac.standrews.cs.sos.configurations.SeaConfiguration;
import uk.ac.standrews.cs.sos.configurations.TestConfiguration;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.model.implementations.locations.OldLocation;
import uk.ac.standrews.cs.utils.Helper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AtomManifestTest {

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

    @Test (expectedExceptions = DataStorageException.class)
    public void testNoLocations() throws IOException, ManifestNotMadeException, DataStorageException {
        Collection<OldLocation> locations = new ArrayList<OldLocation>();
        ManifestFactory.createAtomManifest(configuration, locations);
    }

    @Test
    public void testGetLocations() throws Exception {
        Collection<OldLocation> locations = new ArrayList<OldLocation>();
        OldLocation location = Helper.createDummyDataFile(configuration);
        locations.add(location);
        AtomManifest atomManifest = ManifestFactory.createAtomManifest(configuration, locations);

        // Locations change, since atom is cached.
// FIXME        assertNotEquals(atomManifest.getLocations(), locations);
    }

    @Test (timeOut = 10000)
    public void testToJSON() throws Exception {
        OldLocation location = new OldLocation(Hashes.TEST_HTTP_BIN_URL);
        Collection<OldLocation> locations = new ArrayList<OldLocation>();
        locations.add(location);
        AtomManifest atomManifest = ManifestFactory.createAtomManifest(configuration, locations);

        Collection<OldLocation> newLocations = atomManifest.getLocations();
        assertEquals(newLocations.size(), 2);

        JsonArray jsonLocations = atomManifest.toJSON().getAsJsonArray("Locations");
        assertEquals( jsonLocations.size(), 2);
    }

    @Test (timeOut = 10000)
    public void testIsValid() throws Exception {
        OldLocation location = new OldLocation(Hashes.TEST_HTTP_BIN_URL);
        Collection<OldLocation> locations = new ArrayList<OldLocation>();
        locations.add(location);
        AtomManifest atomManifest = ManifestFactory.createAtomManifest(configuration, locations);

        assertEquals(atomManifest.isValid(), true);
    }

    @Test (timeOut = 10000)
    public void testIsVerified() throws Exception {
        OldLocation location = new OldLocation(Hashes.TEST_HTTP_BIN_URL);
        Collection<OldLocation> locations = new ArrayList<OldLocation>();
        locations.add(location);
        AtomManifest atomManifest = ManifestFactory.createAtomManifest(configuration, locations);

        assertEquals(atomManifest.verify(null), true);
    }
}