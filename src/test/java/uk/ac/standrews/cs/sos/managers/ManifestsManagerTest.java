package uk.ac.standrews.cs.sos.managers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.constants.Hashes;
import uk.ac.standrews.cs.sos.configurations.SeaConfiguration;
import uk.ac.standrews.cs.sos.exceptions.storage.ManifestSaveException;
import uk.ac.standrews.cs.sos.model.implementations.components.manifests.*;
import uk.ac.standrews.cs.sos.model.implementations.identity.IdentityImpl;
import uk.ac.standrews.cs.sos.model.implementations.locations.Location;
import uk.ac.standrews.cs.sos.model.implementations.locations.LocationBundle;
import uk.ac.standrews.cs.sos.model.implementations.locations.URILocation;
import uk.ac.standrews.cs.sos.model.implementations.utils.Content;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUID;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUIDsha1;
import uk.ac.standrews.cs.sos.model.interfaces.components.Manifest;
import uk.ac.standrews.cs.sos.model.interfaces.identity.Identity;
import uk.ac.standrews.cs.utils.Helper;

import java.io.File;
import java.io.IOException;
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

    private SeaConfiguration configuration;
    private Index index;

    @BeforeMethod
    public void setUp() throws IOException {
        configuration = SeaConfiguration.getInstance();
        index = LuceneIndex.getInstance(configuration);
    }

    @AfterMethod
    public void tearDown() throws IOException {
        index.flushDB();
        index.killInstance();

        FileUtils.deleteDirectory(new File(index.getConfiguration().getIndexPath()));
        FileUtils.cleanDirectory(new File(index.getConfiguration().getLocalManifestsLocation()));
        FileUtils.cleanDirectory(new File(index.getConfiguration().getCacheDataPath()));
        FileUtils.cleanDirectory(new File(index.getConfiguration().getDataPath()));

        configuration = null;
        index = null;
    }

    @Test
    public void testAddAtomManifest() throws Exception {
        ManifestsManager manifestsManager = new ManifestsManager(configuration, index);

        Location location = new URILocation(Hashes.TEST_HTTP_BIN_URL);
        LocationBundle bundle = new LocationBundle("prov", new Location[]{location});
        Collection<LocationBundle> bundles = new ArrayList<LocationBundle>();
        bundles.add(bundle);
        AtomManifest atomManifest = ManifestFactory.createAtomManifest(configuration, bundles);

        GUID guid = atomManifest.getContentGUID();
        try {
            manifestsManager.addManifest(atomManifest);
            Manifest manifest = manifestsManager.findManifest(guid);

            assertEquals(manifest.getManifestType(), ManifestConstants.ATOM);
            assertEquals(manifest.getContentGUID(), guid);
            assertEquals(manifest.isValid(), true);
        } catch (ManifestSaveException e) {
            throw new Exception();
        }
    }

    @Test
    public void testAddCompoundManifest() throws Exception {
        ManifestsManager manifestsManager = new ManifestsManager(configuration, index);

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
        }
    }

    @Test
    public void testAddAssetManifest() throws Exception {
        ManifestsManager manifestsManager = new ManifestsManager(configuration, index);

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
        }
    }

    @Test
    public void testUpdateAtomManifest() throws Exception {
        ManifestsManager manifestsManager = new ManifestsManager(configuration, index);

        LocationBundle firstLocation = Helper.createDummyDataFile(configuration, "first.txt");
        LocationBundle secondLocation = Helper.createDummyDataFile(configuration, "second.txt");

        AtomManifest atomManifest = ManifestFactory.createAtomManifest(
                configuration, new ArrayList<LocationBundle>(Arrays.asList(firstLocation)));
        GUID guid = atomManifest.getContentGUID();

        AtomManifest anotherManifest = ManifestFactory.createAtomManifest(
                configuration, new ArrayList<LocationBundle>(Arrays.asList(secondLocation)));
        GUID anotherGUID = anotherManifest.getContentGUID();

        assertEquals(guid, anotherGUID);

        try {
            manifestsManager.addManifest(atomManifest);
            manifestsManager.addManifest(anotherManifest);
            AtomManifest manifest = (AtomManifest) manifestsManager.findManifest(guid);

            assertEquals(manifest.getLocations().size(), 3);
        } catch (ManifestSaveException e) {
            throw new Exception();
        }
    }
}