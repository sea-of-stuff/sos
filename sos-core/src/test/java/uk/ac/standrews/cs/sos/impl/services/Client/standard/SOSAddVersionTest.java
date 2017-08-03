package uk.ac.standrews.cs.sos.impl.services.Client.standard;

import org.skyscreamer.jsonassert.JSONAssert;
import org.testng.Assert;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.guid.ALGORITHM;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.impl.locations.URILocation;
import uk.ac.standrews.cs.sos.impl.manifests.ContentImpl;
import uk.ac.standrews.cs.sos.impl.manifests.VersionManifest;
import uk.ac.standrews.cs.sos.impl.manifests.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.impl.manifests.builders.CompoundBuilder;
import uk.ac.standrews.cs.sos.impl.manifests.builders.VersionBuilder;
import uk.ac.standrews.cs.sos.model.*;
import uk.ac.standrews.cs.sos.utils.HelperTest;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertFalse;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSAddVersionTest extends AgentTest {

    @Test
    public void testAddVersion() throws Exception {
        Content cat = new ContentImpl("cat", GUIDFactory.generateRandomGUID(ALGORITHM.SHA256));
        Set<Content> contents = new LinkedHashSet<>();
        contents.add(cat);

        CompoundBuilder compoundBuilder = new CompoundBuilder()
                .setType(CompoundType.DATA)
                .setContents(contents);
        Compound compound = agent.addCompound(compoundBuilder);

        VersionBuilder builder = new VersionBuilder(compound.guid());
        Version manifest = agent.addVersion(builder);
        Assert.assertEquals(manifest.getType(), ManifestType.VERSION);

        Manifest retrievedManifest = agent.getManifest(manifest.getVersionGUID());
        assertEquals(retrievedManifest.getType(), ManifestType.VERSION);

        JSONAssert.assertEquals(manifest.toString(), retrievedManifest.toString(), false);
    }

    @Test
    public void testRetrieveVersionFromFileWithPrevsAndMeta() throws Exception {
        IGUID invariant = GUIDFactory.generateRandomGUID(ALGORITHM.SHA256);

        Content cat = new ContentImpl("cat", GUIDFactory.generateRandomGUID(ALGORITHM.SHA256));
        Set<Content> contents = new LinkedHashSet<>();
        contents.add(cat);

        CompoundBuilder compoundBuilder = new CompoundBuilder()
                .setType(CompoundType.DATA)
                .setContents(contents);
        Compound compound = agent.addCompound(compoundBuilder);

        Set<IGUID> prevs = new LinkedHashSet<>();
        prevs.add(GUIDFactory.generateRandomGUID(ALGORITHM.SHA256));
        prevs.add(GUIDFactory.generateRandomGUID(ALGORITHM.SHA256));

        Metadata metaMock = mock(Metadata.class);
        when(metaMock.guid()).thenReturn(GUIDFactory.generateRandomGUID(ALGORITHM.SHA256));

        VersionBuilder builder = new VersionBuilder(compound.guid())
                .setInvariant(invariant)
                .setPrevious(prevs)
                .setMetadata(metaMock);
        Version manifest = agent.addVersion(builder);
        assertEquals(manifest.getType(), ManifestType.VERSION);

        Manifest retrievedManifest = agent.getManifest(manifest.getVersionGUID());
        assertEquals(retrievedManifest.getType(), ManifestType.VERSION);

        IGUID retrievedInvariant = ((VersionManifest) retrievedManifest).getInvariantGUID();
        assertEquals(invariant, retrievedInvariant);

        IGUID retrievedMetadata = ((VersionManifest) retrievedManifest).getMetadata();
        assertEquals(retrievedMetadata, metaMock.guid());

        Set<IGUID> retrievedPrevs = ((VersionManifest) retrievedManifest).getPreviousVersions();
        assertTrue(retrievedPrevs.containsAll(prevs));

        JSONAssert.assertEquals(manifest.toString(), retrievedManifest.toString(), false);
    }

    /**
     * Verification fails as Role has not private key to generate the signature in the first place
     * @throws Exception
     */
    @Test
    public void testAddVersionAndVerifyFails() throws Exception {
        Content cat = new ContentImpl("cat", GUIDFactory.generateRandomGUID(ALGORITHM.SHA256));
        Set<Content> contents = new LinkedHashSet<>();
        contents.add(cat);

        CompoundBuilder compoundBuilder = new CompoundBuilder()
                .setType(CompoundType.DATA)
                .setContents(contents);
        Compound compound = agent.addCompound(compoundBuilder);

        VersionBuilder builder = new VersionBuilder(compound.guid());
        Version manifest = agent.addVersion(builder);
        Manifest retrievedManifest = agent.getManifest(manifest.getVersionGUID());

        assertFalse(agent.verifyManifestSignature(localSOSNode.getRMS().activeRole(), retrievedManifest));
    }

    @Test
    public void testAddVersionWithAtom() throws Exception {
        Location location = HelperTest.createDummyDataFile(localStorage);
        AtomBuilder atomBuilder = new AtomBuilder().setLocation(location);
        Atom atom = agent.addAtom(atomBuilder);

        VersionBuilder builder = new VersionBuilder(atom.guid());
        Version manifest = agent.addVersion(builder);
        Assert.assertEquals(manifest.getType(), ManifestType.VERSION);

        Manifest retrievedManifest = agent.getManifest(manifest.getVersionGUID());
        assertEquals(retrievedManifest.getType(), ManifestType.VERSION);

        JSONAssert.assertEquals(manifest.toString(), retrievedManifest.toString(), false);
    }


    @Test
    public void testAddVersionWithMetadata() throws Exception {
        Location location = HelperTest.createDummyDataFile(localStorage);
        AtomBuilder atomBuilder = new AtomBuilder().setLocation(location);
        Atom atom = agent.addAtom(atomBuilder);

        Metadata metadata = agent.addMetadata(atom.getData());

        VersionBuilder builder = new VersionBuilder(atom.guid())
                .setMetadata(metadata);
        Version manifest = agent.addVersion(builder);
        Assert.assertEquals(manifest.getType(), ManifestType.VERSION);

        Manifest retrievedManifest = agent.getManifest(manifest.getVersionGUID());
        assertEquals(retrievedManifest.getType(), ManifestType.VERSION);

        JSONAssert.assertEquals(manifest.toString(), retrievedManifest.toString(), false);
    }

    @Test
    public void testAddVersionWithJPEGMetadata() throws Exception {
        Location location = new URILocation("http://www.eastcottvets.co.uk/uploads/Animals/gingerkitten.jpg");
        AtomBuilder atomBuilder = new AtomBuilder().setLocation(location);
        Atom atom = agent.addAtom(atomBuilder);

        Metadata metadata = agent.addMetadata(atom.getData());

        VersionBuilder builder = new VersionBuilder(atom.guid())
                .setMetadata(metadata);
        Version manifest = agent.addVersion(builder);
        Assert.assertEquals(manifest.getType(), ManifestType.VERSION);

        Manifest retrievedManifest = agent.getManifest(manifest.getVersionGUID());
        assertEquals(retrievedManifest.getType(), ManifestType.VERSION);

        JSONAssert.assertEquals(manifest.toString(), retrievedManifest.toString(), false);
    }

    @Test
    public void testAddVersionAndGetJPEGMetadata() throws Exception {
        Location location = new URILocation("http://www.eastcottvets.co.uk/uploads/Animals/gingerkitten.jpg");
        AtomBuilder atomBuilder = new AtomBuilder().setLocation(location);
        Atom atom = agent.addAtom(atomBuilder);

        Metadata metadata = agent.addMetadata(atom.getData());

        VersionBuilder builder = new VersionBuilder(atom.guid())
                .setMetadata(metadata);
        Version manifest = agent.addVersion(builder);
        Assert.assertEquals(manifest.getType(), ManifestType.VERSION);

        Manifest retrievedManifest = agent.getManifest(manifest.getVersionGUID());
        assertEquals(retrievedManifest.getType(), ManifestType.VERSION);

        Version retrievedVersion = (Version) retrievedManifest;
        IGUID retrievedMetadataGUID = retrievedVersion.getMetadata();
        assertEquals(metadata.guid(), retrievedMetadataGUID);

        Metadata retrievedMetadata = agent.getMetadata(retrievedMetadataGUID);
        assertEquals(metadata.guid(), retrievedMetadata.guid());
    }

}
