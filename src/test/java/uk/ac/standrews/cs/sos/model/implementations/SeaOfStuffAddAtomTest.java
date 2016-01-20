package uk.ac.standrews.cs.sos.model.implementations;

import org.apache.commons.io.IOUtils;
import org.skyscreamer.jsonassert.JSONAssert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.configurations.SeaConfiguration;
import uk.ac.standrews.cs.sos.configurations.TestConfiguration;
import uk.ac.standrews.cs.sos.exceptions.KeyGenerationException;
import uk.ac.standrews.cs.sos.exceptions.KeyLoadedException;
import uk.ac.standrews.cs.sos.managers.MemCache;
import uk.ac.standrews.cs.sos.managers.RedisCache;
import uk.ac.standrews.cs.sos.model.implementations.components.manifests.AtomManifest;
import uk.ac.standrews.cs.sos.model.implementations.components.manifests.ManifestConstants;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUID;
import uk.ac.standrews.cs.sos.model.implementations.utils.Location;
import uk.ac.standrews.cs.sos.model.interfaces.SeaOfStuff;
import uk.ac.standrews.cs.sos.model.interfaces.components.Manifest;
import utils.Helper;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import static org.testng.Assert.*;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SeaOfStuffAddAtomTest {

    private SeaOfStuff model;
    private MemCache cache;
    private SeaConfiguration configuration;

    @BeforeMethod
    public void setUp() {
        try {
            configuration = new TestConfiguration();
            cache = RedisCache.getInstance();
            model = new SeaOfStuffImpl(configuration, cache);
        } catch (KeyGenerationException e) {
            e.printStackTrace();
        } catch (KeyLoadedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterMethod
    public void tearDown() {
        cache.flushDB();
        cache.killInstance();
    }

    @Test
    public void testAddAtom() throws Exception {
        Collection<Location> locations = new ArrayList<Location>();
        Location location = createDummyDataFile();
        locations.add(location);
        AtomManifest manifest = model.addAtom(locations);
        assertEquals(manifest.getManifestType(), ManifestConstants.ATOM);

        Manifest retrievedManifest = model.getManifest(manifest.getContentGUID());
        assertEquals(ManifestConstants.ATOM, retrievedManifest.getManifestType());

        Collection<Location> retrievedLocations = ((AtomManifest) retrievedManifest).getLocations();
        Iterator<Location> iterator = retrievedLocations.iterator();
        assertEquals(location, iterator.next());

        JSONAssert.assertEquals(manifest.toJSON().toString(), retrievedManifest.toJSON().toString(), true);

        deleteStoredFiles(manifest.getContentGUID());
        deleteStoredDataFile(location);
    }

    @Test
    public void testRetrieveAtomFromFile() throws Exception {
        Collection<Location> locations = new ArrayList<Location>();
        Location location = createDummyDataFile();
        locations.add(location);
        AtomManifest manifest = model.addAtom(locations);
        assertEquals(manifest.getManifestType(), ManifestConstants.ATOM);

        // Flush the cache, so to force the manifest to be retrieved from file.
        cache.flushDB();

        Manifest retrievedManifest = model.getManifest(manifest.getContentGUID());
        assertEquals(ManifestConstants.ATOM, retrievedManifest.getManifestType());

        Collection<Location> retrievedLocations = ((AtomManifest) retrievedManifest).getLocations();
        Iterator<Location> iterator = retrievedLocations.iterator();
        assertEquals(location, iterator.next());

        JSONAssert.assertEquals(manifest.toJSON().toString(), retrievedManifest.toJSON().toString(), true);

        deleteStoredFiles(manifest.getContentGUID());
        deleteStoredDataFile(location);
    }

    @Test
    public void testRetrieveAtomData() throws Exception {
        Collection<Location> locations = new ArrayList<Location>();
        Location location = createDummyDataFile();
        locations.add(location);
        AtomManifest manifest = model.addAtom(locations);
        assertEquals(manifest.getManifestType(), ManifestConstants.ATOM);

        Manifest retrievedManifest = model.getManifest(manifest.getContentGUID());
        InputStream inputStream = model.getAtomContent((AtomManifest) retrievedManifest);

        assertTrue(IOUtils.contentEquals(location.getSource(), inputStream));

        deleteStoredFiles(manifest.getContentGUID());
        deleteStoredDataFile(location);
    }

    @Test
    public void testAtomDataVerify() throws Exception {
        Collection<Location> locations = new ArrayList<Location>();
        Location location = createDummyDataFile();
        locations.add(location);
        AtomManifest manifest = model.addAtom(locations);
        assertEquals(manifest.getManifestType(), ManifestConstants.ATOM);

        Manifest retrievedManifest = model.getManifest(manifest.getContentGUID());
        assertTrue(retrievedManifest.verify());

        deleteStoredFiles(manifest.getContentGUID());
        deleteStoredDataFile(location);
    }

    @Test
    public void testAtomDataVerifyFails() throws Exception {
        Collection<Location> locations = new ArrayList<Location>();
        Location location = createDummyDataFile();
        locations.add(location);
        AtomManifest manifest = model.addAtom(locations);
        assertEquals(manifest.getManifestType(), ManifestConstants.ATOM);

        Manifest retrievedManifest = model.getManifest(manifest.getContentGUID());

        appendToFile(location, "Data has changed");
        assertFalse(retrievedManifest.verify());

        deleteStoredFiles(manifest.getContentGUID());
        deleteStoredDataFile(location);
    }

    private void deleteStoredFiles(GUID guid) {
        Helper.deleteFile(configuration.getLocalManifestsLocation() + guid.toString());
    }

    private void deleteStoredDataFile(Location location) throws URISyntaxException {
        Helper.deleteFile(Helper.localURItoPath(location));
    }

    private Location createDummyDataFile() throws FileNotFoundException, UnsupportedEncodingException, MalformedURLException {
        String location = configuration.getDataPath() + "testData.txt";

        File file = new File(location);
        File parent = file.getParentFile();
        if(!parent.exists() && !parent.mkdirs()){
            throw new IllegalStateException("Couldn't create dir: " + parent);
        }

        PrintWriter writer = new PrintWriter(file);
        writer.println("The first line");
        writer.println("The second line");
        writer.close();

        return new Location("file://"+location);
    }

    private void appendToFile(Location location, String text) throws URISyntaxException, FileNotFoundException {
        PrintWriter writer = new PrintWriter(new FileOutputStream(
                new File(Helper.localURItoPath(location)),
                true));

        writer.append(text);
        writer.close();
    }

}