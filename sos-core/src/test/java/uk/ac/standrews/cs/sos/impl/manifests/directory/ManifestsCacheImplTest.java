package uk.ac.standrews.cs.sos.impl.manifests.directory;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.castore.CastoreBuilder;
import uk.ac.standrews.cs.castore.CastoreFactory;
import uk.ac.standrews.cs.castore.CastoreType;
import uk.ac.standrews.cs.castore.exceptions.StorageException;
import uk.ac.standrews.cs.castore.interfaces.IDirectory;
import uk.ac.standrews.cs.castore.interfaces.IFile;
import uk.ac.standrews.cs.castore.interfaces.IStorage;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.CommonTest;
import uk.ac.standrews.cs.sos.constants.Hashes;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.locations.URILocation;
import uk.ac.standrews.cs.sos.impl.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.impl.locations.bundles.ProvenanceLocationBundle;
import uk.ac.standrews.cs.sos.impl.manifests.AtomManifest;
import uk.ac.standrews.cs.sos.impl.manifests.ManifestFactory;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.interfaces.manifests.ManifestsCache;
import uk.ac.standrews.cs.sos.model.Location;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.ManifestType;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ManifestsCacheImplTest extends CommonTest {

    @Test
    public void basicTest() throws ManifestPersistException, ManifestNotFoundException {
        ManifestsCache cache = new ManifestsCacheImpl();

        Manifest manifest = getMockManifest();
        IGUID guid = manifest.guid();
        cache.addManifest(manifest);

        Manifest manifest1 = cache.findManifest(guid);
        assertEquals(manifest, manifest1);
    }

    @Test (expectedExceptions = ManifestPersistException.class)
    public void cacheMissTest() throws ManifestPersistException, ManifestNotFoundException {
        ManifestsCache cache = new ManifestsCacheImpl();

        IGUID guid = GUIDFactory.generateRandomGUID();
        cache.findManifest(guid);
    }

    @Test (expectedExceptions = ManifestPersistException.class)
    public void cacheAddAndMissTest() throws ManifestPersistException, ManifestNotFoundException {
        ManifestsCache cache = new ManifestsCacheImpl(2);

        Manifest manifest = getMockManifest();
        Manifest manifest1 = getMockManifest();
        Manifest manifest2 = getMockManifest();

        cache.addManifest(manifest);
        cache.addManifest(manifest1);
        cache.addManifest(manifest2);

        cache.findManifest(manifest.guid());
    }

    @Test
    public void cacheAddUniqueTest() throws ManifestPersistException, ManifestNotFoundException {
        ManifestsCache cache = new ManifestsCacheImpl();

        Manifest manifest = getMockManifest();

        cache.addManifest(manifest);
        cache.addManifest(manifest);
        cache.addManifest(manifest);

        Manifest manifest1 = cache.findManifest(manifest.guid());
        assertEquals(manifest, manifest1);
    }

    @Test
    public void persistCacheTest() throws IOException, ClassNotFoundException, ManifestPersistException, StorageException,
            DataStorageException, ManifestPersistException, GUIDGenerationException, URISyntaxException, ManifestNotFoundException {

        String root = System.getProperty("user.home") + "/sos/";

        CastoreBuilder castoreBuilder = new CastoreBuilder()
                .setType(CastoreType.LOCAL)
                .setRoot(root);
        IStorage stor = CastoreFactory.createStorage(castoreBuilder);
        LocalStorage localStorage = new LocalStorage(stor);

        IDirectory manifestsDir = localStorage.getManifestsDirectory();
        IDirectory cachesDir = localStorage.getNodeDirectory();

        LocalManifestsDirectory localManifestsManager = new LocalManifestsDirectory(localStorage);
        ManifestsCache cache = new ManifestsCacheImpl();

        Manifest manifest = getValidManifest();
        IGUID guid = manifest.guid();

        // Save manifest to disk, so that the cache can then load it later
        localManifestsManager.addManifest(manifest);
        cache.addManifest(manifest);

        IFile file = localStorage.createFile(cachesDir, "manifests.cache");
        cache.persist(file);

        ManifestsCache persistedCache = ManifestsCacheImpl.load(localStorage, file, manifestsDir);

        assertNotNull(persistedCache.findManifest(guid));
    }

    @Test (expectedExceptions = ManifestPersistException.class)
    public void persistCacheFailsWhenNoManifestsNotSavedTest() throws IOException, ClassNotFoundException,
            ManifestPersistException, StorageException, DataStorageException, ManifestNotFoundException {

        String root = System.getProperty("user.home") + "/sos/";

        CastoreBuilder castoreBuilder = new CastoreBuilder()
                .setType(CastoreType.LOCAL)
                .setRoot(root);
        IStorage stor = CastoreFactory.createStorage(castoreBuilder);
        LocalStorage localStorage = new LocalStorage(stor);

        IDirectory manifestsDir = localStorage.getManifestsDirectory();
        IDirectory cachesDir = localStorage.getNodeDirectory();

        ManifestsCache cache = new ManifestsCacheImpl();

        Manifest manifest = getMockManifest();
        IGUID guid = manifest.guid();
        cache.addManifest(manifest);

        IFile file = localStorage.createFile(cachesDir, "manifests.cache");
        cache.persist(file);

        ManifestsCache persistedCache = ManifestsCacheImpl.load(localStorage, file, manifestsDir);
        persistedCache.findManifest(guid);
    }

    private Manifest getValidManifest() throws GUIDGenerationException, URISyntaxException {
        Location location = new URILocation(Hashes.TEST_HTTP_BIN_URL);
        LocationBundle bundle = new ProvenanceLocationBundle(location);
        Set<LocationBundle> bundles = new LinkedHashSet<>();
        bundles.add(bundle);
        AtomManifest atomManifest = ManifestFactory.createAtomManifest(GUIDFactory.recreateGUID(Hashes.TEST_HTTP_BIN_HASH), bundles);

        return atomManifest;
    }

    private Manifest getMockManifest() {
        Manifest manifest = mock(Manifest.class);
        IGUID guid = GUIDFactory.generateRandomGUID();
        when(manifest.guid()).thenReturn(guid);
        when(manifest.guid()).thenReturn(guid);
        when(manifest.isValid()).thenReturn(true);
        when(manifest.getType()).thenReturn(ManifestType.ATOM);

        return manifest;
    }
}