package uk.ac.standrews.cs.sos.model.implementations;

import org.apache.commons.io.IOUtils;
import org.skyscreamer.jsonassert.JSONAssert;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.constants.Hashes;
import uk.ac.standrews.cs.sos.model.implementations.components.manifests.AtomManifest;
import uk.ac.standrews.cs.sos.model.implementations.components.manifests.ManifestConstants;
import uk.ac.standrews.cs.sos.model.implementations.locations.Location;
import uk.ac.standrews.cs.sos.model.implementations.locations.URILocation;
import uk.ac.standrews.cs.sos.model.implementations.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.model.implementations.locations.bundles.ProvenanceLocationBundle;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUID;
import uk.ac.standrews.cs.sos.model.interfaces.components.Manifest;
import uk.ac.standrews.cs.utils.Helper;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import static org.testng.AssertJUnit.*;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SeaOfStuffAddAtomTest extends SeaOfStuffGeneralTest {

    @Test
    public void testAddAtom() throws Exception {
        Collection<LocationBundle> locations = new ArrayList<>();
        LocationBundle location = Helper.createDummyDataFile(configuration);
        locations.add(location);
        AtomManifest manifest = model.addAtom(locations);
        assertEquals(manifest.getManifestType(), ManifestConstants.ATOM);

        Manifest retrievedManifest = model.getManifest(manifest.getContentGUID());
        assertEquals(ManifestConstants.ATOM, retrievedManifest.getManifestType());
        Collection<LocationBundle> retrievedLocations = ((AtomManifest) retrievedManifest).getLocations();
        assertEquals(retrievedLocations.size(), 2);

        JSONAssert.assertEquals(manifest.toJSON().toString(), retrievedManifest.toJSON().toString(), true);
    }

    @Test
    public void testRetrieveAtomFromFile() throws Exception {
        Collection<LocationBundle> locations = new ArrayList<>();
        LocationBundle location = Helper.createDummyDataFile(configuration);
        locations.add(location);
        AtomManifest manifest = model.addAtom(locations);
        assertEquals(manifest.getManifestType(), ManifestConstants.ATOM);

        // Flush the storage, so to force the manifest to be retrieved from file.
        index.flushDB();

        Manifest retrievedManifest = model.getManifest(manifest.getContentGUID());
        assertEquals(ManifestConstants.ATOM, retrievedManifest.getManifestType());
        Collection<LocationBundle> retrievedLocations = ((AtomManifest) retrievedManifest).getLocations();
        assertEquals(retrievedLocations.size(), 2);

        JSONAssert.assertEquals(manifest.toJSON().toString(), retrievedManifest.toJSON().toString(), true);
    }

    @Test
    public void testRetrieveAtomData() throws Exception {
        Collection<LocationBundle> locations = new ArrayList<>();
        LocationBundle location = Helper.createDummyDataFile(configuration);
        locations.add(location);
        AtomManifest manifest = model.addAtom(locations);
        assertEquals(manifest.getManifestType(), ManifestConstants.ATOM);

        Manifest retrievedManifest = model.getManifest(manifest.getContentGUID());
        InputStream inputStream = model.getAtomContent((AtomManifest) retrievedManifest);

        assertTrue(IOUtils.contentEquals(location.getLocation().getSource(), inputStream));
    }

    @Test
    public void testAtomDataVerify() throws Exception {
        Collection<LocationBundle> locations = new ArrayList<>();
        LocationBundle location = Helper.createDummyDataFile(configuration);
        locations.add(location);
        AtomManifest manifest = model.addAtom(locations);
        assertEquals(manifest.getManifestType(), ManifestConstants.ATOM);

        Manifest retrievedManifest = model.getManifest(manifest.getContentGUID());
        assertTrue(retrievedManifest.verify(null));
    }

    @Test
    public void testAtomDataVerifyFails() throws Exception {
        Collection<LocationBundle> locations = new ArrayList<>();
        LocationBundle location = Helper.createDummyDataFile(configuration);
        locations.add(location);
        AtomManifest manifest = model.addAtom(locations);
        assertEquals(manifest.getManifestType(), ManifestConstants.ATOM);

        Manifest retrievedManifest = model.getManifest(manifest.getContentGUID());
        Collection<LocationBundle> retrievedLocations = ((AtomManifest) retrievedManifest).getLocations();
        LocationBundle cachedLocation = retrievedLocations.iterator().next();

        Helper.appendToFile(cachedLocation.getLocation(), "Data has changed");
        assertFalse(retrievedManifest.verify(null));
    }

    @Test
    public void testAddAtomFromURL() throws Exception {
        Collection<LocationBundle> locations = new ArrayList<>();
        Location location = new URILocation("http://www.eastcottvets.co.uk/uploads/Animals/gingerkitten.jpg");
        locations.add(new ProvenanceLocationBundle(location));
        AtomManifest manifest = model.addAtom(locations);
        assertEquals(manifest.getManifestType(), ManifestConstants.ATOM);

        Manifest retrievedManifest = model.getManifest(manifest.getContentGUID());
        assertEquals(ManifestConstants.ATOM, retrievedManifest.getManifestType());

        System.out.println("SeaOfStuffAddAtomTest: " + manifest.getContentGUID());
    }

    @Test
    public void testAddAtomFromURLHttps() throws Exception {
        Collection<LocationBundle> locations = new ArrayList<>();
        Location location = new URILocation("https://i.ytimg.com/vi/NtgtMQwr3Ko/maxresdefault.jpg");
        locations.add(new ProvenanceLocationBundle(location));
        AtomManifest manifest = model.addAtom(locations);
        assertEquals(manifest.getManifestType(), ManifestConstants.ATOM);

        Manifest retrievedManifest = model.getManifest(manifest.getContentGUID());
        assertEquals(ManifestConstants.ATOM, retrievedManifest.getManifestType());

        System.out.println("SeaOfStuffAddAtomTest: " + manifest.getContentGUID());
    }

    @Test
    public void testAddAtomFromURLHttpsPdf() throws Exception {
        Collection<LocationBundle> locations = new ArrayList<>();
        Location location = new URILocation("https://studres.cs.st-andrews.ac.uk/CS1002/Lectures/W01/W01-Lecture.pdf");
        locations.add(new ProvenanceLocationBundle(location));
        AtomManifest manifest = model.addAtom(locations);
        assertEquals(manifest.getManifestType(), ManifestConstants.ATOM);

        Manifest retrievedManifest = model.getManifest(manifest.getContentGUID());
        assertEquals(ManifestConstants.ATOM, retrievedManifest.getManifestType());

        System.out.println("SeaOfStuffAddAtomTest: " + manifest.getContentGUID());
    }

    @Test
    public void testAddAtomFromURLHttpsTextFile() throws Exception {
        Collection<LocationBundle> locations = new ArrayList<>();
        Location location = new URILocation("https://studres.cs.st-andrews.ac.uk/CS1002/Examples/W01/Example1/W01Example1.java");
        locations.add(new ProvenanceLocationBundle(location));
        AtomManifest manifest = model.addAtom(locations);
        assertEquals(manifest.getManifestType(), ManifestConstants.ATOM);

        Manifest retrievedManifest = model.getManifest(manifest.getContentGUID());
        assertEquals(ManifestConstants.ATOM, retrievedManifest.getManifestType());

        System.out.println("SeaOfStuffAddAtomTest: " + manifest.getContentGUID());
    }


    @Test
    public void testAtomTwiceNoUpdate() throws Exception {
        Collection<LocationBundle> locations = new ArrayList<>();
        Location location = new URILocation(Hashes.TEST_HTTP_BIN_URL);
        LocationBundle bundle = new ProvenanceLocationBundle(location);
        locations.add(bundle);
        AtomManifest manifest = model.addAtom(locations);

        File file = new File(configuration.getCacheDataPath() + manifest.getContentGUID());
        File manifestFile = new File(configuration.getLocalManifestsLocation() + manifest.getContentGUID() + ".json");
        long lmFile = file.lastModified();
        long lmManifestFile = manifestFile.lastModified();

        Thread.sleep(500);

        Collection<LocationBundle> newLocations = new ArrayList<>();
        Location newLocation = new URILocation(Hashes.TEST_HTTP_BIN_URL);
        LocationBundle newBundle = new ProvenanceLocationBundle(newLocation);
        newLocations.add(newBundle);
        AtomManifest newManifest = model.addAtom(newLocations);

        assertEquals(manifest.getContentGUID(), newManifest.getContentGUID());

        File newFile = new File(configuration.getCacheDataPath() + newManifest.getContentGUID());
        File newManifestFile = new File(configuration.getLocalManifestsLocation() + newManifest.getContentGUID() + ".json");
        long newlmFile = newFile.lastModified();
        long newlmManifestFile = newManifestFile.lastModified();

        assertEquals(newlmFile, lmFile);
        assertEquals(newlmManifestFile, lmManifestFile);
    }

}