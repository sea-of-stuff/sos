package uk.ac.standrews.cs.sos.impl.actors.Client.standard;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSFindTest extends AgentTest {

//    @Test
//    public void testFindAtoms() throws Exception {
//        Location location = HelperTest.createDummyDataFile(internalStorage);
//        AtomBuilder builder = new AtomBuilder().setLocation(location);
//        Atom manifest = client.addAtom(builder);
//
//        Location otherLocation = HelperTest.createDummyDataFile(internalStorage, "another-file");
//        HelperTest.appendToFile(otherLocation, "another random line");
//        AtomBuilder otherBuilder = new AtomBuilder().setLocation(otherLocation);
//        Atom manifestOther = client.addAtom(otherBuilder);
//
//        Set<IGUID> manifests = client.findManifestByType("Atom");
//        assertEquals(manifests.size(), 2);
//        assertTrue(manifests.contains(manifest.getContentGUID()));
//        assertTrue(manifests.contains(manifestOther.getContentGUID()));
//    }
//
//    @Test
//    public void testFindAtomsButNotCompounds() throws Exception {
//        Location location = HelperTest.createDummyDataFile(internalStorage);
//        AtomBuilder builder = new AtomBuilder().setLocation(location);
//        Atom manifest = client.addAtom(builder);
//
//        Location otherLocation = HelperTest.createDummyDataFile(internalStorage, "another-file");
//        HelperTest.appendToFile(otherLocation, "another random line");
//        AtomBuilder otherBuilder = new AtomBuilder().setLocation(otherLocation);
//        Atom manifestOther = client.addAtom(otherBuilder);
//
//        Content cat = new Content("cat", manifest.getContentGUID());
//        Set<Content> contents = new ArrayList<>();
//        contents.add(cat);
//
//        client.addCompound(CompoundType.DATA, contents);
//
//        Set<IGUID> manifests = client.findManifestByType("Atom");
//        assertEquals(manifests.size(), 2);
//        assertTrue(manifests.contains(manifest.getContentGUID()));
//        assertTrue(manifests.contains(manifestOther.getContentGUID()));
//    }
//
//    @Test
//    public void testFindContentsByLabel() throws Exception {
//        Content cat = new Content("cat", GUIDFactory.recreateGUID("123"));
//        Set<Content> contents = new ArrayList<>();
//        contents.add(cat);
//        client.addCompound(CompoundType.DATA, contents);
//
//        Content dog = new Content("dog", GUIDFactory.recreateGUID("343"));
//        Set<Content> otherContents = new ArrayList<>();
//        otherContents.add(dog);
//        client.addCompound(CompoundType.DATA, otherContents);
//
//        Set<IGUID> cats = client.findManifestByLabel("cat");
//        assertEquals(cats.size(), 1);
//        assertTrue(cats.contains(GUIDFactory.recreateGUID("123")));
//
//        Set<IGUID> dogs = client.findManifestByLabel("dog");
//        assertEquals(dogs.size(), 1);
//        assertTrue(dogs.contains(GUIDFactory.recreateGUID("343")));
//    }
//
//    @Test
//    public void testFindVersions() throws Exception {
//        Content cat = new Content("cat", GUIDFactory.recreateGUID("123"));
//        Set<Content> contents = new ArrayList<>();
//        contents.add(cat);
//
//        Compound compound = client.addCompound(CompoundType.DATA, contents);
//
//        VersionBuilder builder = new VersionBuilder(compound.getContentGUID());
//        Version manifest = client.addVersion(builder);
//
//        Content feline = new Content("feline", GUIDFactory.recreateGUID("456"));
//        Set<Content> newContents = new ArrayList<>();
//        newContents.add(feline);
//
//        Compound newCompound = client.addCompound(CompoundType.DATA, newContents);
//        Set<IGUID> prevs = new ArrayList<>();
//        prevs.add(manifest.getVersionGUID());
//
//        VersionBuilder newBuilder = new VersionBuilder(newCompound.getContentGUID())
//                .setInvariant(manifest.getInvariantGUID())
//                .setPrevious(prevs);
//        Version newManifest = client.addVersion(newBuilder);
//
//        Set<IGUID> versions = client.findVersions(manifest.getInvariantGUID());
//        assertEquals(versions.size(), 2);
//        assertTrue(versions.contains(manifest.getVersionGUID()));
//        assertTrue(versions.contains(newManifest.getVersionGUID()));
//    }

}
