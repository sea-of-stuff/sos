package sos.managers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import constants.Hashes;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import redis.embedded.RedisServer;
import sos.configurations.DefaultConfiguration;
import sos.configurations.SeaConfiguration;
import sos.exceptions.ManifestSaveException;
import sos.model.implementations.components.manifests.AtomManifest;
import sos.model.implementations.components.manifests.ManifestConstants;
import sos.model.implementations.components.manifests.ManifestFactory;
import sos.model.implementations.utils.GUID;
import sos.model.implementations.utils.Location;
import sos.model.implementations.utils.URLLocation;
import sos.model.interfaces.components.Manifest;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ManifestsManagerTest {

    private static final String EXPECTED_JSON_ATOM_MANIFEST =
            "{\"Type\":\"Atom\"," +
                    "\"ContentGUID\":" + Hashes.TEST_HTTP_BIN_STRING_HASHES + "," +
                    "\"Locations\":[\"" + Hashes.TEST_HTTP_BIN_URL + "\"]" +
                    "}";

    private static final String EXPECTED_JSON_CONTENTS =
            "{\"Type\":\"Compound\"," +
                    "\"ContentGUID\":\"a412b829e2e1f4e982f4f75b99e4bbaebb73e411\"," +
                    "\"Contents\":" +
                    "[{" +
                    "\"Type\":\"label\"," +
                    "\"Value\":\"cat\"," +
                    "\"GUID\":\""+ Hashes.TEST_STRING_HASHED+"\"" +
                    "}]}";

    private JsonParser parser = new JsonParser();
    private JsonObject jsonObj = parser.parse(EXPECTED_JSON_CONTENTS).getAsJsonObject();
    private JsonObject jsonAtomObj = parser.parse(EXPECTED_JSON_ATOM_MANIFEST).getAsJsonObject();

    private RedisServer server;

    @BeforeMethod
    public void setUp() throws IOException {
        server = new RedisServer(RedisCache.REDIS_PORT);
        server.start();
    }

    @AfterMethod
    public void tearDown() {
        server.stop();
    }

    @Test
    public void testAddAtomManifest() throws Exception {
        MemCache cache = RedisCache.getInstance();
        SeaConfiguration configuration = new DefaultConfiguration();
        ManifestsManager manifestsManager = new ManifestsManager(configuration, cache);

        AtomManifest atomManifest = ManifestFactory.createAtomManifest(new ArrayList<Location>(Arrays.asList(new URLLocation(Hashes.TEST_HTTP_BIN_URL))));
        GUID guid = atomManifest.getContentGUID();
        try {
            manifestsManager.addManifest(atomManifest);

            Manifest manifest = manifestsManager.findManifest(guid);

            assertEquals(manifest.getManifestType(), ManifestConstants.ATOM);
            assertEquals(manifest.getContentGUID(), guid);
            assertEquals(manifest.isValid(), true);

            cache.flushDB();
            cache.killInstance();
        } catch (ManifestSaveException e) {
            throw new Exception();
        } finally {
            deleteFile(configuration.getLocalManifestsLocation() + guid.toString());
        }
    }

    @Test
    public void testAddCompoundManifest() {
        assertTrue(false);
    }

    public void deleteFile(String file) {
        Path path = Paths.get(file);
        try {
            Files.delete(path);
        } catch (NoSuchFileException x) {
            System.err.format("%s: no such" + " file or directory", path);
        } catch (DirectoryNotEmptyException x) {
            System.err.format("%s: not empty", path);
        } catch (IOException x) {
            // File permission problems are caught here.
            System.err.println(x);
        }

    }
}