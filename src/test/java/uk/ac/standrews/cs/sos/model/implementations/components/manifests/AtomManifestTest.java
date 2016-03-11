package uk.ac.standrews.cs.sos.model.implementations.components.manifests;


import com.google.gson.JsonArray;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.SetUpTest;
import uk.ac.standrews.cs.constants.Hashes;
import uk.ac.standrews.cs.sos.configurations.SeaConfiguration;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.model.implementations.locations.Location;
import uk.ac.standrews.cs.sos.model.implementations.locations.URILocation;
import uk.ac.standrews.cs.sos.model.implementations.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.model.implementations.locations.bundles.ProvenanceLocationBundle;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUIDsha1;
import uk.ac.standrews.cs.utils.Helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import static org.testng.Assert.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AtomManifestTest extends SetUpTest {

    private SeaConfiguration configuration;

    @BeforeMethod
    public void setUp() throws IOException {
        configuration = SeaConfiguration.getInstance();
    }

    @AfterMethod
    public void tearDown() throws IOException {
        Helper.cleanDirectory(configuration.getCacheDataPath());
        Helper.cleanDirectory(configuration.getDataPath());
    }

    @Test
    public void testNoLocations() throws IOException, ManifestNotMadeException, DataStorageException {
        Collection<LocationBundle> bundles = new ArrayList<>();
        AtomManifest atomManifest = ManifestFactory.createAtomManifest(new GUIDsha1(Hashes.TEST_STRING_HASHED), bundles);

        Collection<LocationBundle> others = atomManifest.getLocations();
        assertEquals(others, bundles);
    }

    @Test
    public void testGetLocations() throws Exception {
        Collection<LocationBundle> bundles = new ArrayList<>();
        LocationBundle bundle = Helper.createDummyDataFile(configuration);
        bundles.add(bundle);
        AtomManifest atomManifest = ManifestFactory.createAtomManifest(new GUIDsha1(Hashes.TEST_STRING_HASHED), bundles);

        Collection<LocationBundle> others = atomManifest.getLocations();
        assertEquals(others, bundles);
    }

    @Test (timeOut = 10000)
    public void testToJSON() throws Exception {
        Location location = new URILocation(Hashes.TEST_HTTP_BIN_URL);
        LocationBundle bundle = new ProvenanceLocationBundle(location);
        Collection<LocationBundle> bundles = new ArrayList<>();
        bundles.add(bundle);
        AtomManifest atomManifest = ManifestFactory.createAtomManifest(new GUIDsha1(Hashes.TEST_HTTP_BIN_HASH), bundles);

        Collection<LocationBundle> newBundles = atomManifest.getLocations();
        assertEquals(newBundles.size(), 1);

        JsonArray jsonLocations = atomManifest.toJSON().getAsJsonArray("Locations");
        assertEquals(jsonLocations.size(), 1);
    }

    @Test (timeOut = 10000)
    public void testIsValid() throws Exception {
        Location location = new URILocation(Hashes.TEST_HTTP_BIN_URL);
        LocationBundle bundle = new ProvenanceLocationBundle(location);
        Collection<LocationBundle> bundles = new ArrayList<>();
        bundles.add(bundle);
        AtomManifest atomManifest = ManifestFactory.createAtomManifest(new GUIDsha1(Hashes.TEST_HTTP_BIN_HASH), bundles);

        assertEquals(atomManifest.isValid(), true);
    }

    @Test (timeOut = 10000)
    public void testIsVerified() throws Exception {
        Location location = new URILocation(Hashes.TEST_HTTP_BIN_URL);
        LocationBundle bundle = new ProvenanceLocationBundle(location);
        Collection<LocationBundle> bundles = new ArrayList<>();
        bundles.add(bundle);
        AtomManifest atomManifest = ManifestFactory.createAtomManifest(new GUIDsha1(Hashes.TEST_HTTP_BIN_HASH), bundles);

        assertEquals(atomManifest.verify(null), true);
    }
}