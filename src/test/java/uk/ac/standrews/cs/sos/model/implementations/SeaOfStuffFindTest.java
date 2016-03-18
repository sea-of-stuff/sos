package uk.ac.standrews.cs.sos.model.implementations;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.model.implementations.locations.Location;
import uk.ac.standrews.cs.sos.model.implementations.manifests.AssetManifest;
import uk.ac.standrews.cs.sos.model.implementations.manifests.AtomManifest;
import uk.ac.standrews.cs.sos.model.implementations.manifests.CompoundManifest;
import uk.ac.standrews.cs.sos.model.implementations.utils.Content;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUID;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUIDsha1;
import uk.ac.standrews.cs.utils.Helper;

import java.util.ArrayList;
import java.util.Collection;

import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SeaOfStuffFindTest extends SeaOfStuffGeneralTest {

    @Test
    public void testFindAtoms() throws Exception {
        Location location = Helper.createDummyDataFile(configuration);
        AtomManifest manifest = model.addAtom(location);

        Location otherLocation = Helper.createDummyDataFile(configuration, "another-file");
        Helper.appendToFile(otherLocation, "another random line");
        AtomManifest manifestOther = model.addAtom(otherLocation);

        Collection<GUID> manifests = model.findManifestByType("Atom");
        assertEquals(manifests.size(), 2);
        assertTrue(manifests.contains(manifest.getContentGUID()));
        assertTrue(manifests.contains(manifestOther.getContentGUID()));
    }

    @Test
    public void testFindAtomsButNotCompounds() throws Exception {
        Location location = Helper.createDummyDataFile(configuration);
        AtomManifest manifest = model.addAtom(location);

        Location otherLocation = Helper.createDummyDataFile(configuration, "another-file");
        Helper.appendToFile(otherLocation, "another random line");
        AtomManifest manifestOther = model.addAtom(otherLocation);

        Content cat = new Content("cat", manifest.getContentGUID());
        Collection<Content> contents = new ArrayList<>();
        contents.add(cat);

        model.addCompound(contents);

        Collection<GUID> manifests = model.findManifestByType("Atom");
        assertEquals(manifests.size(), 2);
        assertTrue(manifests.contains(manifest.getContentGUID()));
        assertTrue(manifests.contains(manifestOther.getContentGUID()));
    }

    @Test
    public void testFindContentsByLabel() throws Exception {
        Content cat = new Content("cat", new GUIDsha1("123"));
        Collection<Content> contents = new ArrayList<>();
        contents.add(cat);
        model.addCompound(contents);

        Content dog = new Content("dog", new GUIDsha1("343"));
        Collection<Content> otherContents = new ArrayList<>();
        otherContents.add(dog);
        model.addCompound(otherContents);

        Collection<GUID> cats = model.findManifestByLabel("cat");
        assertEquals(cats.size(), 1);
        assertTrue(cats.contains(new GUIDsha1("123")));

        Collection<GUID> dogs = model.findManifestByLabel("dog");
        assertEquals(dogs.size(), 1);
        assertTrue(dogs.contains(new GUIDsha1("343")));
    }

    @Test
    public void testFindVersions() throws Exception {
        Content cat = new Content("cat", new GUIDsha1("123"));
        Collection<Content> contents = new ArrayList<>();
        contents.add(cat);

        CompoundManifest compound = model.addCompound(contents);
        AssetManifest manifest = model.addAsset(compound.getContentGUID(), null, null, null);

        Content feline = new Content("feline", new GUIDsha1("456"));
        Collection<Content> newContents = new ArrayList<>();
        newContents.add(feline);

        CompoundManifest newCompound = model.addCompound(newContents);
        Collection<GUID> prevs = new ArrayList<>();
        prevs.add(manifest.getVersionGUID());
        AssetManifest newManifest = model.addAsset(newCompound.getContentGUID(), manifest.getInvariantGUID(), prevs, null);

        Collection<GUID> versions = model.findVersions(manifest.getInvariantGUID());
        assertEquals(versions.size(), 2);
        assertTrue(versions.contains(manifest.getVersionGUID()));
        assertTrue(versions.contains(newManifest.getVersionGUID()));
    }

}
