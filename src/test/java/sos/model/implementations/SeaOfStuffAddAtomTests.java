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
import sos.model.implementations.utils.Location;
import sos.model.interfaces.SeaOfStuff;
import sos.model.interfaces.components.Manifest;
import utils.Helper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;

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
        Location loc = createDummyDataFile();
        locations.add(loc);
        AtomManifest manifest = model.addAtom(locations);

        assertEquals(manifest.getManifestType(), ManifestConstants.ATOM);

        Manifest retrievedManifest = model.getManifest(manifest.getContentGUID());
        JSONAssert.assertEquals(manifest.toJSON().toString(), retrievedManifest.toJSON().toString(), true);

        Helper.deleteFile(configuration.getLocalManifestsLocation() + manifest.getContentGUID().toString());
    }

    private Location createDummyDataFile() throws FileNotFoundException, UnsupportedEncodingException, MalformedURLException {
        // FIXME - do not use this path!
        String location = "/Users/sic2/test/data/alec/A-20120905-163643/tl/nmr/09142012-1-tl-tl12-A/10/audita.txt";
        PrintWriter writer = new PrintWriter(location, "UTF-8");
        writer.println("The first line");
        writer.println("The second line");
        writer.close();

        return new Location("file://"+location);
    }

}