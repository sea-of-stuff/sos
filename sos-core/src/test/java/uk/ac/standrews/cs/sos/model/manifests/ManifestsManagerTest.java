package uk.ac.standrews.cs.sos.model.manifests;

import org.mockito.Mockito;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.constants.Hashes;
import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.exceptions.IndexException;
import uk.ac.standrews.cs.sos.exceptions.NodeManagerException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.storage.ManifestPersistException;
import uk.ac.standrews.cs.sos.interfaces.identity.Identity;
import uk.ac.standrews.cs.sos.interfaces.index.Index;
import uk.ac.standrews.cs.sos.interfaces.locations.Location;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;
import uk.ac.standrews.cs.sos.model.Configuration;
import uk.ac.standrews.cs.sos.model.identity.IdentityImpl;
import uk.ac.standrews.cs.sos.model.index.LuceneIndex;
import uk.ac.standrews.cs.sos.model.locations.URILocation;
import uk.ac.standrews.cs.sos.model.locations.bundles.CacheLocationBundle;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.model.locations.bundles.ProvenanceLocationBundle;
import uk.ac.standrews.cs.sos.node.NodeManager;
import uk.ac.standrews.cs.sos.utils.Helper;

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

    private Configuration configuration;
    private Index index;

    @BeforeMethod
    public void setUp() throws IndexException, ConfigurationException, NodeManagerException {
        Configuration.setRootName("test");
        configuration = Configuration.getInstance();
        index = LuceneIndex.getInstance(configuration);

        NodeManager.setConfiguration(configuration);
        NodeManager.setIndex(index);
        NodeManager.getInstance();
    }

    @AfterMethod
    public void tearDown() throws IOException, IndexException {
        index.flushDB();
        index.killInstance();

        Helper.DeletePath(configuration.getIndexDirectory());
        Helper.DeletePath(configuration.getManifestsDirectory());
        Helper.DeletePath(configuration.getCacheDirectory());
        Helper.DeletePath(configuration.getDataDirectory());
    }

    @Test
    public void testAddAtomManifest() throws Exception {
        ManifestsManager manifestsManager = new ManifestsManager(configuration, index);

        Location location = new URILocation(Hashes.TEST_HTTP_BIN_URL);
        LocationBundle bundle = new ProvenanceLocationBundle(location);
        Collection<LocationBundle> bundles = new ArrayList<>();
        bundles.add(bundle);
        AtomManifest atomManifest = ManifestFactory.createAtomManifest(GUIDFactory.recreateGUID(Hashes.TEST_HTTP_BIN_HASH), bundles);

        IGUID guid = atomManifest.getContentGUID();
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
        Content content = new Content("Cat", GUIDFactory.recreateGUID("123"));
        Collection<Content> contents = new ArrayList<>();
        contents.add(content);

        CompoundManifest compoundManifest = ManifestFactory.createCompoundManifest(CompoundType.DATA, contents, identity);
        IGUID guid = compoundManifest.getContentGUID();
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

        IGUID contentGUID = GUIDFactory.recreateGUID("123");
        VersionManifest assetManifest = ManifestFactory.createVersionManifest(contentGUID, null, null, null, identity);
        IGUID guid = assetManifest.getVersionGUID();
        try {
            manifestsManager.addManifest(assetManifest);
            Manifest manifest = manifestsManager.findManifest(guid);

            assertEquals(manifest.getManifestType(), ManifestConstants.VERSION);
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
        ManifestFactory.createVersionManifest(null, null, null, null, identity);
    }

    @Test
    public void testUpdateAtomManifest() throws Exception {
        ManifestsManager manifestsManager = new ManifestsManager(configuration, index);

        Location firstLocation = Helper.createDummyDataFile(configuration, "first.txt");
        Location secondLocation = Helper.createDummyDataFile(configuration, "second.txt");

        AtomManifest atomManifest = ManifestFactory.createAtomManifest(
                GUIDFactory.recreateGUID(Hashes.TEST_STRING_HASHED),
                new ArrayList<>(Collections.singletonList(new CacheLocationBundle(firstLocation))));
        IGUID guid = atomManifest.getContentGUID();

        AtomManifest anotherManifest = ManifestFactory.createAtomManifest(
                GUIDFactory.recreateGUID(Hashes.TEST_STRING_HASHED),
                new ArrayList<>(Collections.singletonList(new CacheLocationBundle(secondLocation))));
        IGUID anotherGUID = anotherManifest.getContentGUID();

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
