package uk.ac.standrews.cs.sos.managers;

import org.mockito.Mockito;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.constants.Hashes;
import uk.ac.standrews.cs.sos.configurations.SeaConfiguration;
import uk.ac.standrews.cs.sos.exceptions.identity.KeyGenerationException;
import uk.ac.standrews.cs.sos.exceptions.identity.KeyLoadedException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.storage.ManifestPersistException;
import uk.ac.standrews.cs.sos.model.implementations.SeaOfStuffImpl;
import uk.ac.standrews.cs.sos.model.implementations.components.manifests.*;
import uk.ac.standrews.cs.sos.model.implementations.identity.IdentityImpl;
import uk.ac.standrews.cs.sos.model.implementations.locations.Location;
import uk.ac.standrews.cs.sos.model.implementations.locations.URILocation;
import uk.ac.standrews.cs.sos.model.implementations.locations.bundles.CacheLocationBundle;
import uk.ac.standrews.cs.sos.model.implementations.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.model.implementations.locations.bundles.ProvenanceLocationBundle;
import uk.ac.standrews.cs.sos.model.implementations.utils.Content;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUID;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUIDsha1;
import uk.ac.standrews.cs.sos.model.interfaces.components.Manifest;
import uk.ac.standrews.cs.sos.model.interfaces.identity.Identity;
import uk.ac.standrews.cs.utils.Helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ManifestsManagerTest {

    private SeaConfiguration configuration;
    private Index index;

    @BeforeMethod
    public void setUp() throws IOException, KeyGenerationException, KeyLoadedException {
        SeaConfiguration.setRootName("test");
        configuration = SeaConfiguration.getInstance();
        index = LuceneIndex.getInstance(configuration);
        new SeaOfStuffImpl(configuration, index);
    }

    @AfterMethod
    public void tearDown() throws IOException {
        index.flushDB();
        index.killInstance();

        Helper.deleteDirectory(configuration.getIndexPath());
        Helper.cleanDirectory(configuration.getLocalManifestsLocation());
        Helper.cleanDirectory(configuration.getCacheDataPath());
        Helper.cleanDirectory(configuration.getDataPath());
    }

    @Test
    public void testAddAtomManifest() throws Exception {
        ManifestsManager manifestsManager = new ManifestsManager(configuration, index);

        Location location = new URILocation(Hashes.TEST_HTTP_BIN_URL);
        LocationBundle bundle = new ProvenanceLocationBundle(location);
        Collection<LocationBundle> bundles = new ArrayList<>();
        bundles.add(bundle);
        AtomManifest atomManifest = ManifestFactory.createAtomManifest(new GUIDsha1(Hashes.TEST_HTTP_BIN_HASH), bundles);

        GUID guid = atomManifest.getContentGUID();
        try {
            manifestsManager.addManifest(atomManifest);
            Manifest manifest = manifestsManager.findManifest(guid);

            assertEquals(manifest.getManifestType(), ManifestConstants.ATOM);
            assertEquals(manifest.getContentGUID(), guid);
            assertEquals(manifest.isValid(), true);
        } catch (ManifestPersistException e) {
            throw new Exception();
        }
    }

    @Test
    public void testAddCompoundManifest() throws Exception {
        ManifestsManager manifestsManager = new ManifestsManager(configuration, index);

        Identity identity = new IdentityImpl(configuration);
        Content content = new Content("Cat", new GUIDsha1("123"));
        Collection<Content> contents = new ArrayList<>();
        contents.add(content);

        CompoundManifest compoundManifest = ManifestFactory.createCompoundManifest(contents, identity);
        GUID guid = compoundManifest.getContentGUID();
        try {
            manifestsManager.addManifest(compoundManifest);
            Manifest manifest = manifestsManager.findManifest(guid);

            assertEquals(manifest.getManifestType(), ManifestConstants.COMPOUND);
            assertFalse(((SignedManifest) manifest).getSignature().isEmpty());
            assertEquals(manifest.getContentGUID(), guid);
            assertEquals(manifest.isValid(), true);
        } catch (ManifestPersistException e) {
            throw new Exception();
        }
    }

    @Test
    public void testAddAssetManifest() throws Exception {
        ManifestsManager manifestsManager = new ManifestsManager(configuration, index);
        Identity identity = new IdentityImpl(configuration);

        GUID contentGUID = new GUIDsha1("123");
        AssetManifest assetManifest = ManifestFactory.createAssetManifest(contentGUID, null, null, null, identity);
        GUID guid = assetManifest.getVersionGUID();
        try {
            manifestsManager.addManifest(assetManifest);
            Manifest manifest = manifestsManager.findManifest(guid);

            assertEquals(manifest.getManifestType(), ManifestConstants.ASSET);
            assertFalse(((SignedManifest) manifest).getSignature().isEmpty());
            assertEquals(manifest.getContentGUID(), contentGUID);
            assertEquals(manifest.isValid(), true);
        } catch (ManifestPersistException e) {
            throw new Exception();
        }
    }

    @Test (expectedExceptions = ManifestNotMadeException.class)
    public void testAddAssetManifestNullContent() throws Exception {
        Identity identity = new IdentityImpl(configuration);
        ManifestFactory.createAssetManifest(null, null, null, null, identity);
    }

    @Test
    public void testUpdateAtomManifest() throws Exception {
        ManifestsManager manifestsManager = new ManifestsManager(configuration, index);

        Location firstLocation = Helper.createDummyDataFile(configuration, "first.txt");
        Location secondLocation = Helper.createDummyDataFile(configuration, "second.txt");

        AtomManifest atomManifest = ManifestFactory.createAtomManifest(
                new GUIDsha1(Hashes.TEST_STRING_HASHED),
                new ArrayList<>(Collections.singletonList(new CacheLocationBundle(firstLocation))));
        GUID guid = atomManifest.getContentGUID();

        AtomManifest anotherManifest = ManifestFactory.createAtomManifest(
                new GUIDsha1(Hashes.TEST_STRING_HASHED),
                new ArrayList<>(Collections.singletonList(new CacheLocationBundle(secondLocation))));
        GUID anotherGUID = anotherManifest.getContentGUID();

        assertEquals(guid, anotherGUID);

        try {
            manifestsManager.addManifest(atomManifest);
            manifestsManager.addManifest(anotherManifest);
            AtomManifest manifest = (AtomManifest) manifestsManager.findManifest(guid);

            assertEquals(manifest.getLocations().size(), 2);
        } catch (ManifestPersistException e) {
            throw new Exception();
        }
    }

    @Test (expectedExceptions = ManifestPersistException.class)
    public void testAddNullManifest() throws Exception {
        ManifestsManager manifestsManager = new ManifestsManager(configuration, index);

        BasicManifest manifest = mock(BasicManifest.class, Mockito.CALLS_REAL_METHODS);
        when(manifest.isValid()).thenReturn(false);
        manifestsManager.addManifest(manifest);
    }

}
