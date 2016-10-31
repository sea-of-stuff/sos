package uk.ac.standrews.cs.sos.SOSImpl.Client.standard;

import org.skyscreamer.jsonassert.JSONAssert;
import org.testng.Assert;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.interfaces.locations.Location;
import uk.ac.standrews.cs.sos.interfaces.manifests.Atom;
import uk.ac.standrews.cs.sos.interfaces.manifests.Compound;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;
import uk.ac.standrews.cs.sos.interfaces.manifests.Version;
import uk.ac.standrews.cs.sos.interfaces.metadata.SOSMetadata;
import uk.ac.standrews.cs.sos.model.locations.URILocation;
import uk.ac.standrews.cs.sos.model.manifests.CompoundType;
import uk.ac.standrews.cs.sos.model.manifests.Content;
import uk.ac.standrews.cs.sos.model.manifests.ManifestType;
import uk.ac.standrews.cs.sos.model.manifests.VersionManifest;
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
public class SOSAddVersionTest extends ClientTest {

    @Test
    public void testAddAsset() throws Exception {
        Content cat = new Content("cat", GUIDFactory.recreateGUID("123"));
        Collection<Content> contents = new ArrayList<>();
        contents.add(cat);

        Compound compound = client.addCompound(CompoundType.DATA, contents);

        VersionBuilder builder = new VersionBuilder(compound.getContentGUID());
        Version manifest = client.addVersion(builder);
        Assert.assertEquals(manifest.getManifestType(), ManifestType.VERSION);

        Manifest retrievedManifest = client.getManifest(manifest.getVersionGUID());
        assertEquals(retrievedManifest.getManifestType(), ManifestType.VERSION);

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

        Collection<SOSMetadata> metadata = new ArrayList<>();
        SOSMetadata metaMock = mock(SOSMetadata.class);
        when(metaMock.guid()).thenReturn(GUIDFactory.recreateGUID("897"));

        SOSMetadata metaMock1 = mock(SOSMetadata.class);
        when(metaMock1.guid()).thenReturn(GUIDFactory.recreateGUID("456"));

        VersionBuilder builder = new VersionBuilder(compound.getContentGUID())
                .setInvariant(invariant)
                .setPrevious(prevs)
                .setMetadata(metadata);
        Version manifest = client.addVersion(builder);
        assertEquals(manifest.getManifestType(), ManifestType.VERSION);

        Manifest retrievedManifest = client.getManifest(manifest.getVersionGUID());
        assertEquals(retrievedManifest.getManifestType(), ManifestType.VERSION);

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

    @Test
    public void testAddAssetWithAtom() throws Exception {
        Location location = HelperTest.createDummyDataFile(internalStorage);
        AtomBuilder atomBuilder = new AtomBuilder().setLocation(location);
        Atom atom = client.addAtom(atomBuilder);

        VersionBuilder builder = new VersionBuilder(atom.guid());
        Version manifest = client.addVersion(builder);
        Assert.assertEquals(manifest.getManifestType(), ManifestType.VERSION);

        Manifest retrievedManifest = client.getManifest(manifest.getVersionGUID());
        assertEquals(retrievedManifest.getManifestType(), ManifestType.VERSION);

        JSONAssert.assertEquals(manifest.toString(), retrievedManifest.toString(), false);
    }


    @Test
    public void testAddAssetWithMetadata() throws Exception {
        Location location = HelperTest.createDummyDataFile(internalStorage);
        AtomBuilder atomBuilder = new AtomBuilder().setLocation(location);
        Atom atom = client.addAtom(atomBuilder);

        SOSMetadata metadata = client.addMetadata(atom);

        VersionBuilder builder = new VersionBuilder(atom.guid())
                .setMetadata(metadata);
        Version manifest = client.addVersion(builder);
        Assert.assertEquals(manifest.getManifestType(), ManifestType.VERSION);

        Manifest retrievedManifest = client.getManifest(manifest.getVersionGUID());
        assertEquals(retrievedManifest.getManifestType(), ManifestType.VERSION);

        JSONAssert.assertEquals(manifest.toString(), retrievedManifest.toString(), false);
    }

    @Test
    public void testAddAssetWithJPEGMetadata() throws Exception {
        Location location = new URILocation("http://www.eastcottvets.co.uk/uploads/Animals/gingerkitten.jpg");
        AtomBuilder atomBuilder = new AtomBuilder().setLocation(location);
        Atom atom = client.addAtom(atomBuilder);

        SOSMetadata metadata = client.addMetadata(atom);

        VersionBuilder builder = new VersionBuilder(atom.guid())
                .setMetadata(metadata);
        Version manifest = client.addVersion(builder);
        Assert.assertEquals(manifest.getManifestType(), ManifestType.VERSION);

        Manifest retrievedManifest = client.getManifest(manifest.getVersionGUID());
        assertEquals(retrievedManifest.getManifestType(), ManifestType.VERSION);

        JSONAssert.assertEquals(manifest.toString(), retrievedManifest.toString(), false);
    }

    @Test
    public void testAddAssetAndGetJPEGMetadata() throws Exception {
        Location location = new URILocation("http://www.eastcottvets.co.uk/uploads/Animals/gingerkitten.jpg");
        AtomBuilder atomBuilder = new AtomBuilder().setLocation(location);
        Atom atom = client.addAtom(atomBuilder);

        SOSMetadata metadata = client.addMetadata(atom);

        VersionBuilder builder = new VersionBuilder(atom.guid())
                .setMetadata(metadata);
        Version manifest = client.addVersion(builder);
        Assert.assertEquals(manifest.getManifestType(), ManifestType.VERSION);

        Manifest retrievedManifest = client.getManifest(manifest.getVersionGUID());
        assertEquals(retrievedManifest.getManifestType(), ManifestType.VERSION);

        Version retrievedVersion = (Version) retrievedManifest;
        IGUID retrievedMetadataGUID = (IGUID) retrievedVersion.getMetadata().toArray()[0];
        assertEquals(metadata.guid(), retrievedMetadataGUID);

        SOSMetadata retrievedMetadata = client.getMetadata(retrievedMetadataGUID);
        assertEquals(metadata.guid(), retrievedMetadata.guid());
    }

}
