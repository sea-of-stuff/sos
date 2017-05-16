package uk.ac.standrews.cs.sos.impl.actors.Storage;

import org.apache.commons.io.IOUtils;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.impl.locations.URILocation;
import uk.ac.standrews.cs.sos.impl.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.impl.locations.bundles.PersistLocationBundle;
import uk.ac.standrews.cs.sos.impl.locations.bundles.ProvenanceLocationBundle;
import uk.ac.standrews.cs.sos.impl.manifests.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.model.Atom;
import uk.ac.standrews.cs.sos.model.Location;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.protocol.DDSNotificationInfo;
import uk.ac.standrews.cs.sos.utils.HelperTest;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Set;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSAddAtomTest extends StorageTest {

    @Test
    public void testRetrieveAtomData() throws Exception {
        Location location = HelperTest.createDummyDataFile(localStorage);

        AtomBuilder builder = new AtomBuilder().setLocation(location);
        Atom manifest = storage.addAtom(builder, true, new DDSNotificationInfo().setNotifyDDSNodes(false));
        assertEquals(manifest.getType(), ManifestType.ATOM);

        try (InputStream inputStream = storage.getAtomContent(manifest)) {
            assertTrue(IOUtils.contentEquals(location.getSource(), inputStream));
        }
    }

    @Test
    public void testAddAtomPersistentLocation() throws Exception {
        Location location = HelperTest.createDummyDataFile(localStorage);

        AtomBuilder builder = new AtomBuilder().setLocation(location);
        Atom manifest = storage.addAtom(builder, true, new DDSNotificationInfo().setNotifyDDSNodes(false));
        assertEquals(manifest.getType(), ManifestType.ATOM);

        Set<LocationBundle> retrievedLocations = (manifest.getLocations());
        Iterator<LocationBundle> bundles = retrievedLocations.iterator();
        assertEquals(2, retrievedLocations.size());

        LocationBundle firstBundle = bundles.next();
        assertTrue(firstBundle instanceof ProvenanceLocationBundle);

        LocationBundle secondBundle = bundles.next();
        assertTrue(secondBundle instanceof PersistLocationBundle);
    }

    @Test
    public void testAddAtomStreamPersistentLocation() throws Exception {
        String testString = "first line and second line";
        InputStream stream = HelperTest.StringToInputStream(testString);

        AtomBuilder builder = new AtomBuilder().setInputStream(stream);
        Atom manifest = storage.addAtom(builder, true, new DDSNotificationInfo().setNotifyDDSNodes(false));
        assertEquals(manifest.getType(), ManifestType.ATOM);

        Set<LocationBundle> retrievedLocations = (manifest.getLocations());
        Iterator<LocationBundle> bundles = retrievedLocations.iterator();
        assertEquals(1, retrievedLocations.size());

        LocationBundle secondBundle = bundles.next();
        assertTrue(secondBundle instanceof PersistLocationBundle);
    }

    @Test
    public void testAddAtomFromURLPersistentLocation() throws Exception {
        Location location = new URILocation("http://www.eastcottvets.co.uk/uploads/Animals/gingerkitten.jpg");

        AtomBuilder builder = new AtomBuilder().setLocation(location);
        Atom manifest = storage.addAtom(builder, true, new DDSNotificationInfo().setNotifyDDSNodes(false));
        assertEquals(manifest.getType(), ManifestType.ATOM);

        Set<LocationBundle> retrievedLocations = (manifest.getLocations());
        Iterator<LocationBundle> bundles = retrievedLocations.iterator();
        assertEquals(2, retrievedLocations.size());

        LocationBundle firstBundle = bundles.next();
        assertTrue(firstBundle instanceof ProvenanceLocationBundle);

        LocationBundle secondBundle = bundles.next();
        assertTrue(secondBundle instanceof PersistLocationBundle);
    }

}
