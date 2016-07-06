package uk.ac.standrews.cs.sos.node.SOSImpl.Client;

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
public class SOSAddVersionTest extends ClientTest {

    @Test
    public void testAddAsset() throws Exception {
        Content cat = new Content("cat", GUIDFactory.recreateGUID("123"));
        Collection<Content> contents = new ArrayList<>();
        contents.add(cat);

        Compound compound = client.addCompound(CompoundType.DATA, contents);
        Version manifest = client.addVersion(compound.getContentGUID(), null, null, null);
        Assert.assertEquals(manifest.getManifestType(), ManifestConstants.VERSION);

        Manifest retrievedManifest = client.getManifest(manifest.getVersionGUID());
        assertEquals(retrievedManifest.getManifestType(), ManifestConstants.VERSION);

        JSONAssert.assertEquals(manifest.toString(), retrievedManifest.toString(), false);
    }

    @Test
    public void testRetrieveAssetFromFile() throws Exception {
        Content cat = new Content("cat", GUIDFactory.recreateGUID("123"));
        Collection<Content> contents = new ArrayList<>();
        contents.add(cat);

        Compound compound = client.addCompound(CompoundType.DATA, contents);
        Version manifest = client.addVersion(compound.getContentGUID(), null, null, null);
        assertEquals(manifest.getManifestType(), ManifestConstants.VERSION);

        // Flush the internalStorage, so to force the manifest to be retrieved from file.
        index.flushDB();

        Manifest retrievedManifest = client.getManifest(manifest.getVersionGUID());
        assertEquals(retrievedManifest.getManifestType(), ManifestConstants.VERSION);

        JSONAssert.assertEquals(manifest.toString(), retrievedManifest.toString(), false);
    }

    @Test
    public void testRetrieveAssetFromFileWithPrevsAndMeta() throws Exception {
        IGUID invariant = GUIDFactory.recreateGUID("1a2b3c");

        Content cat = new Content("cat", GUIDFactory.recreateGUID("123"));
        Collection<Content> contents = new ArrayList<>();
        contents.add(cat);

        Compound compound = client.addCompound(CompoundType.DATA, contents);

        Collection<IGUID> prevs = new ArrayList<>();
        prevs.add(GUIDFactory.recreateGUID("321"));
        prevs.add(GUIDFactory.recreateGUID("abcef"));

        Collection<IGUID> metadata = new ArrayList<>();
        metadata.add(GUIDFactory.recreateGUID("897"));
        metadata.add(GUIDFactory.recreateGUID("456"));

        Version manifest = client.addVersion(compound.getContentGUID(), invariant, prevs, metadata);
        assertEquals(manifest.getManifestType(), ManifestConstants.VERSION);

        // Flush the internalStorage, so to force the manifest to be retrieved from file.
        index.flushDB();

        Manifest retrievedManifest = client.getManifest(manifest.getVersionGUID());
        assertEquals(retrievedManifest.getManifestType(), ManifestConstants.VERSION);

        IGUID retrievedInvariant = ((VersionManifest) retrievedManifest).getInvariantGUID();
        assertEquals(invariant, retrievedInvariant);

        Collection<IGUID> retrievedMetadata = ((VersionManifest) retrievedManifest).getMetadata();
        assertTrue(retrievedMetadata.containsAll(metadata));

        Collection<IGUID> retrievedPrevs = ((VersionManifest) retrievedManifest).getPreviousManifests();
        assertTrue(retrievedPrevs.containsAll(prevs));

        JSONAssert.assertEquals(manifest.toString(), retrievedManifest.toString(), false);
    }

    @Test
    public void testAddAssetAndVerify() throws Exception {
        Content cat = new Content("cat", GUIDFactory.recreateGUID("123"));
        Collection<Content> contents = new ArrayList<>();
        contents.add(cat);

        Compound compound = client.addCompound(CompoundType.DATA, contents);
        Version manifest = client.addVersion(compound.getContentGUID(), null, null, null);
        Manifest retrievedManifest = client.getManifest(manifest.getVersionGUID());

        assertTrue(client.verifyManifest(client.getIdentity(), retrievedManifest));
    }


}
