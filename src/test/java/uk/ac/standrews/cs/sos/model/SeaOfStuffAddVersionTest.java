package uk.ac.standrews.cs.sos.model;

import org.skyscreamer.jsonassert.JSONAssert;
import org.testng.Assert;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.interfaces.manifests.Compound;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;
import uk.ac.standrews.cs.sos.interfaces.manifests.Version;
import uk.ac.standrews.cs.sos.model.manifests.CompoundType;
import uk.ac.standrews.cs.sos.model.manifests.Content;
import uk.ac.standrews.cs.sos.model.manifests.ManifestConstants;
import uk.ac.standrews.cs.sos.model.manifests.VersionManifest;

import java.util.ArrayList;
import java.util.Collection;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SeaOfStuffAddVersionTest extends SeaOfStuffGeneralTest {

    @Test
    public void testAddAsset() throws Exception {
        Content cat = new Content("cat", GUIDFactory.recreateGUID("123"));
        Collection<Content> contents = new ArrayList<>();
        contents.add(cat);

        Compound compound = model.addCompound(CompoundType.DATA, contents);
        Version manifest = model.addVersion(compound.getContentGUID(), null, null, null);
        Assert.assertEquals(manifest.getManifestType(), ManifestConstants.VERSION);

        Manifest retrievedManifest = model.getManifest(manifest.getVersionGUID());
        assertEquals(retrievedManifest.getManifestType(), ManifestConstants.VERSION);

        JSONAssert.assertEquals(manifest.toJSON().toString(), retrievedManifest.toJSON().toString(), false);
    }

    @Test
    public void testRetrieveAssetFromFile() throws Exception {
        Content cat = new Content("cat", GUIDFactory.recreateGUID("123"));
        Collection<Content> contents = new ArrayList<>();
        contents.add(cat);

        Compound compound = model.addCompound(CompoundType.DATA, contents);
        Version manifest = model.addVersion(compound.getContentGUID(), null, null, null);
        assertEquals(manifest.getManifestType(), ManifestConstants.VERSION);

        // Flush the storage, so to force the manifest to be retrieved from file.
        index.flushDB();

        Manifest retrievedManifest = model.getManifest(manifest.getVersionGUID());
        assertEquals(retrievedManifest.getManifestType(), ManifestConstants.VERSION);

        JSONAssert.assertEquals(manifest.toJSON().toString(), retrievedManifest.toJSON().toString(), false);
    }

    @Test
    public void testRetrieveAssetFromFileWithPrevsAndMeta() throws Exception {
        IGUID invariant = GUIDFactory.recreateGUID("1a2b3c");

        Content cat = new Content("cat", GUIDFactory.recreateGUID("123"));
        Collection<Content> contents = new ArrayList<>();
        contents.add(cat);

        Compound compound = model.addCompound(CompoundType.DATA, contents);

        Collection<IGUID> prevs = new ArrayList<>();
        prevs.add(GUIDFactory.recreateGUID("321"));
        prevs.add(GUIDFactory.recreateGUID("abcef"));

        Collection<IGUID> metadata = new ArrayList<>();
        metadata.add(GUIDFactory.recreateGUID("897"));
        metadata.add(GUIDFactory.recreateGUID("456"));

        Version manifest = model.addVersion(compound.getContentGUID(), invariant, prevs, metadata);
        assertEquals(manifest.getManifestType(), ManifestConstants.VERSION);

        // Flush the storage, so to force the manifest to be retrieved from file.
        index.flushDB();

        Manifest retrievedManifest = model.getManifest(manifest.getVersionGUID());
        assertEquals(retrievedManifest.getManifestType(), ManifestConstants.VERSION);

        IGUID retrievedInvariant = ((VersionManifest) retrievedManifest).getInvariantGUID();
        assertEquals(invariant, retrievedInvariant);

        Collection<IGUID> retrievedMetadata = ((VersionManifest) retrievedManifest).getMetadata();
        assertTrue(retrievedMetadata.containsAll(metadata));

        Collection<IGUID> retrievedPrevs = ((VersionManifest) retrievedManifest).getPreviousManifests();
        assertTrue(retrievedPrevs.containsAll(prevs));

        JSONAssert.assertEquals(manifest.toJSON().toString(), retrievedManifest.toJSON().toString(), false);
    }

    @Test
    public void testAddAssetAndVerify() throws Exception {
        Content cat = new Content("cat", GUIDFactory.recreateGUID("123"));
        Collection<Content> contents = new ArrayList<>();
        contents.add(cat);

        Compound compound = model.addCompound(CompoundType.DATA, contents);
        Version manifest = model.addVersion(compound.getContentGUID(), null, null, null);
        Manifest retrievedManifest = model.getManifest(manifest.getVersionGUID());

        assertTrue(model.verifyManifest(model.getIdentity(), retrievedManifest));
    }


}
