package uk.ac.standrews.cs.sos.impl.datamodel;


import com.fasterxml.jackson.databind.JsonNode;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.castore.CastoreBuilder;
import uk.ac.standrews.cs.castore.CastoreFactory;
import uk.ac.standrews.cs.castore.CastoreType;
import uk.ac.standrews.cs.castore.exceptions.StorageException;
import uk.ac.standrews.cs.castore.interfaces.IStorage;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.sos.CommonTest;
import uk.ac.standrews.cs.sos.constants.Hashes;
import uk.ac.standrews.cs.sos.constants.JSONConstants;
import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.exceptions.crypto.SignatureException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.URILocation;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.bundles.CacheLocationBundle;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.bundles.ExternalLocationBundle;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.impl.manifest.ManifestFactory;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.model.Atom;
import uk.ac.standrews.cs.sos.model.Location;
import uk.ac.standrews.cs.sos.utils.HelperTest;
import uk.ac.standrews.cs.sos.utils.JSONHelper;

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
            String root = System.getProperty("user.home") + "/sos/";

            CastoreBuilder castoreBuilder = new CastoreBuilder()
                    .setType(CastoreType.LOCAL)
                    .setRoot(root);
            IStorage stor = CastoreFactory.createStorage(castoreBuilder);
            localStorage = new LocalStorage(stor);

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
        Atom atomManifest = ManifestFactory.createAtomManifest(GUIDFactory.recreateGUID(Hashes.TEST_STRING_HASHED), bundles);

        Set<LocationBundle> others = atomManifest.getLocations();
        assertEquals(others, bundles);
    }

    @Test
    public void testNullGUID() throws Exception {
        Set<LocationBundle> bundles = new LinkedHashSet<>();
        Location location = HelperTest.createDummyDataFile(localStorage);
        bundles.add(new CacheLocationBundle(location));
        Atom atomManifest = ManifestFactory.createAtomManifest(null, bundles);

        Set<LocationBundle> others = atomManifest.getLocations();
        assertEquals(others, bundles);
        assertFalse(atomManifest.isValid());
    }

    @Test
    public void testGetLocations() throws Exception {
        Set<LocationBundle> bundles = new LinkedHashSet<>();
        Location location = HelperTest.createDummyDataFile(localStorage);
        bundles.add(new CacheLocationBundle(location));
        Atom atomManifest = ManifestFactory.createAtomManifest(GUIDFactory.recreateGUID(Hashes.TEST_STRING_HASHED), bundles);

        Set<LocationBundle> others = atomManifest.getLocations();
        assertEquals(others, bundles);
    }

    @Test (timeOut = 10000)
    public void testToJSON() throws Exception {
        Location location = new URILocation(Hashes.TEST_HTTP_BIN_URL);
        LocationBundle bundle = new ExternalLocationBundle(location);
        Set<LocationBundle> bundles = new LinkedHashSet<>();
        bundles.add(bundle);
        Atom atomManifest = ManifestFactory.createAtomManifest(GUIDFactory.recreateGUID(Hashes.TEST_HTTP_BIN_HASH), bundles);

        Set<LocationBundle> newBundles = atomManifest.getLocations();
        assertEquals(newBundles.size(), 1);

        JsonNode node = JSONHelper.JsonObjMapper().readTree(atomManifest.toString());
        JsonNode locationsNode = node.get(JSONConstants.KEY_LOCATIONS);
        assertTrue(locationsNode.isArray());
        assertEquals(locationsNode.size(), 1);
    }

    @Test (timeOut = 10000)
    public void testIsValid() throws Exception {
        Location location = new URILocation(Hashes.TEST_HTTP_BIN_URL);
        LocationBundle bundle = new ExternalLocationBundle(location);
        Set<LocationBundle> bundles = new LinkedHashSet<>();
        bundles.add(bundle);
        Atom atomManifest = ManifestFactory.createAtomManifest(GUIDFactory.recreateGUID(Hashes.TEST_HTTP_BIN_HASH), bundles);

        assertEquals(atomManifest.isValid(), true);
    }

    @Test (timeOut = 10000)
    public void testIsVerified() throws Exception {
        Location location = new URILocation(Hashes.TEST_HTTP_BIN_URL);
        LocationBundle bundle = new ExternalLocationBundle(location);
        Set<LocationBundle> bundles = new LinkedHashSet<>();
        bundles.add(bundle);
        Atom atomManifest = ManifestFactory.createAtomManifest(GUIDFactory.recreateGUID(Hashes.TEST_HTTP_BIN_HASH), bundles);

        assertFalse(atomManifest.verifySignature(null));
        assertTrue(atomManifest.verifyIntegrity());
    }

    @Test (timeOut = 10000)
    public void testIsVerifiedForLocation() throws Exception {
        Location location = new URILocation(Hashes.TEST_HTTP_BIN_URL);
        LocationBundle bundle = new ExternalLocationBundle(location);
        Set<LocationBundle> bundles = new LinkedHashSet<>();
        bundles.add(bundle);
        Atom atomManifest = ManifestFactory.createAtomManifest(GUIDFactory.recreateGUID(Hashes.TEST_HTTP_BIN_HASH), bundles);

        assertTrue(atomManifest.verifyIntegrity(bundle));

        Location wrongLocation = new URILocation(Hashes.TEST_HTTP_BIN_URL_OTHER);
        LocationBundle wrongBundle = new ExternalLocationBundle(wrongLocation);

        assertFalse(atomManifest.verifyIntegrity(wrongBundle));
    }

    @Test
    public void verifyAtomWithNullGUIDTest() throws SignatureException {
        Set<LocationBundle> bundles = new LinkedHashSet<>();
        Atom atomManifest = ManifestFactory.createAtomManifest(null, bundles);

        assertFalse(atomManifest.verifySignature(null));
        assertTrue(atomManifest.verifyIntegrity());
    }
}