package uk.ac.standrews.cs.sos.model;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.interfaces.locations.Location;
import uk.ac.standrews.cs.sos.interfaces.manifests.Asset;
import uk.ac.standrews.cs.sos.interfaces.manifests.Atom;
import uk.ac.standrews.cs.sos.interfaces.manifests.Compound;
import uk.ac.standrews.cs.sos.model.manifests.CompoundType;
import uk.ac.standrews.cs.sos.model.manifests.Content;
import uk.ac.standrews.cs.utils.GUID;
import uk.ac.standrews.cs.utils.GUIDsha1;
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
        Atom manifest = model.addAtom(location);

        Location otherLocation = Helper.createDummyDataFile(configuration, "another-file");
        Helper.appendToFile(otherLocation, "another random line");
        Atom manifestOther = model.addAtom(otherLocation);

        Collection<GUID> manifests = model.findManifestByType("Atom");
        assertEquals(manifests.size(), 2);
        assertTrue(manifests.contains(manifest.getContentGUID()));
        assertTrue(manifests.contains(manifestOther.getContentGUID()));
    }

    @Test
    public void testFindAtomsButNotCompounds() throws Exception {
        Location location = Helper.createDummyDataFile(configuration);
        Atom manifest = model.addAtom(location);

        Location otherLocation = Helper.createDummyDataFile(configuration, "another-file");
        Helper.appendToFile(otherLocation, "another random line");
        Atom manifestOther = model.addAtom(otherLocation);

        Content cat = new Content("cat", manifest.getContentGUID());
        Collection<Content> contents = new ArrayList<>();
        contents.add(cat);

        model.addCompound(CompoundType.DATA, contents);

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
        model.addCompound(CompoundType.DATA, contents);

        Content dog = new Content("dog", new GUIDsha1("343"));
        Collection<Content> otherContents = new ArrayList<>();
        otherContents.add(dog);
        model.addCompound(CompoundType.DATA, otherContents);

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

        Compound compound = model.addCompound(CompoundType.DATA, contents);
        Asset manifest = model.addAsset(compound.getContentGUID(), null, null, null);

        Content feline = new Content("feline", new GUIDsha1("456"));
        Collection<Content> newContents = new ArrayList<>();
        newContents.add(feline);

        Compound newCompound = model.addCompound(CompoundType.DATA, newContents);
        Collection<GUID> prevs = new ArrayList<>();
        prevs.add(manifest.getVersionGUID());
        Asset newManifest = model.addAsset(newCompound.getContentGUID(), manifest.getInvariantGUID(), prevs, null);

        Collection<GUID> versions = model.findVersions(manifest.getInvariantGUID());
        assertEquals(versions.size(), 2);
        assertTrue(versions.contains(manifest.getVersionGUID()));
        assertTrue(versions.contains(newManifest.getVersionGUID()));
    }

}
