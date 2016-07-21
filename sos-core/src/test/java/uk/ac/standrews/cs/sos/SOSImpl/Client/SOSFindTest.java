package uk.ac.standrews.cs.sos.SOSImpl.Client;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.interfaces.locations.Location;
import uk.ac.standrews.cs.sos.interfaces.manifests.Atom;
import uk.ac.standrews.cs.sos.interfaces.manifests.Compound;
import uk.ac.standrews.cs.sos.interfaces.manifests.Version;
import uk.ac.standrews.cs.sos.model.manifests.CompoundType;
import uk.ac.standrews.cs.sos.model.manifests.Content;
import uk.ac.standrews.cs.sos.model.manifests.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.model.manifests.builders.VersionBuilder;
import uk.ac.standrews.cs.sos.utils.HelperTest;

import java.util.ArrayList;
import java.util.Collection;

import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSFindTest extends ClientTest {

    @Test
    public void testFindAtoms() throws Exception {
        Location location = HelperTest.createDummyDataFile(internalStorage);
        AtomBuilder builder = new AtomBuilder().setLocation(location);
        Atom manifest = client.addAtom(builder);

        Location otherLocation = HelperTest.createDummyDataFile(internalStorage, "another-file");
        HelperTest.appendToFile(otherLocation, "another random line");
        AtomBuilder otherBuilder = new AtomBuilder().setLocation(otherLocation);
        Atom manifestOther = client.addAtom(otherBuilder);

        Collection<IGUID> manifests = client.findManifestByType("Atom");
        assertEquals(manifests.size(), 2);
        assertTrue(manifests.contains(manifest.getContentGUID()));
        assertTrue(manifests.contains(manifestOther.getContentGUID()));
    }

    @Test
    public void testFindAtomsButNotCompounds() throws Exception {
        Location location = HelperTest.createDummyDataFile(internalStorage);
        AtomBuilder builder = new AtomBuilder().setLocation(location);
        Atom manifest = client.addAtom(builder);

        Location otherLocation = HelperTest.createDummyDataFile(internalStorage, "another-file");
        HelperTest.appendToFile(otherLocation, "another random line");
        AtomBuilder otherBuilder = new AtomBuilder().setLocation(otherLocation);
        Atom manifestOther = client.addAtom(otherBuilder);

        Content cat = new Content("cat", manifest.getContentGUID());
        Collection<Content> contents = new ArrayList<>();
        contents.add(cat);

        client.addCompound(CompoundType.DATA, contents);

        Collection<IGUID> manifests = client.findManifestByType("Atom");
        assertEquals(manifests.size(), 2);
        assertTrue(manifests.contains(manifest.getContentGUID()));
        assertTrue(manifests.contains(manifestOther.getContentGUID()));
    }

    @Test
    public void testFindContentsByLabel() throws Exception {
        Content cat = new Content("cat", GUIDFactory.recreateGUID("123"));
        Collection<Content> contents = new ArrayList<>();
        contents.add(cat);
        client.addCompound(CompoundType.DATA, contents);

        Content dog = new Content("dog", GUIDFactory.recreateGUID("343"));
        Collection<Content> otherContents = new ArrayList<>();
        otherContents.add(dog);
        client.addCompound(CompoundType.DATA, otherContents);

        Collection<IGUID> cats = client.findManifestByLabel("cat");
        assertEquals(cats.size(), 1);
        assertTrue(cats.contains(GUIDFactory.recreateGUID("123")));

        Collection<IGUID> dogs = client.findManifestByLabel("dog");
        assertEquals(dogs.size(), 1);
        assertTrue(dogs.contains(GUIDFactory.recreateGUID("343")));
    }

    @Test
    public void testFindVersions() throws Exception {
        Content cat = new Content("cat", GUIDFactory.recreateGUID("123"));
        Collection<Content> contents = new ArrayList<>();
        contents.add(cat);

        Compound compound = client.addCompound(CompoundType.DATA, contents);

        VersionBuilder builder = new VersionBuilder(compound.getContentGUID());
        Version manifest = client.addVersion(builder);

        Content feline = new Content("feline", GUIDFactory.recreateGUID("456"));
        Collection<Content> newContents = new ArrayList<>();
        newContents.add(feline);

        Compound newCompound = client.addCompound(CompoundType.DATA, newContents);
        Collection<IGUID> prevs = new ArrayList<>();
        prevs.add(manifest.getVersionGUID());

        VersionBuilder newBuilder = new VersionBuilder(newCompound.getContentGUID())
                .setInvariant(manifest.getInvariantGUID())
                .setPrevious(prevs);
        Version newManifest = client.addVersion(newBuilder);

        Collection<IGUID> versions = client.findVersions(manifest.getInvariantGUID());
        assertEquals(versions.size(), 2);
        assertTrue(versions.contains(manifest.getVersionGUID()));
        assertTrue(versions.contains(newManifest.getVersionGUID()));
    }

}
