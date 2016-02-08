package uk.ac.standrews.cs.sos.model.implementations;

import org.skyscreamer.jsonassert.JSONAssert;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.model.implementations.components.manifests.AssetManifest;
import uk.ac.standrews.cs.sos.model.implementations.components.manifests.CompoundManifest;
import uk.ac.standrews.cs.sos.model.implementations.components.manifests.ManifestConstants;
import uk.ac.standrews.cs.sos.model.implementations.utils.Content;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUID;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUIDsha1;
import uk.ac.standrews.cs.sos.model.interfaces.components.Manifest;

import java.util.ArrayList;
import java.util.Collection;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SeaOfStuffAddAssetTest extends SeaOfStuffGeneralTest {

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
        assertEquals(retrievedManifest.getManifestType(), ManifestConstants.ASSET);

        Content retrievedContent = ((AssetManifest) retrievedManifest).getContent();
        assertEquals(assetContent, retrievedContent);

        JSONAssert.assertEquals(manifest.toJSON().toString(), retrievedManifest.toJSON().toString(), false);
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

        // Flush the storage, so to force the manifest to be retrieved from file.
        index.flushDB();

        Manifest retrievedManifest = model.getManifest(manifest.getVersionGUID());
        assertEquals(retrievedManifest.getManifestType(), ManifestConstants.ASSET);

        Content retrievedContent = ((AssetManifest) retrievedManifest).getContent();
        assertEquals(assetContent, retrievedContent);

        JSONAssert.assertEquals(manifest.toJSON().toString(), retrievedManifest.toJSON().toString(), false);
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

        // Flush the storage, so to force the manifest to be retrieved from file.
        index.flushDB();

        Manifest retrievedManifest = model.getManifest(manifest.getVersionGUID());
        assertEquals(retrievedManifest.getManifestType(), ManifestConstants.ASSET);

        GUID retrievedInvariant = ((AssetManifest) retrievedManifest).getInvariantGUID();
        assertEquals(invariant, retrievedInvariant);

        Content retrievedContent = ((AssetManifest) retrievedManifest).getContent();
        assertEquals(assetContent, retrievedContent);

        Collection<GUID> retrievedMetadata = ((AssetManifest) retrievedManifest).getMetadata();
        assertTrue(retrievedMetadata.containsAll(metadata));

        Collection<GUID> retrievedPrevs = ((AssetManifest) retrievedManifest).getPreviousManifests();
        assertTrue(retrievedPrevs.containsAll(prevs));

        JSONAssert.assertEquals(manifest.toJSON().toString(), retrievedManifest.toJSON().toString(), false);
    }

    @Test
    public void testAddAssetAndVerify() throws Exception {
        Content cat = new Content("cat", new GUIDsha1("123"));
        Collection<Content> contents = new ArrayList<>();
        contents.add(cat);

        CompoundManifest compound = model.addCompound(contents);
        Content assetContent = new Content(compound.getContentGUID());

        AssetManifest manifest = model.addAsset(assetContent, null, null, null);
        Manifest retrievedManifest = model.getManifest(manifest.getVersionGUID());

        assertTrue(model.verifyManifest(model.getIdentity(), retrievedManifest));
    }


}