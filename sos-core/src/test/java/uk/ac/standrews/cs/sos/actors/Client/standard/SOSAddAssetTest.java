package uk.ac.standrews.cs.sos.actors.Client.standard;

import org.skyscreamer.jsonassert.JSONAssert;
import org.testng.Assert;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.interfaces.locations.Location;
import uk.ac.standrews.cs.sos.interfaces.model.*;
import uk.ac.standrews.cs.sos.model.locations.URILocation;
import uk.ac.standrews.cs.sos.model.manifests.AssetManifest;
import uk.ac.standrews.cs.sos.model.manifests.CompoundType;
import uk.ac.standrews.cs.sos.model.manifests.Content;
import uk.ac.standrews.cs.sos.model.manifests.ManifestType;
import uk.ac.standrews.cs.sos.model.manifests.builders.AssetBuilder;
import uk.ac.standrews.cs.sos.model.manifests.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.utils.HelperTest;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSAddAssetTest extends AgentTest {

    @Test
    public void testAddAsset() throws Exception {
        Content cat = new Content("cat", GUIDFactory.recreateGUID("123"));
        Set<Content> contents = new LinkedHashSet<>();
        contents.add(cat);

        Compound compound = agent.addCompound(CompoundType.DATA, contents);

        AssetBuilder builder = new AssetBuilder(compound.guid());
        Asset manifest = agent.addAsset(builder);
        Assert.assertEquals(manifest.getType(), ManifestType.ASSET);

        Manifest retrievedManifest = agent.getManifest(manifest.getVersionGUID());
        assertEquals(retrievedManifest.getType(), ManifestType.ASSET);

        JSONAssert.assertEquals(manifest.toString(), retrievedManifest.toString(), false);
    }

    @Test
    public void testRetrieveAssetFromFileWithPrevsAndMeta() throws Exception {
        IGUID invariant = GUIDFactory.recreateGUID("1a2b3c");

        Content cat = new Content("cat", GUIDFactory.recreateGUID("123"));
        Set<Content> contents = new LinkedHashSet<>();
        contents.add(cat);

        Compound compound = agent.addCompound(CompoundType.DATA, contents);

        Set<IGUID> prevs = new LinkedHashSet<>();
        prevs.add(GUIDFactory.recreateGUID("321"));
        prevs.add(GUIDFactory.recreateGUID("abcef"));

        SOSMetadata metaMock = mock(SOSMetadata.class);
        when(metaMock.guid()).thenReturn(GUIDFactory.recreateGUID("897"));

        AssetBuilder builder = new AssetBuilder(compound.guid())
                .setInvariant(invariant)
                .setPrevious(prevs)
                .setMetadata(metaMock);
        Asset manifest = agent.addAsset(builder);
        assertEquals(manifest.getType(), ManifestType.ASSET);

        Manifest retrievedManifest = agent.getManifest(manifest.getVersionGUID());
        assertEquals(retrievedManifest.getType(), ManifestType.ASSET);

        IGUID retrievedInvariant = ((AssetManifest) retrievedManifest).getInvariantGUID();
        assertEquals(invariant, retrievedInvariant);

        IGUID retrievedMetadata = ((AssetManifest) retrievedManifest).getMetadata();
        assertEquals(retrievedMetadata, metaMock.guid());

        Set<IGUID> retrievedPrevs = ((AssetManifest) retrievedManifest).getPreviousVersions();
        assertTrue(retrievedPrevs.containsAll(prevs));

        JSONAssert.assertEquals(manifest.toString(), retrievedManifest.toString(), false);
    }

    @Test
    public void testAddAssetAndVerify() throws Exception {
        Content cat = new Content("cat", GUIDFactory.recreateGUID("123"));
        Set<Content> contents = new LinkedHashSet<>();
        contents.add(cat);

        Compound compound = agent.addCompound(CompoundType.DATA, contents);

        AssetBuilder builder = new AssetBuilder(compound.guid());
        Asset manifest = agent.addAsset(builder);
        Manifest retrievedManifest = agent.getManifest(manifest.getVersionGUID());

        assertTrue(agent.verifyManifest(localSOSNode.getIdentity(), retrievedManifest));
    }

    @Test
    public void testAddAssetWithAtom() throws Exception {
        Location location = HelperTest.createDummyDataFile(localStorage);
        AtomBuilder atomBuilder = new AtomBuilder().setLocation(location);
        Atom atom = agent.addAtom(atomBuilder);

        AssetBuilder builder = new AssetBuilder(atom.guid());
        Asset manifest = agent.addAsset(builder);
        Assert.assertEquals(manifest.getType(), ManifestType.ASSET);

        Manifest retrievedManifest = agent.getManifest(manifest.getVersionGUID());
        assertEquals(retrievedManifest.getType(), ManifestType.ASSET);

        JSONAssert.assertEquals(manifest.toString(), retrievedManifest.toString(), false);
    }


    @Test
    public void testAddAssetWithMetadata() throws Exception {
        Location location = HelperTest.createDummyDataFile(localStorage);
        AtomBuilder atomBuilder = new AtomBuilder().setLocation(location);
        Atom atom = agent.addAtom(atomBuilder);

        SOSMetadata metadata = agent.addMetadata(atom);

        AssetBuilder builder = new AssetBuilder(atom.guid())
                .setMetadata(metadata);
        Asset manifest = agent.addAsset(builder);
        Assert.assertEquals(manifest.getType(), ManifestType.ASSET);

        Manifest retrievedManifest = agent.getManifest(manifest.getVersionGUID());
        assertEquals(retrievedManifest.getType(), ManifestType.ASSET);

        JSONAssert.assertEquals(manifest.toString(), retrievedManifest.toString(), false);
    }

    @Test
    public void testAddAssetWithJPEGMetadata() throws Exception {
        Location location = new URILocation("http://www.eastcottvets.co.uk/uploads/Animals/gingerkitten.jpg");
        AtomBuilder atomBuilder = new AtomBuilder().setLocation(location);
        Atom atom = agent.addAtom(atomBuilder);

        SOSMetadata metadata = agent.addMetadata(atom);

        AssetBuilder builder = new AssetBuilder(atom.guid())
                .setMetadata(metadata);
        Asset manifest = agent.addAsset(builder);
        Assert.assertEquals(manifest.getType(), ManifestType.ASSET);

        Manifest retrievedManifest = agent.getManifest(manifest.getVersionGUID());
        assertEquals(retrievedManifest.getType(), ManifestType.ASSET);

        JSONAssert.assertEquals(manifest.toString(), retrievedManifest.toString(), false);
    }

    @Test
    public void testAddAssetAndGetJPEGMetadata() throws Exception {
        Location location = new URILocation("http://www.eastcottvets.co.uk/uploads/Animals/gingerkitten.jpg");
        AtomBuilder atomBuilder = new AtomBuilder().setLocation(location);
        Atom atom = agent.addAtom(atomBuilder);

        SOSMetadata metadata = agent.addMetadata(atom);

        AssetBuilder builder = new AssetBuilder(atom.guid())
                .setMetadata(metadata);
        Asset manifest = agent.addAsset(builder);
        Assert.assertEquals(manifest.getType(), ManifestType.ASSET);

        Manifest retrievedManifest = agent.getManifest(manifest.getVersionGUID());
        assertEquals(retrievedManifest.getType(), ManifestType.ASSET);

        Asset retrievedAsset = (Asset) retrievedManifest;
        IGUID retrievedMetadataGUID = retrievedAsset.getMetadata();
        assertEquals(metadata.guid(), retrievedMetadataGUID);

        SOSMetadata retrievedMetadata = agent.getMetadata(retrievedMetadataGUID);
        assertEquals(metadata.guid(), retrievedMetadata.guid());
    }

}
