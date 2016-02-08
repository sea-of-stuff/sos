package uk.ac.standrews.cs.sos.model.implementations.components.manifests;


import com.google.gson.JsonArray;
import org.apache.commons.io.FileUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.constants.Hashes;
import uk.ac.standrews.cs.sos.configurations.SeaConfiguration;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.model.implementations.locations.Location;
import uk.ac.standrews.cs.sos.model.implementations.locations.LocationBundle;
import uk.ac.standrews.cs.sos.model.implementations.locations.URILocation;
import uk.ac.standrews.cs.utils.Helper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import static org.testng.Assert.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AtomManifestTest {

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

    @Test (expectedExceptions = DataStorageException.class)
    public void testNoLocations() throws IOException, ManifestNotMadeException, DataStorageException {
        Collection<LocationBundle> bundles = new ArrayList<LocationBundle>();
        ManifestFactory.createAtomManifest(configuration, bundles);
    }

    @Test
    public void testGetLocations() throws Exception {
        Collection<LocationBundle> bundles = new ArrayList<LocationBundle>();
        LocationBundle bundle = Helper.createDummyDataFile(configuration);
        bundles.add(bundle);
        AtomManifest atomManifest = ManifestFactory.createAtomManifest(configuration, bundles);

        Collection<LocationBundle> others = atomManifest.getLocations();
        assertEquals(others, bundles);
    }

    @Test (timeOut = 10000)
    public void testToJSON() throws Exception {
        Location location = new URILocation(Hashes.TEST_HTTP_BIN_URL);
        LocationBundle bundle = new LocationBundle("prov", new Location[]{location});
        Collection<LocationBundle> bundles = new ArrayList<LocationBundle>();
        bundles.add(bundle);
        AtomManifest atomManifest = ManifestFactory.createAtomManifest(configuration, bundles);

        Collection<LocationBundle> newBundles = atomManifest.getLocations();
        assertEquals(newBundles.size(), 2);

        JsonArray jsonLocations = atomManifest.toJSON().getAsJsonArray("Locations");
        assertEquals( jsonLocations.size(), 2);
    }

    @Test (timeOut = 10000)
    public void testIsValid() throws Exception {
        Location location = new URILocation(Hashes.TEST_HTTP_BIN_URL);
        LocationBundle bundle = new LocationBundle("prov", new Location[]{location});
        Collection<LocationBundle> bundles = new ArrayList<LocationBundle>();
        bundles.add(bundle);
        AtomManifest atomManifest = ManifestFactory.createAtomManifest(configuration, bundles);

        assertEquals(atomManifest.isValid(), true);
    }

    @Test (timeOut = 10000)
    public void testIsVerified() throws Exception {
        Location location = new URILocation(Hashes.TEST_HTTP_BIN_URL);
        LocationBundle bundle = new LocationBundle("prov", new Location[]{location});
        Collection<LocationBundle> bundles = new ArrayList<LocationBundle>();
        bundles.add(bundle);
        AtomManifest atomManifest = ManifestFactory.createAtomManifest(configuration, bundles);

        assertEquals(atomManifest.verify(null), true);
    }
}