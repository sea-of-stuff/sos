package uk.ac.standrews.cs.sos.impl.datamodel.directory;

import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.SetUpTest;
import uk.ac.standrews.cs.sos.constants.Hashes;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.impl.datamodel.AtomManifest;
import uk.ac.standrews.cs.sos.impl.datamodel.ContentImpl;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.URILocation;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.bundles.CacheLocationBundle;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.bundles.ExternalLocationBundle;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.impl.manifest.AbstractSignedManifest;
import uk.ac.standrews.cs.sos.impl.manifest.BasicManifest;
import uk.ac.standrews.cs.sos.impl.manifest.ManifestFactory;
import uk.ac.standrews.cs.sos.model.*;
import uk.ac.standrews.cs.sos.utils.HelperTest;
import uk.ac.standrews.cs.sos.utils.ManifestUtils;
import uk.ac.standrews.cs.sos.utils.UserRoleUtils;

import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static uk.ac.standrews.cs.sos.constants.Internals.GUID_ALGORITHM;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class LocalManifestsDirectoryTest extends SetUpTest {

    @Test
    public void addAtomManifestTest() throws Exception {
        LocalManifestsDirectory manifestsDirectory = new LocalManifestsDirectory(localStorage);

        Location location = new URILocation(Hashes.TEST_HTTP_BIN_URL);
        LocationBundle bundle = new ExternalLocationBundle(location);
        Set<LocationBundle> bundles = new LinkedHashSet<>();
        bundles.add(bundle);
        Atom atomManifest = ManifestFactory.createAtomManifest(GUIDFactory.recreateGUID(Hashes.TEST_HTTP_BIN_HASH), bundles);

        IGUID guid = atomManifest.guid();
        try {
            manifestsDirectory.addManifest(atomManifest);
            Manifest manifest = manifestsDirectory.findManifest(guid);

            Assert.assertEquals(manifest.getType(), ManifestType.ATOM);
            assertEquals(manifest.guid(), guid);
            assertEquals(manifest.isValid(), true);
        } catch (ManifestPersistException |ManifestNotFoundException e) {
            throw new Exception(e);
        }
    }

    @Test
    public void addCompoundManifestTest() throws Exception {
        LocalManifestsDirectory manifestsDirectory = new LocalManifestsDirectory(localStorage);

        Role roleMocked = UserRoleUtils.BareRoleMock();
        Content content = new ContentImpl("Cat", GUIDFactory.generateRandomGUID(GUID_ALGORITHM));
        Set<Content> contents = new LinkedHashSet<>();
        contents.add(content);

        Compound compoundManifest = ManifestFactory.createCompoundManifest(CompoundType.DATA, contents, roleMocked);
        IGUID guid = compoundManifest.guid();
        try {
            manifestsDirectory.addManifest(compoundManifest);
            Manifest manifest = manifestsDirectory.findManifest(guid);

            assertEquals(manifest.getType(), ManifestType.COMPOUND);
            assertFalse(((AbstractSignedManifest) manifest).getSignature().isEmpty());
            assertEquals(manifest.guid(), guid);
            assertEquals(manifest.isValid(), true);
        } catch (ManifestPersistException | ManifestNotFoundException e) {
            throw new Exception(e);
        }
    }

    @Test (expectedExceptions = ManifestNotMadeException.class)
    public void noCompoundTypeYieldsNotValidManifestTest() throws Exception {
        InputStream inputStreamFake = HelperTest.StringToInputStream(Hashes.TEST_STRING);
        IGUID guid = GUIDFactory.generateGUID(GUID_ALGORITHM, inputStreamFake);

        Content cat = new ContentImpl("cat", guid);
        Set<Content> contents = new LinkedHashSet<>();
        contents.add(cat);

        Role roleMocked = UserRoleUtils.BareRoleMock();
        ManifestFactory.createCompoundManifest(null, contents, roleMocked);
    }

    @Test
    public void addVersionManifestTest() throws Exception {
        LocalManifestsDirectory manifestsDirectory = new LocalManifestsDirectory(localStorage);

        IGUID contentGUID = GUIDFactory.generateRandomGUID(GUID_ALGORITHM);
        Version versionManifest = ManifestUtils.createDummyVersion(contentGUID);

        IGUID guid = versionManifest.version();
        try {
            manifestsDirectory.addManifest(versionManifest);
            Version manifest = (Version) manifestsDirectory.findManifest(guid);

            assertEquals(manifest.getType(), ManifestType.VERSION);
            assertEquals(manifest.content(), contentGUID);
            assertEquals(manifest.isValid(), true);
        } catch (ManifestPersistException | ManifestNotFoundException e) {
            throw new Exception(e);
        }
    }

    @Test (expectedExceptions = ManifestNotMadeException.class)
    public void testAddVersionManifestNullContent() throws Exception {
        ManifestUtils.createDummyVersion(null);
    }


    @Test
    public void updateAtomManifestTest() throws Exception {
        LocalManifestsDirectory manifestsDirectory = new LocalManifestsDirectory(localStorage);

        Location firstLocation = HelperTest.createDummyDataFile(localStorage, "first.txt");
        Location secondLocation = HelperTest.createDummyDataFile(localStorage, "second.txt");

        Atom atomManifest = ManifestFactory.createAtomManifest(GUIDFactory.recreateGUID(Hashes.TEST_STRING_HASHED),
                new LinkedHashSet<>(Collections.singletonList(new ExternalLocationBundle(firstLocation))));
        IGUID guid = atomManifest.guid();

        Atom anotherManifest = ManifestFactory.createAtomManifest(GUIDFactory.recreateGUID(Hashes.TEST_STRING_HASHED),
                new LinkedHashSet<>(Collections.singletonList(new ExternalLocationBundle(secondLocation))));
        IGUID anotherGUID = anotherManifest.guid();

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
        LocalManifestsDirectory manifestsDirectory = new LocalManifestsDirectory(localStorage);

        Location firstLocation = HelperTest.createDummyDataFile(localStorage, "first.txt");
        Location secondLocation = HelperTest.createDummyDataFile(localStorage, "second.txt");

        Atom atomManifest = ManifestFactory.createAtomManifest(GUIDFactory.recreateGUID(Hashes.TEST_STRING_HASHED),
                new LinkedHashSet<>(Collections.singletonList(new CacheLocationBundle(firstLocation))));
        IGUID guid = atomManifest.guid();

        Atom anotherManifest = ManifestFactory.createAtomManifest(GUIDFactory.recreateGUID(Hashes.TEST_STRING_HASHED),
                new LinkedHashSet<>(Collections.singletonList(new CacheLocationBundle(secondLocation))));
        IGUID anotherGUID = anotherManifest.guid();

        assertEquals(guid, anotherGUID);

        try {
            manifestsDirectory.addManifest(atomManifest);
            localStorage.getManifestsDirectory().remove(guid.toMultiHash() + ".json");

            manifestsDirectory.addManifest(anotherManifest);
            AtomManifest manifest = (AtomManifest) manifestsDirectory.findManifest(guid);

            assertEquals(manifest.getLocations().size(), 1);
        } catch (ManifestPersistException | ManifestNotFoundException e) {
            throw new Exception(e);
        }
    }

    @Test (expectedExceptions = ManifestPersistException.class)
    public void addNullManifestTest() throws Exception {
        LocalManifestsDirectory manifestsDirectory = new LocalManifestsDirectory(localStorage);

        BasicManifest manifest = mock(BasicManifest.class, Mockito.CALLS_REAL_METHODS);
        when(manifest.isValid()).thenReturn(false);
        manifestsDirectory.addManifest(manifest);
    }

}
