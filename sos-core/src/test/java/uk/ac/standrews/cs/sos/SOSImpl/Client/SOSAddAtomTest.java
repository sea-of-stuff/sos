package uk.ac.standrews.cs.sos.SOSImpl.Client;

import org.apache.commons.io.IOUtils;
import org.skyscreamer.jsonassert.JSONAssert;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.constants.Hashes;
import uk.ac.standrews.cs.sos.interfaces.locations.Location;
import uk.ac.standrews.cs.sos.interfaces.manifests.Atom;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;
import uk.ac.standrews.cs.sos.model.locations.URILocation;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.model.manifests.AtomManifest;
import uk.ac.standrews.cs.sos.model.manifests.ManifestConstants;
import uk.ac.standrews.cs.sos.model.manifests.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.utils.HelperTest;
import uk.ac.standrews.cs.storage.interfaces.Directory;
import uk.ac.standrews.cs.storage.interfaces.File;

import java.io.InputStream;
import java.util.Collection;

import static org.testng.AssertJUnit.*;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSAddAtomTest extends ClientTest {

    private static final int PAUSE_TIME_MS = 500;
    private static final int TEST_TIMEOUT = 10000;

    @Test
    public void testAddAtom() throws Exception {
        Location location = HelperTest.createDummyDataFile(internalStorage);
        AtomBuilder builder = new AtomBuilder().setLocation(location);
        Atom manifest = client.addAtom(builder);
        assertEquals(manifest.getManifestType(), ManifestConstants.ATOM);

        Manifest retrievedManifest = client.getManifest(manifest.getContentGUID());
        assertEquals(ManifestConstants.ATOM, retrievedManifest.getManifestType());
        Collection<LocationBundle> retrievedLocations = ((AtomManifest) retrievedManifest).getLocations();
        assertEquals(2, retrievedLocations.size());

        JSONAssert.assertEquals(manifest.toString(), retrievedManifest.toString(), true);
    }

    @Test
    public void testRetrieveAtomFromFile() throws Exception {
        Location location = HelperTest.createDummyDataFile(internalStorage);
        AtomBuilder builder = new AtomBuilder().setLocation(location);
        Atom manifest = client.addAtom(builder);
        assertEquals(manifest.getManifestType(), ManifestConstants.ATOM);

        // Flush the storage, so to force the manifest to be retrieved from file.
        index.flushDB();

        Manifest retrievedManifest = client.getManifest(manifest.getContentGUID());
        assertEquals(ManifestConstants.ATOM, retrievedManifest.getManifestType());
        Collection<LocationBundle> retrievedLocations = ((AtomManifest) retrievedManifest).getLocations();
        assertEquals(retrievedLocations.size(), 2);

        JSONAssert.assertEquals(manifest.toString(), retrievedManifest.toString(), true);
    }

    @Test
    public void testRetrieveAtomData() throws Exception {
        Location location = HelperTest.createDummyDataFile(internalStorage);
        AtomBuilder builder = new AtomBuilder().setLocation(location);
        Atom manifest = client.addAtom(builder);
        assertEquals(manifest.getManifestType(), ManifestConstants.ATOM);

        Manifest retrievedManifest = client.getManifest(manifest.getContentGUID());
        InputStream inputStream = client.getAtomContent((AtomManifest) retrievedManifest);

        assertTrue(IOUtils.contentEquals(location.getSource(), inputStream));
        inputStream.close();
    }

    @Test
    public void testAtomDataVerify() throws Exception {
        Location location = HelperTest.createDummyDataFile(internalStorage);
        AtomBuilder builder = new AtomBuilder().setLocation(location);
        Atom manifest = client.addAtom(builder);
        assertEquals(manifest.getManifestType(), ManifestConstants.ATOM);

        Manifest retrievedManifest = client.getManifest(manifest.getContentGUID());
        assertTrue(retrievedManifest.verify(null));
    }

    @Test
    public void testAtomDataVerifyFails() throws Exception {
        Location location = HelperTest.createDummyDataFile(internalStorage);
        AtomBuilder builder = new AtomBuilder().setLocation(location);
        Atom manifest = client.addAtom(builder);
        assertEquals(manifest.getManifestType(), ManifestConstants.ATOM);

        Manifest retrievedManifest = client.getManifest(manifest.getContentGUID());
        Collection<LocationBundle> retrievedLocations = ((AtomManifest) retrievedManifest).getLocations();
        LocationBundle cachedLocation = retrievedLocations.iterator().next();

        HelperTest.appendToFile(cachedLocation.getLocation(), "Data has changed");
        assertFalse(retrievedManifest.verify(null));
    }

    @Test (timeOut = TEST_TIMEOUT)
    public void testAddAtomFromURL() throws Exception {
        Location location = new URILocation("http://www.eastcottvets.co.uk/uploads/Animals/gingerkitten.jpg");
        AtomBuilder builder = new AtomBuilder().setLocation(location);
        Atom manifest = client.addAtom(builder);
        assertEquals(manifest.getManifestType(), ManifestConstants.ATOM);

        Manifest retrievedManifest = client.getManifest(manifest.getContentGUID());
        assertEquals(ManifestConstants.ATOM, retrievedManifest.getManifestType());

        System.out.println("SOSAddAtomTest: " + manifest.getContentGUID());
    }

    @Test (timeOut = TEST_TIMEOUT)
    public void testAddAtomFromURLHttps() throws Exception {
        Location location = new URILocation("https://i.ytimg.com/vi/NtgtMQwr3Ko/maxresdefault.jpg");
        AtomBuilder builder = new AtomBuilder().setLocation(location);
        Atom manifest = client.addAtom(builder);
        assertEquals(manifest.getManifestType(), ManifestConstants.ATOM);

        Manifest retrievedManifest = client.getManifest(manifest.getContentGUID());
        assertEquals(ManifestConstants.ATOM, retrievedManifest.getManifestType());

        System.out.println("SOSAddAtomTest: " + manifest.getContentGUID());
    }

    @Test (timeOut = TEST_TIMEOUT)
    public void testAddAtomFromURLHttpsPdf() throws Exception {
        Location location = new URILocation("https://www.adobe.com/be_en/active-use/pdf/Alice_in_Wonderland.pdf");
        AtomBuilder builder = new AtomBuilder().setLocation(location);
        Atom manifest = client.addAtom(builder);
        assertEquals(manifest.getManifestType(), ManifestConstants.ATOM);

        Manifest retrievedManifest = client.getManifest(manifest.getContentGUID());
        assertEquals(ManifestConstants.ATOM, retrievedManifest.getManifestType());

        System.out.println("SOSAddAtomTest: " + manifest.getContentGUID());
    }

    @Test (timeOut = TEST_TIMEOUT)
    public void testAddAtomFromURLHttpsTextFile() throws Exception {
        Location location = new URILocation("http://www.umich.edu/~umfandsf/other/ebooks/alice30.txt");
        AtomBuilder builder = new AtomBuilder().setLocation(location);
        Atom manifest = client.addAtom(builder);
        assertEquals(manifest.getManifestType(), ManifestConstants.ATOM);

        Manifest retrievedManifest = client.getManifest(manifest.getContentGUID());
        assertEquals(ManifestConstants.ATOM, retrievedManifest.getManifestType());

        System.out.println("SOSAddAtomTest: " + manifest.getContentGUID());
    }

    @Test (timeOut = TEST_TIMEOUT)
    public void testAddAtomTwiceNoUpdate() throws Exception {
        Location location = new URILocation(Hashes.TEST_HTTP_BIN_URL);
        AtomBuilder builder = new AtomBuilder().setLocation(location);
        Atom manifest = client.addAtom(builder);

        Directory dataDir = internalStorage.getDataDirectory();
        Directory manifestsDir = internalStorage.getManifestDirectory();

        File file = internalStorage.createFile(dataDir, manifest.getContentGUID().toString());
        File manifestFile = internalStorage.createFile(manifestsDir, manifest.getContentGUID() + ".json");
        long lmFile = file.lastModified();
        long lmManifestFile = manifestFile.lastModified();

        Thread.sleep(PAUSE_TIME_MS);

        Location newLocation = new URILocation(Hashes.TEST_HTTP_BIN_URL);
        AtomBuilder secondBuilder = new AtomBuilder().setLocation(newLocation);
        Atom newManifest = client.addAtom(secondBuilder);

        assertEquals(manifest.getContentGUID(), newManifest.getContentGUID());

        File newFile = internalStorage.createFile(dataDir, newManifest.getContentGUID().toString());
        File newManifestFile = internalStorage.createFile(manifestsDir, newManifest.getContentGUID() + ".json");
        long newlmFile = newFile.lastModified();
        long newlmManifestFile = newManifestFile.lastModified();

        assertEquals(newlmFile, lmFile);
        assertEquals(newlmManifestFile, lmManifestFile);
    }

    @Test
    public void testAddAtomFromStream() throws Exception {
        String testString = "first line and second line";
        InputStream stream = HelperTest.StringToInputStream(testString);
        AtomBuilder builder = new AtomBuilder().setInputStream(stream);
        Atom manifest = client.addAtom(builder);
        assertNotNull(manifest.getContentGUID());
        assertEquals(manifest.getLocations().size(), 1);

        InputStream inputStream = client.getAtomContent(manifest);
        String resultString = HelperTest.InputStreamToString(inputStream);
        assertEquals(testString, resultString);
        inputStream.close();
    }

}