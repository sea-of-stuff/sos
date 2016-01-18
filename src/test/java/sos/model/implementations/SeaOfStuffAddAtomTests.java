package sos.model.implementations;

import org.skyscreamer.jsonassert.JSONAssert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import sos.configurations.SeaConfiguration;
import sos.configurations.TestConfiguration;
import sos.exceptions.KeyGenerationException;
import sos.exceptions.KeyLoadedException;
import sos.managers.MemCache;
import sos.managers.RedisCache;
import sos.model.implementations.components.manifests.AtomManifest;
import sos.model.implementations.components.manifests.ManifestConstants;
import sos.model.implementations.utils.GUID;
import sos.model.implementations.utils.Location;
import sos.model.interfaces.SeaOfStuff;
import sos.model.interfaces.components.Manifest;
import utils.Helper;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import static org.testng.Assert.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SeaOfStuffAddAtomTests {

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
        assertEquals("Atom", retrievedManifest.getManifestType());

        Collection<Location> retrievedLocations = ((AtomManifest) retrievedManifest).getLocations();
        Iterator<Location> iterator = retrievedLocations.iterator();
        assertEquals(location, iterator.next());

        JSONAssert.assertEquals(manifest.toJSON().toString(), retrievedManifest.toJSON().toString(), true);

        deleteStoredFiles(manifest.getContentGUID());
        deleteStoredDataFile(location);
    }

    @Test
    public void testRetrieveAtomFromFile() {
        // TODO - test AtomManifestDeserialiser
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

}