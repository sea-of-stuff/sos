package uk.ac.standrews.cs.sos.model.implementations;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.model.implementations.components.manifests.AtomManifest;
import uk.ac.standrews.cs.sos.model.implementations.components.manifests.CompoundManifest;
import uk.ac.standrews.cs.sos.model.implementations.utils.Content;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUID;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUIDsha1;
import uk.ac.standrews.cs.sos.model.implementations.utils.Location;
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
        Collection<Location> locations = new ArrayList<Location>();
        Location location = Helper.createDummyDataFile(configuration);
        locations.add(location);
        AtomManifest manifest = model.addAtom(locations);

        Collection<Location> locationsOther = new ArrayList<Location>();
        Location locationOther = Helper.createDummyDataFile(configuration, "another-file");
        Helper.appendToFile(locationOther, "another random line");
        locationsOther.add(locationOther);
        AtomManifest manifestOther = model.addAtom(locationsOther);

        Collection<GUID> manifests = model.findManifestByType("Atom");
        assertEquals(manifests.size(), 2);
        assertTrue(manifests.contains(manifest.getContentGUID()));
        assertTrue(manifests.contains(manifestOther.getContentGUID()));
    }

    @Test
    public void testFindAtomsButNotCompounds() throws Exception {
        Collection<Location> locations = new ArrayList<Location>();
        Location location = Helper.createDummyDataFile(configuration);
        locations.add(location);
        AtomManifest manifest = model.addAtom(locations);

        Collection<Location> locationsOther = new ArrayList<Location>();
        Location locationOther = Helper.createDummyDataFile(configuration, "another-file");
        Helper.appendToFile(locationOther, "another random line");
        locationsOther.add(locationOther);
        AtomManifest manifestOther = model.addAtom(locationsOther);

        Content cat = new Content("cat", manifest.getContentGUID());
        Collection<Content> contents = new ArrayList<>();
        contents.add(cat);

        CompoundManifest compoundManifest = model.addCompound(contents);

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
        CompoundManifest compoundManifest = model.addCompound(contents);

        Content dog = new Content("dog", new GUIDsha1("343"));
        Collection<Content> otherContents = new ArrayList<>();
        otherContents.add(dog);
        CompoundManifest otherCompoundManifest = model.addCompound(otherContents);

        Collection<GUID> cats = model.findManifestByLabel("cat");
        assertEquals(cats.size(), 1);
        assertTrue(cats.contains(new GUIDsha1("123")));

        Collection<GUID> dogs = model.findManifestByLabel("dog");
        assertEquals(dogs.size(), 1);
        assertTrue(dogs.contains(new GUIDsha1("343")));
    }
}
