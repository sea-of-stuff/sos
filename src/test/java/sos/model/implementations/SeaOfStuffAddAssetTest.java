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
import sos.model.implementations.components.manifests.AssetManifest;
import sos.model.implementations.components.manifests.CompoundManifest;
import sos.model.implementations.components.manifests.ManifestConstants;
import sos.model.implementations.utils.Content;
import sos.model.implementations.utils.GUID;
import sos.model.implementations.utils.GUIDsha1;
import sos.model.interfaces.SeaOfStuff;
import sos.model.interfaces.components.Manifest;
import utils.Helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SeaOfStuffAddAssetTest {

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
    public void testAddAsset() throws Exception {
        Content cat = new Content("cat", new GUIDsha1("123"));
        Collection<Content> contents = new ArrayList<>();
        contents.add(cat);

        CompoundManifest compound = model.addCompound(contents);
        Content assetContent = new Content(compound.getContentGUID());

        AssetManifest manifest = model.addAsset(assetContent, null, null, null);
        assertEquals(manifest.getManifestType(), ManifestConstants.ASSET);

        Manifest retrievedManifest = model.getManifest(manifest.getVersionGUID());
        assertEquals(ManifestConstants.ASSET, retrievedManifest.getManifestType());

        Content retrievedContent = ((AssetManifest) retrievedManifest).getContent();
        assertEquals(assetContent, retrievedContent);

        JSONAssert.assertEquals(manifest.toJSON().toString(), retrievedManifest.toJSON().toString(), false);

        deleteStoredFiles(((AssetManifest) retrievedManifest).getVersionGUID());
        deleteStoredFiles(compound.getContentGUID());
    }

    @Test
    public void testRetrieveAssetFromFile() throws Exception {
        Content cat = new Content("cat", new GUIDsha1("123"));
        Collection<Content> contents = new ArrayList<>();
        contents.add(cat);

        CompoundManifest compound = model.addCompound(contents);
        Content assetContent = new Content(compound.getContentGUID());

        AssetManifest manifest = model.addAsset(assetContent, null, null, null);
        assertEquals(manifest.getManifestType(), ManifestConstants.ASSET);

        // Flush the cache, so to force the manifest to be retrieved from file.
        cache.flushDB();

        Manifest retrievedManifest = model.getManifest(manifest.getVersionGUID());
        assertEquals(ManifestConstants.ASSET, retrievedManifest.getManifestType());

        Content retrievedContent = ((AssetManifest) retrievedManifest).getContent();
        assertEquals(assetContent, retrievedContent);

        JSONAssert.assertEquals(manifest.toJSON().toString(), retrievedManifest.toJSON().toString(), false);

        deleteStoredFiles(((AssetManifest) retrievedManifest).getVersionGUID());
        deleteStoredFiles(compound.getContentGUID());
    }

    @Test
    public void testRetrieveAssetFromFileWithPrevsAndMeta() throws Exception {
        GUID invariant = new GUIDsha1("1a2b3c");

        Content cat = new Content("cat", new GUIDsha1("123"));
        Collection<Content> contents = new ArrayList<>();
        contents.add(cat);

        CompoundManifest compound = model.addCompound(contents);
        Content assetContent = new Content(compound.getContentGUID());

        Collection<GUID> prevs = new ArrayList<>();
        prevs.add(new GUIDsha1("321"));
        prevs.add(new GUIDsha1("abcef"));

        Collection<GUID> metadata = new ArrayList<>();
        metadata.add(new GUIDsha1("897"));
        metadata.add(new GUIDsha1("456"));

        AssetManifest manifest = model.addAsset(assetContent, invariant, prevs, metadata);
        assertEquals(manifest.getManifestType(), ManifestConstants.ASSET);

        // Flush the cache, so to force the manifest to be retrieved from file.
        cache.flushDB();

        Manifest retrievedManifest = model.getManifest(manifest.getVersionGUID());
        assertEquals(ManifestConstants.ASSET, retrievedManifest.getManifestType());

        GUID retrievedInvariant = ((AssetManifest) retrievedManifest).getInvariantGUID();
        assertEquals(invariant, retrievedInvariant);

        Content retrievedContent = ((AssetManifest) retrievedManifest).getContent();
        assertEquals(assetContent, retrievedContent);

        Collection<GUID> retrievedMetadata = ((AssetManifest) retrievedManifest).getMetadata();
        assertTrue(retrievedMetadata.containsAll(metadata));

        Collection<GUID> retrievedPrevs = ((AssetManifest) retrievedManifest).getPreviousManifests();
        assertTrue(retrievedPrevs.containsAll(prevs));

        JSONAssert.assertEquals(manifest.toJSON().toString(), retrievedManifest.toJSON().toString(), false);

        deleteStoredFiles(((AssetManifest) retrievedManifest).getVersionGUID());
        deleteStoredFiles(compound.getContentGUID());
    }

    private void deleteStoredFiles(GUID guid) {
        Helper.deleteFile(configuration.getLocalManifestsLocation() + guid.toString());
    }

}
