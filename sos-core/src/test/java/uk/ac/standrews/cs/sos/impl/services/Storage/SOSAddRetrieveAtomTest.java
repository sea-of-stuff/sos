package uk.ac.standrews.cs.sos.impl.services.Storage;

import org.apache.commons.io.IOUtils;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.castore.data.InputStreamData;
import uk.ac.standrews.cs.sos.exceptions.manifest.AtomNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.impl.datamodel.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.URILocation;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.bundles.BundleTypes;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.model.Atom;
import uk.ac.standrews.cs.sos.model.Location;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.utils.HelperTest;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Set;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSAddRetrieveAtomTest extends StorageServiceTest {

    @Test
    public void testRetrieveAtomData() throws Exception {
        Location location = HelperTest.createDummyDataFile(localStorage);

        AtomBuilder builder = new AtomBuilder()
                .setLocation(location)
                .setBundleType(BundleTypes.PERSISTENT);
        Atom manifest = storageService.addAtom(builder);
        assertEquals(manifest.getType(), ManifestType.ATOM);

        try (Data data = storageService.getAtomContent(manifest)) {
            assertTrue(IOUtils.contentEquals(location.getSource(), data.getInputStream()));
        }
    }

    @Test
    public void testAddAtomPersistentLocation() throws Exception {
        Location location = HelperTest.createDummyDataFile(localStorage);

        AtomBuilder builder = new AtomBuilder()
                .setLocation(location)
                .setBundleType(BundleTypes.PERSISTENT);
        Atom manifest = storageService.addAtom(builder);
        assertEquals(manifest.getType(), ManifestType.ATOM);

        Set<LocationBundle> retrievedLocations = (manifest.getLocations());
        Iterator<LocationBundle> bundles = retrievedLocations.iterator();
        assertEquals(2, retrievedLocations.size());

        LocationBundle firstBundle = bundles.next();
        assertEquals(firstBundle.getType(), BundleTypes.PERSISTENT);

        LocationBundle secondBundle = bundles.next();
        assertEquals(secondBundle.getType(), BundleTypes.EXTERNAL);
    }

    @Test
    public void testAddAtomStreamPersistentLocation() throws Exception {
        String testString = "first line and second line";
        InputStream stream = HelperTest.StringToInputStream(testString);

        AtomBuilder builder = new AtomBuilder()
                .setData(new InputStreamData(stream))
                .setBundleType(BundleTypes.PERSISTENT);
        Atom manifest = storageService.addAtom(builder);
        assertEquals(manifest.getType(), ManifestType.ATOM);

        Set<LocationBundle> retrievedLocations = (manifest.getLocations());
        Iterator<LocationBundle> bundles = retrievedLocations.iterator();
        assertEquals(1, retrievedLocations.size());

        LocationBundle firstBundle = bundles.next();
        assertEquals(firstBundle.getType(), BundleTypes.PERSISTENT);
    }

    @Test
    public void testAddAtomFromURLPersistentLocation() throws Exception {
        Location location = new URILocation("http://www.eastcottvets.co.uk/uploads/Animals/gingerkitten.jpg");

        AtomBuilder builder = new AtomBuilder()
                .setLocation(location)
                .setBundleType(BundleTypes.PERSISTENT);
        Atom manifest = storageService.addAtom(builder);
        assertEquals(manifest.getType(), ManifestType.ATOM);

        Set<LocationBundle> retrievedLocations = (manifest.getLocations());
        Iterator<LocationBundle> bundles = retrievedLocations.iterator();
        assertEquals(2, retrievedLocations.size());

        LocationBundle firstBundle = bundles.next();
        assertEquals(firstBundle.getType(), BundleTypes.PERSISTENT);

        LocationBundle secondBundle = bundles.next();
        assertEquals(secondBundle.getType(), BundleTypes.EXTERNAL);
    }

    @Test (expectedExceptions = AtomNotFoundException.class)
    public void testAddAtomMethodWithoutStoringData() throws Exception {
        Location location = HelperTest.createDummyDataFile(localStorage);

        AtomBuilder builder = new AtomBuilder()
                .setLocation(location)
                .setDoNotStoreDataLocally(true);
        Atom manifest = storageService.addAtom(builder);
        assertEquals(manifest.getType(), ManifestType.ATOM);

        storageService.getAtomContent(manifest);
    }

    @Test (expectedExceptions = AtomNotFoundException.class)
    public void testAddAtomMethodWithoutStoringManifest_1() throws Exception {
        Location location = HelperTest.createDummyDataFile(localStorage);

        AtomBuilder builder = new AtomBuilder()
                .setLocation(location)
                .setDoNotStoreDataLocally(true)
                .setDoNotStoreManifestLocally(true);

        Atom manifest = storageService.addAtom(builder);
        assertEquals(manifest.getType(), ManifestType.ATOM);

        storageService.getAtomContent(manifest);
    }

    @Test (expectedExceptions = ManifestNotFoundException.class)
    public void testAddAtomMethodWithoutStoringManifest_2() throws Exception {
        Location location = HelperTest.createDummyDataFile(localStorage);

        AtomBuilder builder = new AtomBuilder()
                .setLocation(location)
                .setDoNotStoreDataLocally(true)
                .setDoNotStoreManifestLocally(true);

        Atom manifest = storageService.addAtom(builder);
        assertEquals(manifest.getType(), ManifestType.ATOM);

        localSOSNode.getMDS().getManifest(manifest.guid());
    }

    @Test (expectedExceptions = ManifestNotFoundException.class)
    public void testAddAtomMethodWithoutStoringDataAndManifest() throws Exception {
        Location location = HelperTest.createDummyDataFile(localStorage);

        AtomBuilder builder = new AtomBuilder()
                .setLocation(location)
                .setDoNotStoreManifestLocally(true);

        Atom manifest = storageService.addAtom(builder);
        assertEquals(manifest.getType(), ManifestType.ATOM);

        localSOSNode.getMDS().getManifest(manifest.guid());
    }

}
