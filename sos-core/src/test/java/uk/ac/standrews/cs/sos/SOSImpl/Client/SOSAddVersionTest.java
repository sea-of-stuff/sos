package uk.ac.standrews.cs.sos.SOSImpl.Client;

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
import uk.ac.standrews.cs.sos.model.manifests.builders.VersionBuilder;

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

        VersionBuilder builder = new VersionBuilder(compound.getContentGUID());
        Version manifest = client.addVersion(builder);
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

        VersionBuilder builder = new VersionBuilder(compound.getContentGUID());
        Version manifest = client.addVersion(builder);
        assertEquals(manifest.getManifestType(), ManifestConstants.VERSION);

        // Flush the storage, so to force the manifest to be retrieved from file.
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

        VersionBuilder builder = new VersionBuilder(compound.getContentGUID())
                .setInvariant(invariant)
                .setPrevious(prevs)
                .setMetadataCollection(metadata);
        Version manifest = client.addVersion(builder);
        assertEquals(manifest.getManifestType(), ManifestConstants.VERSION);

        // Flush the storage, so to force the manifest to be retrieved from file.
        index.flushDB();

        Manifest retrievedManifest = client.getManifest(manifest.getVersionGUID());
        assertEquals(retrievedManifest.getManifestType(), ManifestConstants.VERSION);

        IGUID retrievedInvariant = ((VersionManifest) retrievedManifest).getInvariantGUID();
        assertEquals(invariant, retrievedInvariant);

        Collection<IGUID> retrievedMetadata = ((VersionManifest) retrievedManifest).getMetadata();
        assertTrue(retrievedMetadata.containsAll(metadata));

        Collection<IGUID> retrievedPrevs = ((VersionManifest) retrievedManifest).getPreviousVersions();
        assertTrue(retrievedPrevs.containsAll(prevs));

        JSONAssert.assertEquals(manifest.toString(), retrievedManifest.toString(), false);
    }

    @Test
    public void testAddAssetAndVerify() throws Exception {
        Content cat = new Content("cat", GUIDFactory.recreateGUID("123"));
        Collection<Content> contents = new ArrayList<>();
        contents.add(cat);

        Compound compound = client.addCompound(CompoundType.DATA, contents);

        VersionBuilder builder = new VersionBuilder(compound.getContentGUID());
        Version manifest = client.addVersion(builder);
        Manifest retrievedManifest = client.getManifest(manifest.getVersionGUID());

        assertTrue(client.verifyManifest(localSOSNode.getIdentity(), retrievedManifest));
    }

}
