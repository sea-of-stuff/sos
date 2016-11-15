package uk.ac.standrews.cs.sos.SOSImpl.Client.standard;

import org.skyscreamer.jsonassert.JSONAssert;
import org.testng.Assert;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.interfaces.locations.Location;
import uk.ac.standrews.cs.sos.interfaces.manifests.Asset;
import uk.ac.standrews.cs.sos.interfaces.manifests.Atom;
import uk.ac.standrews.cs.sos.interfaces.manifests.Compound;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;
import uk.ac.standrews.cs.sos.interfaces.metadata.SOSMetadata;
import uk.ac.standrews.cs.sos.model.locations.URILocation;
import uk.ac.standrews.cs.sos.model.manifests.AssetManifest;
import uk.ac.standrews.cs.sos.model.manifests.CompoundType;
import uk.ac.standrews.cs.sos.model.manifests.Content;
import uk.ac.standrews.cs.sos.model.manifests.ManifestType;
import uk.ac.standrews.cs.sos.model.manifests.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.model.manifests.builders.VersionBuilder;
import uk.ac.standrews.cs.sos.utils.HelperTest;

import java.util.ArrayList;
import java.util.Collection;

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
        Collection<Content> contents = new ArrayList<>();
        contents.add(cat);

        Compound compound = agent.addCompound(CompoundType.DATA, contents);

        VersionBuilder builder = new VersionBuilder(compound.getContentGUID());
        Asset manifest = agent.addVersion(builder);
        Assert.assertEquals(manifest.getManifestType(), ManifestType.ASSET);

        Manifest retrievedManifest = agent.getManifest(manifest.getVersionGUID());
        assertEquals(retrievedManifest.getManifestType(), ManifestType.ASSET);

        JSONAssert.assertEquals(manifest.toString(), retrievedManifest.toString(), false);
    }

    @Test
    public void testRetrieveAssetFromFileWithPrevsAndMeta() throws Exception {
        IGUID invariant = GUIDFactory.recreateGUID("1a2b3c");

        Content cat = new Content("cat", GUIDFactory.recreateGUID("123"));
        Collection<Content> contents = new ArrayList<>();
        contents.add(cat);

        Compound compound = agent.addCompound(CompoundType.DATA, contents);

        Collection<IGUID> prevs = new ArrayList<>();
        prevs.add(GUIDFactory.recreateGUID("321"));
        prevs.add(GUIDFactory.recreateGUID("abcef"));

        Collection<SOSMetadata> metadata = new ArrayList<>();
        SOSMetadata metaMock = mock(SOSMetadata.class);
        when(metaMock.guid()).thenReturn(GUIDFactory.recreateGUID("897"));

        SOSMetadata metaMock1 = mock(SOSMetadata.class);
        when(metaMock1.guid()).thenReturn(GUIDFactory.recreateGUID("456"));

        VersionBuilder builder = new VersionBuilder(compound.getContentGUID())
                .setInvariant(invariant)
                .setPrevious(prevs)
                .setMetadata(metadata);
        Asset manifest = agent.addVersion(builder);
        assertEquals(manifest.getManifestType(), ManifestType.ASSET);

        Manifest retrievedManifest = agent.getManifest(manifest.getVersionGUID());
        assertEquals(retrievedManifest.getManifestType(), ManifestType.ASSET);

        IGUID retrievedInvariant = ((AssetManifest) retrievedManifest).getInvariantGUID();
        assertEquals(invariant, retrievedInvariant);

        Collection<IGUID> retrievedMetadata = ((AssetManifest) retrievedManifest).getMetadata();
        assertTrue(retrievedMetadata.containsAll(metadata));

        Collection<IGUID> retrievedPrevs = ((AssetManifest) retrievedManifest).getPreviousVersions();
        assertTrue(retrievedPrevs.containsAll(prevs));

        JSONAssert.assertEquals(manifest.toString(), retrievedManifest.toString(), false);
    }

    @Test
    public void testAddAssetAndVerify() throws Exception {
        Content cat = new Content("cat", GUIDFactory.recreateGUID("123"));
        Collection<Content> contents = new ArrayList<>();
        contents.add(cat);

        Compound compound = agent.addCompound(CompoundType.DATA, contents);

        VersionBuilder builder = new VersionBuilder(compound.getContentGUID());
        Asset manifest = agent.addVersion(builder);
        Manifest retrievedManifest = agent.getManifest(manifest.getVersionGUID());

        assertTrue(agent.verifyManifest(localSOSNode.getIdentity(), retrievedManifest));
    }

    @Test
    public void testAddAssetWithAtom() throws Exception {
        Location location = HelperTest.createDummyDataFile(localStorage);
        AtomBuilder atomBuilder = new AtomBuilder().setLocation(location);
        Atom atom = agent.addAtom(atomBuilder);

        VersionBuilder builder = new VersionBuilder(atom.guid());
        Asset manifest = agent.addVersion(builder);
        Assert.assertEquals(manifest.getManifestType(), ManifestType.ASSET);

        Manifest retrievedManifest = agent.getManifest(manifest.getVersionGUID());
        assertEquals(retrievedManifest.getManifestType(), ManifestType.ASSET);

        JSONAssert.assertEquals(manifest.toString(), retrievedManifest.toString(), false);
    }


    @Test
    public void testAddAssetWithMetadata() throws Exception {
        Location location = HelperTest.createDummyDataFile(localStorage);
        AtomBuilder atomBuilder = new AtomBuilder().setLocation(location);
        Atom atom = agent.addAtom(atomBuilder);

        SOSMetadata metadata = agent.addMetadata(atom);

        VersionBuilder builder = new VersionBuilder(atom.guid())
                .setMetadata(metadata);
        Asset manifest = agent.addVersion(builder);
        Assert.assertEquals(manifest.getManifestType(), ManifestType.ASSET);

        Manifest retrievedManifest = agent.getManifest(manifest.getVersionGUID());
        assertEquals(retrievedManifest.getManifestType(), ManifestType.ASSET);

        JSONAssert.assertEquals(manifest.toString(), retrievedManifest.toString(), false);
    }

    @Test
    public void testAddAssetWithJPEGMetadata() throws Exception {
        Location location = new URILocation("http://www.eastcottvets.co.uk/uploads/Animals/gingerkitten.jpg");
        AtomBuilder atomBuilder = new AtomBuilder().setLocation(location);
        Atom atom = agent.addAtom(atomBuilder);

        SOSMetadata metadata = agent.addMetadata(atom);

        VersionBuilder builder = new VersionBuilder(atom.guid())
                .setMetadata(metadata);
        Asset manifest = agent.addVersion(builder);
        Assert.assertEquals(manifest.getManifestType(), ManifestType.ASSET);

        Manifest retrievedManifest = agent.getManifest(manifest.getVersionGUID());
        assertEquals(retrievedManifest.getManifestType(), ManifestType.ASSET);

        JSONAssert.assertEquals(manifest.toString(), retrievedManifest.toString(), false);
    }

    @Test
    public void testAddAssetAndGetJPEGMetadata() throws Exception {
        Location location = new URILocation("http://www.eastcottvets.co.uk/uploads/Animals/gingerkitten.jpg");
        AtomBuilder atomBuilder = new AtomBuilder().setLocation(location);
        Atom atom = agent.addAtom(atomBuilder);

        SOSMetadata metadata = agent.addMetadata(atom);

        VersionBuilder builder = new VersionBuilder(atom.guid())
                .setMetadata(metadata);
        Asset manifest = agent.addVersion(builder);
        Assert.assertEquals(manifest.getManifestType(), ManifestType.ASSET);

        Manifest retrievedManifest = agent.getManifest(manifest.getVersionGUID());
        assertEquals(retrievedManifest.getManifestType(), ManifestType.ASSET);

        Asset retrievedAsset = (Asset) retrievedManifest;
        IGUID retrievedMetadataGUID = (IGUID) retrievedAsset.getMetadata().toArray()[0];
        assertEquals(metadata.guid(), retrievedMetadataGUID);

        SOSMetadata retrievedMetadata = agent.getMetadata(retrievedMetadataGUID);
        assertEquals(metadata.guid(), retrievedMetadata.guid());
    }

}
