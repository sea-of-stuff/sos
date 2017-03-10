package uk.ac.standrews.cs.sos.model.manifests;


import com.fasterxml.jackson.databind.JsonNode;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.sos.CommonTest;
import uk.ac.standrews.cs.sos.constants.Hashes;
import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestVerificationException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.interfaces.locations.Location;
import uk.ac.standrews.cs.sos.model.locations.URILocation;
import uk.ac.standrews.cs.sos.model.locations.bundles.CacheLocationBundle;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.model.locations.bundles.ProvenanceLocationBundle;
import uk.ac.standrews.cs.sos.storage.LocalStorage;
import uk.ac.standrews.cs.sos.utils.HelperTest;
import uk.ac.standrews.cs.sos.utils.JSONHelper;
import uk.ac.standrews.cs.storage.StorageFactory;
import uk.ac.standrews.cs.storage.StorageType;
import uk.ac.standrews.cs.storage.exceptions.StorageException;

import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AtomManifestTest extends CommonTest {

    private LocalStorage localStorage;

    @BeforeMethod
    public void setUp(Method testMethod) throws Exception {
        super.setUp(testMethod);

        try {
            String location = System.getProperty("user.home") + "/sos";
            localStorage =
                    new LocalStorage(StorageFactory
                            .createStorage(StorageType.LOCAL, location));
        } catch (StorageException | DataStorageException e) {
            throw new SOSException(e);
        }

    }

    @AfterMethod
    public void tearDown() throws DataStorageException {
        localStorage.destroy();
    }

    @Test
    public void testNoLocations() throws Exception {
        Set<LocationBundle> bundles = new LinkedHashSet<>();
        AtomManifest atomManifest = ManifestFactory.createAtomManifest(GUIDFactory.recreateGUID(Hashes.TEST_STRING_HASHED), bundles);

        Set<LocationBundle> others = atomManifest.getLocations();
        assertEquals(others, bundles);
    }

    @Test
    public void testNullGUID() throws Exception {
        Set<LocationBundle> bundles = new LinkedHashSet<>();
        Location location = HelperTest.createDummyDataFile(localStorage);
        bundles.add(new CacheLocationBundle(location));
        AtomManifest atomManifest = ManifestFactory.createAtomManifest(null, bundles);

        Set<LocationBundle> others = atomManifest.getLocations();
        assertEquals(others, bundles);
        assertFalse(atomManifest.isValid());
    }

    @Test
    public void testGetLocations() throws Exception {
        Set<LocationBundle> bundles = new LinkedHashSet<>();
        Location location = HelperTest.createDummyDataFile(localStorage);
        bundles.add(new CacheLocationBundle(location));
        AtomManifest atomManifest = ManifestFactory.createAtomManifest(GUIDFactory.recreateGUID(Hashes.TEST_STRING_HASHED), bundles);

        Set<LocationBundle> others = atomManifest.getLocations();
        assertEquals(others, bundles);
    }

    @Test (timeOut = 10000)
    public void testToJSON() throws Exception {
        Location location = new URILocation(Hashes.TEST_HTTP_BIN_URL);
        LocationBundle bundle = new ProvenanceLocationBundle(location);
        Set<LocationBundle> bundles = new LinkedHashSet<>();
        bundles.add(bundle);
        AtomManifest atomManifest = ManifestFactory.createAtomManifest(GUIDFactory.recreateGUID(Hashes.TEST_HTTP_BIN_HASH), bundles);

        Set<LocationBundle> newBundles = atomManifest.getLocations();
        assertEquals(newBundles.size(), 1);

        JsonNode node = JSONHelper.JsonObjMapper().readTree(atomManifest.toString());
        JsonNode locationsNode = node.get(ManifestConstants.KEY_LOCATIONS);
        assertTrue(locationsNode.isArray());
        assertEquals(locationsNode.size(), 1);
    }

    @Test (timeOut = 10000)
    public void testIsValid() throws Exception {
        Location location = new URILocation(Hashes.TEST_HTTP_BIN_URL);
        LocationBundle bundle = new ProvenanceLocationBundle(location);
        Set<LocationBundle> bundles = new LinkedHashSet<>();
        bundles.add(bundle);
        AtomManifest atomManifest = ManifestFactory.createAtomManifest(GUIDFactory.recreateGUID(Hashes.TEST_HTTP_BIN_HASH), bundles);

        assertEquals(atomManifest.isValid(), true);
    }

    @Test (timeOut = 10000)
    public void testIsVerified() throws Exception {
        Location location = new URILocation(Hashes.TEST_HTTP_BIN_URL);
        LocationBundle bundle = new ProvenanceLocationBundle(location);
        Set<LocationBundle> bundles = new LinkedHashSet<>();
        bundles.add(bundle);
        AtomManifest atomManifest = ManifestFactory.createAtomManifest(GUIDFactory.recreateGUID(Hashes.TEST_HTTP_BIN_HASH), bundles);

        assertEquals(atomManifest.verifySignature(null), true);
    }

    @Test
    public void verifyAtomWithNullGUIDTest() throws ManifestVerificationException {
        Set<LocationBundle> bundles = new LinkedHashSet<>();
        AtomManifest atomManifest = ManifestFactory.createAtomManifest(null, bundles);

        assertFalse(atomManifest.verifySignature(null));
    }
}