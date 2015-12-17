package sos.managers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import constants.Hashes;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import redis.embedded.RedisServer;
import sos.configurations.DefaultConfiguration;
import sos.configurations.SeaConfiguration;
import sos.exceptions.ManifestSaveException;
import sos.model.implementations.components.manifests.AtomManifest;
import sos.model.implementations.components.manifests.ManifestConstants;
import sos.model.implementations.components.manifests.ManifestFactory;
import sos.model.implementations.utils.GUID;
import sos.model.implementations.utils.GUIDsha1;
import sos.model.implementations.utils.Location;
import sos.model.implementations.utils.URLLocation;
import sos.model.interfaces.components.Manifest;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.testng.Assert.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ManifestsManagerTest {

    private static final String EXPECTED_JSON_ATOM_MANIFEST =
            "{\"Type\":\"Atom\"," +
                    "\"ManifestGUID\":\"b57ac21f9edc88e961ed8c60700e1b5f9d202aa1\"," +
                    "\"ContentGUID\":" + Hashes.TEST_HTTP_BIN_STRING_HASHES + "," +
                    "\"Locations\":[\"" + Hashes.TEST_HTTP_BIN_URL + "\"]" +
                    "}";

    private static final String EXPECTED_JSON_CONTENTS =
            "{\"Type\":\"Compound\"," +
                    "\"ManifestGUID\":\"2ffdfe2d899c4db7cde6d76cc2ade7ff49d5e0b9\"," +
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

    @BeforeTest
    public void setUp() throws IOException {
        server = new RedisServer(RedisCache.REDIS_PORT);
        server.start();
    }

    @AfterTest
    public void tearDown() {
        server.stop();
    }

    @Test
    public void testAddAtomManifest() throws Exception {
        MemCache cache = RedisCache.getInstance();
        SeaConfiguration configuration = new DefaultConfiguration();
        ManifestsManager manifestsManager = new ManifestsManager(configuration, null, cache);

        AtomManifest mockedManifest = mock(AtomManifest.class);
        when(mockedManifest.getManifestGUID()).thenReturn(new GUIDsha1("b57ac21f9edc88e961ed8c60700e1b5f9d202aa1"));
        when(mockedManifest.getManifestType()).thenReturn(ManifestConstants.ATOM);
        when(mockedManifest.getContentGUID()).thenReturn(new GUIDsha1(Hashes.TEST_HTTP_BIN_STRING_HASHES));
        when(mockedManifest.getLocations()).thenReturn(new ArrayList<Location>(Arrays.asList(new URLLocation(Hashes.TEST_HTTP_BIN_URL))));
        when(mockedManifest.isValid()).thenReturn(true);
        when(mockedManifest.toJSON()).thenReturn(jsonAtomObj);
        try {
            manifestsManager.addManifest(mockedManifest);

            Manifest manifest = manifestsManager.findManifest(new GUIDsha1("b57ac21f9edc88e961ed8c60700e1b5f9d202aa1"));

            assertEquals(manifest.getManifestType(), ManifestConstants.ATOM);
            assertEquals(manifest.getContentGUID(), new GUIDsha1(Hashes.TEST_HTTP_BIN_STRING_HASHES));
            assertEquals(manifest.isValid(), true);

            cache.killInstance();
        } catch (ManifestSaveException e) {
            throw new Exception();
        } finally {
            deleteFile(configuration.getLocalManifestsLocation() + "b57ac21f9edc88e961ed8c60700e1b5f9d202aa1");
        }
    }

    @Test
    public void testAddAtomManifest2() throws Exception {
        MemCache cache = RedisCache.getInstance();
        SeaConfiguration configuration = new DefaultConfiguration();
        ManifestsManager manifestsManager = new ManifestsManager(configuration, null, cache);

        AtomManifest atomManifest = ManifestFactory.createAtomManifest(new ArrayList<Location>(Arrays.asList(new URLLocation(Hashes.TEST_HTTP_BIN_URL))));
        GUID guid = atomManifest.getManifestGUID();
        try {
            manifestsManager.addManifest(atomManifest);

            Manifest manifest = manifestsManager.findManifest(guid);

            assertEquals(manifest.getManifestType(), ManifestConstants.ATOM);
            assertEquals(manifest.getContentGUID(), atomManifest.getGUIDContent());
            assertEquals(manifest.isValid(), true);

            cache.flushDB();
            cache.killInstance();
        } catch (ManifestSaveException e) {
            throw new Exception();
        } finally {
            deleteFile(configuration.getLocalManifestsLocation() + guid.toString());
        }
    }

    public void deleteFile(String file) {
        Path path = Paths.get(file);
        try {
            Files.delete(path);
        } catch (NoSuchFileException x) {
            System.err.format("%s: no such" + " file or directory%n", path);
        } catch (DirectoryNotEmptyException x) {
            System.err.format("%s: not empty%n", path);
        } catch (IOException x) {
            // File permission problems are caught here.
            System.err.println(x);
        }

    }
}