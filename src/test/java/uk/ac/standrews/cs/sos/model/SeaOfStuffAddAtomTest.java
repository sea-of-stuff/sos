package uk.ac.standrews.cs.sos.model;

import org.apache.commons.io.IOUtils;
import org.skyscreamer.jsonassert.JSONAssert;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.constants.Hashes;
import uk.ac.standrews.cs.sos.interfaces.locations.Location;
import uk.ac.standrews.cs.sos.interfaces.manifests.Atom;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;
import uk.ac.standrews.cs.sos.model.locations.URILocation;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.model.manifests.AtomManifest;
import uk.ac.standrews.cs.sos.model.manifests.ManifestConstants;
import uk.ac.standrews.cs.utils.Helper;
import uk.ac.standrews.cs.utils.StreamsUtils;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;

import static org.testng.AssertJUnit.*;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SeaOfStuffAddAtomTest extends SeaOfStuffGeneralTest {

    private static final int PAUSE_TIME_MS = 500;

    @Test
    public void testAddAtom() throws Exception {
        Location location = Helper.createDummyDataFile(configuration);
        Atom manifest = model.addAtom(location);
        assertEquals(manifest.getManifestType(), ManifestConstants.ATOM);

        Manifest retrievedManifest = model.getManifest(manifest.getContentGUID());
        assertEquals(ManifestConstants.ATOM, retrievedManifest.getManifestType());
        Collection<LocationBundle> retrievedLocations = ((AtomManifest) retrievedManifest).getLocations();
        assertEquals(retrievedLocations.size(), 2);

        JSONAssert.assertEquals(manifest.toJSON().toString(), retrievedManifest.toJSON().toString(), true);
    }

    @Test
    public void testRetrieveAtomFromFile() throws Exception {
        Location location = Helper.createDummyDataFile(configuration);
        Atom manifest = model.addAtom(location);
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
        Location location = Helper.createDummyDataFile(configuration);
        Atom manifest = model.addAtom(location);
        assertEquals(manifest.getManifestType(), ManifestConstants.ATOM);

        Manifest retrievedManifest = model.getManifest(manifest.getContentGUID());
        InputStream inputStream = model.getAtomContent((AtomManifest) retrievedManifest);

        assertTrue(IOUtils.contentEquals(location.getSource(), inputStream));
    }

    @Test
    public void testAtomDataVerify() throws Exception {
        Location location = Helper.createDummyDataFile(configuration);
        Atom manifest = model.addAtom(location);
        assertEquals(manifest.getManifestType(), ManifestConstants.ATOM);

        Manifest retrievedManifest = model.getManifest(manifest.getContentGUID());
        assertTrue(retrievedManifest.verify(null));
    }

    @Test
    public void testAtomDataVerifyFails() throws Exception {
        Location location = Helper.createDummyDataFile(configuration);
        Atom manifest = model.addAtom(location);
        assertEquals(manifest.getManifestType(), ManifestConstants.ATOM);

        Manifest retrievedManifest = model.getManifest(manifest.getContentGUID());
        Collection<LocationBundle> retrievedLocations = ((AtomManifest) retrievedManifest).getLocations();
        LocationBundle cachedLocation = retrievedLocations.iterator().next();

        Helper.appendToFile(cachedLocation.getLocation(), "Data has changed");
        assertFalse(retrievedManifest.verify(null));
    }

    @Test
    public void testAddAtomFromURL() throws Exception {
        Location location = new URILocation("http://www.eastcottvets.co.uk/uploads/Animals/gingerkitten.jpg");
        Atom manifest = model.addAtom(location);
        assertEquals(manifest.getManifestType(), ManifestConstants.ATOM);

        Manifest retrievedManifest = model.getManifest(manifest.getContentGUID());
        assertEquals(ManifestConstants.ATOM, retrievedManifest.getManifestType());

        System.out.println("SeaOfStuffAddAtomTest: " + manifest.getContentGUID());
    }

    @Test
    public void testAddAtomFromURLHttps() throws Exception {
        Location location = new URILocation("https://i.ytimg.com/vi/NtgtMQwr3Ko/maxresdefault.jpg");
        Atom manifest = model.addAtom(location);
        assertEquals(manifest.getManifestType(), ManifestConstants.ATOM);

        Manifest retrievedManifest = model.getManifest(manifest.getContentGUID());
        assertEquals(ManifestConstants.ATOM, retrievedManifest.getManifestType());

        System.out.println("SeaOfStuffAddAtomTest: " + manifest.getContentGUID());
    }

    @Test
    public void testAddAtomFromURLHttpsPdf() throws Exception {
        Location location = new URILocation("https://studres.cs.st-andrews.ac.uk/CS1002/Lectures/W01/W01-Lecture.pdf");
        Atom manifest = model.addAtom(location);
        assertEquals(manifest.getManifestType(), ManifestConstants.ATOM);

        Manifest retrievedManifest = model.getManifest(manifest.getContentGUID());
        assertEquals(ManifestConstants.ATOM, retrievedManifest.getManifestType());

        System.out.println("SeaOfStuffAddAtomTest: " + manifest.getContentGUID());
    }

    @Test
    public void testAddAtomFromURLHttpsTextFile() throws Exception {
        Location location = new URILocation("https://studres.cs.st-andrews.ac.uk/CS1002/Examples/W01/Example1/W01Example1.java");
        Atom manifest = model.addAtom(location);
        assertEquals(manifest.getManifestType(), ManifestConstants.ATOM);

        Manifest retrievedManifest = model.getManifest(manifest.getContentGUID());
        assertEquals(ManifestConstants.ATOM, retrievedManifest.getManifestType());

        System.out.println("SeaOfStuffAddAtomTest: " + manifest.getContentGUID());
    }

    @Test
    public void testAddAtomTwiceNoUpdate() throws Exception {
        Location location = new URILocation(Hashes.TEST_HTTP_BIN_URL);
        Atom manifest = model.addAtom(location);

        File file = new File(configuration.getCacheDataPath() + manifest.getContentGUID());
        File manifestFile = new File(configuration.getManifestsDirectory() + manifest.getContentGUID() + ".json");
        long lmFile = file.lastModified();
        long lmManifestFile = manifestFile.lastModified();

        Thread.sleep(PAUSE_TIME_MS);

        Location newLocation = new URILocation(Hashes.TEST_HTTP_BIN_URL);
        Atom newManifest = model.addAtom(newLocation);

        assertEquals(manifest.getContentGUID(), newManifest.getContentGUID());

        File newFile = new File(configuration.getCacheDataPath() + newManifest.getContentGUID());
        File newManifestFile = new File(configuration.getManifestsDirectory() + newManifest.getContentGUID() + ".json");
        long newlmFile = newFile.lastModified();
        long newlmManifestFile = newManifestFile.lastModified();

        assertEquals(newlmFile, lmFile);
        assertEquals(newlmManifestFile, lmManifestFile);
    }

    @Test
    public void testAddAtomFromStream() throws Exception {
        String testString = "first line and second line";
        InputStream stream = StreamsUtils.StringToInputStream(testString);
        Atom manifest = model.addAtom(stream);
        assertNotNull(manifest.getContentGUID());
        assertEquals(manifest.getLocations().size(), 1);

        InputStream resultStream = model.getAtomContent(manifest);
        String resultString = Helper.InputStreamToString(resultStream);
        assertEquals(testString, resultString);
    }

}