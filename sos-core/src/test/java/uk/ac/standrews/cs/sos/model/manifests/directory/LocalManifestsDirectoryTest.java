package uk.ac.standrews.cs.sos.model.manifests.directory;

import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.CommonTest;
import uk.ac.standrews.cs.sos.configuration.SOSConfiguration;
import uk.ac.standrews.cs.sos.constants.Hashes;
import uk.ac.standrews.cs.sos.exceptions.manifest.*;
import uk.ac.standrews.cs.sos.interfaces.identity.Identity;
import uk.ac.standrews.cs.sos.interfaces.locations.Location;
import uk.ac.standrews.cs.sos.interfaces.manifests.Asset;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;
import uk.ac.standrews.cs.sos.interfaces.policy.ManifestPolicy;
import uk.ac.standrews.cs.sos.model.identity.IdentityImpl;
import uk.ac.standrews.cs.sos.model.locations.URILocation;
import uk.ac.standrews.cs.sos.model.locations.bundles.CacheLocationBundle;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.model.locations.bundles.ProvenanceLocationBundle;
import uk.ac.standrews.cs.sos.model.manifests.*;
import uk.ac.standrews.cs.sos.policies.BasicManifestPolicy;
import uk.ac.standrews.cs.sos.storage.LocalStorage;
import uk.ac.standrews.cs.sos.utils.HelperTest;
import uk.ac.standrews.cs.storage.StorageFactory;
import uk.ac.standrews.cs.storage.StorageType;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.*;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class LocalManifestsDirectoryTest extends CommonTest {

    private LocalStorage storage;
    private ManifestPolicy policy;

    @BeforeMethod
    public void setUp(Method testMethod) throws Exception {
        super.setUp(testMethod);

        SOSConfiguration configurationMock = mock(SOSConfiguration.class);
        when(configurationMock.getStorageType()).thenReturn(StorageType.LOCAL);
        when(configurationMock.getStorageLocation()).thenReturn(System.getProperty("user.home") + "/sos/");

        storage = new LocalStorage(StorageFactory
                .createStorage(configurationMock.getStorageType(),
                        configurationMock.getStorageLocation()));

        policy = new BasicManifestPolicy(true, false, 0);
    }

    @AfterMethod
    public void tearDown() throws Exception {
        storage.destroy();
    }

    @Test
    public void addAtomManifestTest() throws Exception {
        LocalManifestsDirectory manifestsDirectory = new LocalManifestsDirectory(policy, storage);

        Location location = new URILocation(Hashes.TEST_HTTP_BIN_URL);
        LocationBundle bundle = new ProvenanceLocationBundle(location);
        Set<LocationBundle> bundles = new LinkedHashSet<>();
        bundles.add(bundle);
        AtomManifest atomManifest = ManifestFactory.createAtomManifest(GUIDFactory.recreateGUID(Hashes.TEST_HTTP_BIN_HASH), bundles);

        IGUID guid = atomManifest.getContentGUID();
        try {
            manifestsDirectory.addManifest(atomManifest);
            Manifest manifest = manifestsDirectory.findManifest(guid);

            Assert.assertEquals(manifest.getManifestType(), ManifestType.ATOM);
            assertEquals(manifest.getContentGUID(), guid);
            assertEquals(manifest.isValid(), true);
        } catch (ManifestPersistException |ManifestNotFoundException e) {
            throw new Exception(e);
        }
    }

    @Test
    public void addCompoundManifestTest() throws Exception {
        LocalManifestsDirectory manifestsDirectory = new LocalManifestsDirectory(policy, storage);

        Identity identity = new IdentityImpl();
        Content content = new Content("Cat", GUIDFactory.recreateGUID("123"));
        Set<Content> contents = new LinkedHashSet<>();
        contents.add(content);

        CompoundManifest compoundManifest = ManifestFactory.createCompoundManifest(CompoundType.DATA, contents, identity);
        IGUID guid = compoundManifest.getContentGUID();
        try {
            manifestsDirectory.addManifest(compoundManifest);
            Manifest manifest = manifestsDirectory.findManifest(guid);

            assertEquals(manifest.getManifestType(), ManifestType.COMPOUND);
            assertFalse(((SignedManifest) manifest).getSignature().isEmpty());
            assertEquals(manifest.getContentGUID(), guid);
            assertEquals(manifest.isValid(), true);
        } catch (ManifestPersistException | ManifestNotFoundException e) {
            throw new Exception(e);
        }
    }

    @Test (expectedExceptions = ManifestNotMadeException.class)
    public void noCompoundTypeYieldsNotValidManifestTest() throws Exception {
        InputStream inputStreamFake = HelperTest.StringToInputStream(Hashes.TEST_STRING);
        IGUID guid = GUIDFactory.generateGUID(inputStreamFake);

        Content cat = new Content("cat", guid);
        Set<Content> contents = new LinkedHashSet<>();
        contents.add(cat);

        Identity identityMocked = mock(Identity.class);
        byte[] fakedSignature = new byte[]{0, 0, 1};
        when(identityMocked.sign(any(String.class))).thenReturn(fakedSignature);

        CompoundManifest compoundManifest = ManifestFactory.createCompoundManifest(null, contents, identityMocked);
    }

    @Test
    public void addAssetManifestTest() throws Exception {
        LocalManifestsDirectory manifestsDirectory = new LocalManifestsDirectory(policy, storage);

        IGUID contentGUID = GUIDFactory.recreateGUID("123");
        Asset assetManifest = createDummyVersion(contentGUID);

        IGUID guid = assetManifest.getVersionGUID();
        try {
            manifestsDirectory.addManifest(assetManifest);
            Manifest manifest = manifestsDirectory.findManifest(guid);

            assertEquals(manifest.getManifestType(), ManifestType.ASSET);
            assertFalse(((SignedManifest) manifest).getSignature().isEmpty());
            assertEquals(manifest.getContentGUID(), contentGUID);
            assertEquals(manifest.isValid(), true);
        } catch (ManifestPersistException | ManifestNotFoundException e) {
            throw new Exception(e);
        }
    }

    @Test (expectedExceptions = ManifestNotMadeException.class)
    public void testAddAssetManifestNullContent() throws Exception {
        Asset assetManifest = createDummyVersion(null);
    }

    @Test
    public void updateAtomManifestTest() throws Exception {
        LocalManifestsDirectory manifestsDirectory = new LocalManifestsDirectory(policy, storage);

        Location firstLocation = HelperTest.createDummyDataFile(storage, "first.txt");
        Location secondLocation = HelperTest.createDummyDataFile(storage, "second.txt");

        AtomManifest atomManifest = ManifestFactory.createAtomManifest(
                GUIDFactory.recreateGUID(Hashes.TEST_STRING_HASHED),
                new LinkedHashSet<>(Collections.singletonList(new CacheLocationBundle(firstLocation))));
        IGUID guid = atomManifest.getContentGUID();

        AtomManifest anotherManifest = ManifestFactory.createAtomManifest(
                GUIDFactory.recreateGUID(Hashes.TEST_STRING_HASHED),
                new LinkedHashSet<>(Collections.singletonList(new CacheLocationBundle(secondLocation))));
        IGUID anotherGUID = anotherManifest.getContentGUID();

        assertEquals(guid, anotherGUID);

        try {
            manifestsDirectory.addManifest(atomManifest);
            manifestsDirectory.addManifest(anotherManifest);
            AtomManifest manifest = (AtomManifest) manifestsDirectory.findManifest(guid);

            assertEquals(manifest.getLocations().size(), 2);
        } catch (ManifestPersistException | ManifestNotFoundException e) {
            throw new Exception(e);
        }
    }

    @Test
    public void deletePrevAtomWhileNewIsAddedTest() throws Exception {
        LocalManifestsDirectory manifestsDirectory = new LocalManifestsDirectory(policy, storage);

        Location firstLocation = HelperTest.createDummyDataFile(storage, "first.txt");
        Location secondLocation = HelperTest.createDummyDataFile(storage, "second.txt");

        AtomManifest atomManifest = ManifestFactory.createAtomManifest(
                GUIDFactory.recreateGUID(Hashes.TEST_STRING_HASHED),
                new LinkedHashSet<>(Collections.singletonList(new CacheLocationBundle(firstLocation))));
        IGUID guid = atomManifest.getContentGUID();

        AtomManifest anotherManifest = ManifestFactory.createAtomManifest(
                GUIDFactory.recreateGUID(Hashes.TEST_STRING_HASHED),
                new LinkedHashSet<>(Collections.singletonList(new CacheLocationBundle(secondLocation))));
        IGUID anotherGUID = anotherManifest.getContentGUID();

        assertEquals(guid, anotherGUID);

        try {
            manifestsDirectory.addManifest(atomManifest);
            storage.getManifestDirectory().remove(guid.toString() + ".json");

            manifestsDirectory.addManifest(anotherManifest);
            AtomManifest manifest = (AtomManifest) manifestsDirectory.findManifest(guid);

            assertEquals(manifest.getLocations().size(), 1);
        } catch (ManifestPersistException | ManifestNotFoundException e) {
            throw new Exception(e);
        }
    }

    @Test (expectedExceptions = ManifestPersistException.class)
    public void addNullManifestTest() throws Exception {
        LocalManifestsDirectory manifestsDirectory = new LocalManifestsDirectory(policy, storage);

        BasicManifest manifest = mock(BasicManifest.class, Mockito.CALLS_REAL_METHODS);
        when(manifest.isValid()).thenReturn(false);
        manifestsDirectory.addManifest(manifest);
    }


    @Test (expectedExceptions = HEADNotFoundException.class)
    public void getUnsetHEAD() throws Exception {
        LocalManifestsDirectory manifestsDirectory = new LocalManifestsDirectory(policy, storage);

        IGUID contentGUID = GUIDFactory.recreateGUID("123");
        Asset asset = createDummyVersion(contentGUID);
        IGUID invariant = asset.getInvariantGUID();

        manifestsDirectory.getHEAD(invariant);
    }

    @Test (expectedExceptions = HEADNotFoundException.class)
    public void getRandomVersionUnsetHEAD() throws Exception {
        LocalManifestsDirectory manifestsDirectory = new LocalManifestsDirectory(policy, storage);

        IGUID invariant = GUIDFactory.generateRandomGUID();

        manifestsDirectory.getHEAD(invariant);
    }

    @Test
    public void getSetAndGetHEAD() throws Exception {
        LocalManifestsDirectory manifestsDirectory = new LocalManifestsDirectory(policy, storage);

        IGUID contentGUID = GUIDFactory.recreateGUID("123");
        Asset asset = createDummyVersion(contentGUID);
        IGUID invariant = asset.getInvariantGUID();

        manifestsDirectory.addManifest(asset);
        manifestsDirectory.setHEAD(asset.getVersionGUID());
        Asset retrievedAsset = manifestsDirectory.getHEAD(invariant);

        assertNotNull(retrievedAsset);
        assertEquals(retrievedAsset.toString(), asset.toString());
    }

    @Test (expectedExceptions = HEADNotSetException.class)
    public void getReSetHEADFail() throws Exception {
        LocalManifestsDirectory manifestsDirectory = new LocalManifestsDirectory(policy, storage);

        IGUID contentGUID = GUIDFactory.recreateGUID("123");
        Asset asset = createDummyVersion(contentGUID);
        IGUID invariant = asset.getInvariantGUID();

        manifestsDirectory.addManifest(asset);
        manifestsDirectory.setHEAD(asset.getVersionGUID());
        manifestsDirectory.setHEAD(GUIDFactory.generateRandomGUID());

        manifestsDirectory.getHEAD(invariant);
    }

    private Asset createDummyVersion(IGUID contentGUID) throws Exception {
        Identity identity = new IdentityImpl();
        Asset asset = ManifestFactory.createVersionManifest(contentGUID, null, null, null, identity);

        return asset;
    }
}
