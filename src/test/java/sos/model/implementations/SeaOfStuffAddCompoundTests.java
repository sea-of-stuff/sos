package sos.model.implementations;

import IO.utils.StreamsUtils;
import constants.Hashes;
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
import sos.model.implementations.components.manifests.CompoundManifest;
import sos.model.implementations.components.manifests.ManifestConstants;
import sos.model.implementations.utils.Content;
import sos.model.implementations.utils.GUID;
import sos.model.implementations.utils.GUIDsha1;
import sos.model.interfaces.SeaOfStuff;
import sos.model.interfaces.components.Manifest;
import utils.Helper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import static org.testng.Assert.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SeaOfStuffAddCompoundTests {

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
    public void testAddCompound() throws Exception {
        InputStream inputStreamFake = StreamsUtils.StringToInputStream(Hashes.TEST_STRING);
        GUIDsha1 guid = new GUIDsha1(inputStreamFake);

        Content cat = new Content("cat", guid);
        Collection<Content> contents = new ArrayList<>();
        contents.add(cat);

        CompoundManifest manifest = model.addCompound(contents);
        assertEquals(manifest.getManifestType(), ManifestConstants.COMPOUND);

        Manifest retrievedManifest = model.getManifest(manifest.getContentGUID());
        assertEquals("Compound", retrievedManifest.getManifestType());

        Collection<Content> retrievedContents = ((CompoundManifest) retrievedManifest).getContents();
        Iterator<Content> iterator = retrievedContents.iterator();
        assertEquals(cat, iterator.next());

        JSONAssert.assertEquals(manifest.toJSON().toString(), retrievedManifest.toJSON().toString(), true);

        deleteStoredFiles(retrievedManifest.getContentGUID());
    }

    private void deleteStoredFiles(GUID guid) {
        Helper.deleteFile(configuration.getLocalManifestsLocation() + guid.toString());
    }

}
