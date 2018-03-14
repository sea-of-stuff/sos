/*
 * Copyright 2018 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module core.
 *
 * core is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * core is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with core. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.sos.impl.datamodel.directory;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.castore.CastoreBuilder;
import uk.ac.standrews.cs.castore.CastoreFactory;
import uk.ac.standrews.cs.castore.CastoreType;
import uk.ac.standrews.cs.castore.exceptions.StorageException;
import uk.ac.standrews.cs.castore.interfaces.IDirectory;
import uk.ac.standrews.cs.castore.interfaces.IFile;
import uk.ac.standrews.cs.castore.interfaces.IStorage;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.CommonTest;
import uk.ac.standrews.cs.sos.SettingsConfiguration;
import uk.ac.standrews.cs.sos.constants.Hashes;
import uk.ac.standrews.cs.sos.exceptions.IgnoreException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.URILocation;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.bundles.ExternalLocationBundle;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.sos.SOSURLProtocol;
import uk.ac.standrews.cs.sos.impl.manifest.ManifestFactory;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.interfaces.manifests.ManifestsCache;
import uk.ac.standrews.cs.sos.model.Atom;
import uk.ac.standrews.cs.sos.model.Location;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.utils.ManifestUtils;
import uk.ac.standrews.cs.sos.utils.Persistence;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.testng.Assert.*;
import static uk.ac.standrews.cs.sos.constants.Internals.GUID_ALGORITHM;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ManifestsCacheImplTest extends CommonTest {

    @BeforeMethod
    public void setUp(Method testMethod) throws Exception {
        super.setUp(testMethod);

        new SOS_LOG(GUIDFactory.generateRandomGUID(GUID_ALGORITHM));

        SOSLocalNode.settings = new SettingsConfiguration.Settings();
        SOSLocalNode.settings.setGuid(GUIDFactory.generateRandomGUID(GUID_ALGORITHM).toMultiHash());
        SOSURLProtocol.getInstance().register(null, null); // Local storage is not needed for this set of tests
    }

    @Test
    public void basicTest() throws ManifestPersistException, ManifestNotFoundException {
        ManifestsCache cache = new ManifestsCacheImpl();

        Manifest manifest = ManifestUtils.createMockManifestTypeAtom();
        IGUID guid = manifest.guid();
        cache.addManifest(manifest);

        Manifest manifest1 = cache.findManifest(guid);
        assertEquals(manifest, manifest1);
    }

    @Test (expectedExceptions = ManifestNotFoundException.class)
    public void cacheMissTest() throws ManifestNotFoundException {
        ManifestsCache cache = new ManifestsCacheImpl();

        IGUID guid = GUIDFactory.generateRandomGUID(GUID_ALGORITHM);
        cache.findManifest(guid);
    }

    @Test (expectedExceptions = ManifestNotFoundException.class)
    public void cacheAddAndMissTest() throws ManifestPersistException, ManifestNotFoundException {
        ManifestsCache cache = new ManifestsCacheImpl(2);

        Manifest manifest = ManifestUtils.createMockManifestTypeAtom();
        Manifest manifest1 = ManifestUtils.createMockManifestTypeAtom();
        Manifest manifest2 = ManifestUtils.createMockManifestTypeAtom();

        cache.addManifest(manifest);
        cache.addManifest(manifest1);
        cache.addManifest(manifest2);

        cache.findManifest(manifest.guid());
    }

    @Test
    public void cacheAddUniqueTest() throws ManifestPersistException, ManifestNotFoundException {
        ManifestsCache cache = new ManifestsCacheImpl();

        Manifest manifest = ManifestUtils.createMockManifestTypeAtom();

        cache.addManifest(manifest);
        cache.addManifest(manifest);
        cache.addManifest(manifest);

        Manifest manifest1 = cache.findManifest(manifest.guid());
        assertEquals(manifest, manifest1);
    }

    @Test
    public void persistCacheTest() throws IOException, ClassNotFoundException, StorageException,
            DataStorageException, ManifestPersistException, GUIDGenerationException, URISyntaxException, ManifestNotFoundException, IgnoreException {

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
        Persistence.persist(cache, file);

        ManifestsCache persistedCache = ManifestsCacheImpl.load(localStorage, file, manifestsDir);

        assertNotNull(persistedCache.findManifest(guid));
    }

    // A ref to the manifest is added to cache, but the manifest is not stored anywhere
    @Test (expectedExceptions = ManifestNotFoundException.class)
    public void persistCacheFailsWhenNoManifestsNotSavedTest() throws IOException, ClassNotFoundException,
            ManifestPersistException, StorageException, DataStorageException, ManifestNotFoundException, IgnoreException {

        String root = System.getProperty("user.home") + "/sos/";

        CastoreBuilder castoreBuilder = new CastoreBuilder()
                .setType(CastoreType.LOCAL)
                .setRoot(root);
        IStorage stor = CastoreFactory.createStorage(castoreBuilder);
        LocalStorage localStorage = new LocalStorage(stor);

        IDirectory manifestsDir = localStorage.getManifestsDirectory();
        IDirectory cachesDir = localStorage.getNodeDirectory();

        ManifestsCache cache = new ManifestsCacheImpl();

        Manifest manifest = ManifestUtils.createMockManifestTypeAtom();
        IGUID guid = manifest.guid();
        cache.addManifest(manifest);

        IFile file = localStorage.createFile(cachesDir, "manifests.cache");
        Persistence.persist(cache, file);

        ManifestsCache persistedCache = ManifestsCacheImpl.load(localStorage, file, manifestsDir);
        persistedCache.findManifest(guid);
    }

    @Test
    public void mergeAtomsTest() throws ManifestPersistException, ManifestNotFoundException {
        ManifestsCache cache = new ManifestsCacheImpl();

        Manifest manifest = ManifestUtils.createMockAtom();
        IGUID guid = manifest.guid();
        cache.addManifest(manifest);


        Manifest atomWithOtherLocation = ManifestUtils.createMockAtom(guid);
        cache.addManifest(atomWithOtherLocation);

        Manifest manifest1 = cache.findManifest(guid);
        assertNotEquals(manifest, manifest1);
        assertNotEquals(atomWithOtherLocation, manifest1);

        Set<LocationBundle> bundles = ((Atom) manifest1).getLocations();
        assertEquals(bundles.size(), 2);
    }

    private Manifest getValidManifest() throws GUIDGenerationException, URISyntaxException {
        Location location = new URILocation(Hashes.TEST_HTTP_BIN_URL);
        LocationBundle bundle = new ExternalLocationBundle(location);
        Set<LocationBundle> bundles = new LinkedHashSet<>();
        bundles.add(bundle);
        Atom atomManifest = ManifestFactory.createAtomManifest(GUIDFactory.recreateGUID(Hashes.TEST_HTTP_BIN_HASH), bundles);

        return atomManifest;
    }

}