package sos.managers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import constants.Hashes;
import org.testng.annotations.Test;
import sos.configurations.SeaConfiguration;
import sos.configurations.TestConfiguration;
import sos.exceptions.ManifestSaveException;
import sos.model.implementations.components.manifests.*;
import sos.model.implementations.identity.IdentityImpl;
import sos.model.implementations.utils.Content;
import sos.model.implementations.utils.GUID;
import sos.model.implementations.utils.GUIDsha1;
import sos.model.implementations.utils.Location;
import sos.model.interfaces.components.Manifest;
import sos.model.interfaces.identity.Identity;
import utils.Helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.testng.Assert.assertEquals;

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

    @Test
    public void testAddAtomManifest() throws Exception {
        MemCache cache = RedisCache.getInstance();
        SeaConfiguration configuration = new TestConfiguration();
        ManifestsManager manifestsManager = new ManifestsManager(configuration, cache);

        AtomManifest atomManifest = ManifestFactory.createAtomManifest(new ArrayList<Location>(Arrays.asList(new Location(Hashes.TEST_HTTP_BIN_URL))));
        GUID guid = atomManifest.getContentGUID();
        try {
            manifestsManager.addManifest(atomManifest);

            Manifest manifest = manifestsManager.findManifest(guid);

            assertEquals(manifest.getManifestType(), ManifestConstants.ATOM);
            assertEquals(manifest.getContentGUID(), guid);
            assertEquals(manifest.isValid(), true);
        } catch (ManifestSaveException e) {
            throw new Exception();
        } finally {
            cache.flushDB();
            cache.killInstance();

            Helper.deleteFile(configuration.getLocalManifestsLocation() + guid.toString());
        }
    }

    @Test
    public void testAddCompoundManifest() throws Exception {
        MemCache cache = RedisCache.getInstance();
        SeaConfiguration configuration = new TestConfiguration();
        ManifestsManager manifestsManager = new ManifestsManager(configuration, cache);

        Identity identity = new IdentityImpl(configuration);
        Content content = new Content("Cat", new GUIDsha1("123"));
        Collection<Content> contents = new ArrayList<Content>();
        contents.add(content);

        CompoundManifest compoundManifest = ManifestFactory.createCompoundManifest(contents, identity);
        GUID guid = compoundManifest.getContentGUID();
        try {
            manifestsManager.addManifest(compoundManifest);

            Manifest manifest = manifestsManager.findManifest(guid);

            assertEquals(manifest.getManifestType(), ManifestConstants.COMPOUND);
            assertEquals(manifest.getContentGUID(), guid);
            assertEquals(manifest.isValid(), true);
        } catch (ManifestSaveException e) {
            throw new Exception();
        } finally {
            cache.flushDB();
            cache.killInstance();

            Helper.deleteFile(configuration.getLocalManifestsLocation() + guid.toString());
        }
    }


    @Test
    public void testAddAssetManifest() throws Exception {
        MemCache cache = RedisCache.getInstance();
        SeaConfiguration configuration = new TestConfiguration();
        ManifestsManager manifestsManager = new ManifestsManager(configuration, cache);

        Identity identity = new IdentityImpl(configuration);
        Content content = new Content("Cat", new GUIDsha1("123"));

        AssetManifest assetManifest = ManifestFactory.createAssetManifest(content, null, null, null, identity);
        GUID guid = assetManifest.getVersionGUID();
        try {
            manifestsManager.addManifest(assetManifest);

            Manifest manifest = manifestsManager.findManifest(guid);

            assertEquals(manifest.getManifestType(), ManifestConstants.ASSET);
            assertEquals(manifest.getContentGUID(), content.getGUID());
            assertEquals(manifest.isValid(), true);
        } catch (ManifestSaveException e) {
            throw new Exception();
        } finally {
            cache.flushDB();
            cache.killInstance();

            Helper.deleteFile(configuration.getLocalManifestsLocation() + guid.toString());
        }
    }
}