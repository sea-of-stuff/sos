package uk.ac.standrews.cs.sos.model.implementations;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SeaOfStuffAddAtomTest extends SeaOfStuffGeneralTest {

    /*
    @Test
    public void testAddAtom() throws Exception {
        Collection<OldLocation> locations = new ArrayList<OldLocation>();
        OldLocation location = Helper.createDummyDataFile(configuration);
        locations.add(location);
        AtomManifest manifest = model.addAtom(locations);
        assertEquals(manifest.getManifestType(), ManifestConstants.ATOM);

        Manifest retrievedManifest = model.getManifest(manifest.getContentGUID());
        assertEquals(ManifestConstants.ATOM, retrievedManifest.getManifestType());
        Collection<OldLocation> retrievedLocations = ((AtomManifest) retrievedManifest).getLocations();
        assertEquals(retrievedLocations.size(), 1);

        JSONAssert.assertEquals(manifest.toJSON().toString(), retrievedManifest.toJSON().toString(), true);
    }

    @Test
    public void testRetrieveAtomFromFile() throws Exception {
        Collection<OldLocation> locations = new ArrayList<OldLocation>();
        OldLocation location = Helper.createDummyDataFile(configuration);
        locations.add(location);
        AtomManifest manifest = model.addAtom(locations);
        assertEquals(manifest.getManifestType(), ManifestConstants.ATOM);

        // Flush the storage, so to force the manifest to be retrieved from file.
        index.flushDB();

        Manifest retrievedManifest = model.getManifest(manifest.getContentGUID());
        assertEquals(ManifestConstants.ATOM, retrievedManifest.getManifestType());
        Collection<OldLocation> retrievedLocations = ((AtomManifest) retrievedManifest).getLocations();
        assertEquals(retrievedLocations.size(), 1);

        JSONAssert.assertEquals(manifest.toJSON().toString(), retrievedManifest.toJSON().toString(), true);
    }

    @Test
    public void testRetrieveAtomData() throws Exception {
        Collection<OldLocation> locations = new ArrayList<OldLocation>();
        OldLocation location = Helper.createDummyDataFile(configuration);
        locations.add(location);
        AtomManifest manifest = model.addAtom(locations);
        assertEquals(manifest.getManifestType(), ManifestConstants.ATOM);

        Manifest retrievedManifest = model.getManifest(manifest.getContentGUID());
        InputStream inputStream = model.getAtomContent((AtomManifest) retrievedManifest);

        assertTrue(IOUtils.contentEquals(location.getSource(), inputStream));
    }

    @Test
    public void testAtomDataVerify() throws Exception {
        Collection<OldLocation> locations = new ArrayList<OldLocation>();
        OldLocation location = Helper.createDummyDataFile(configuration);
        locations.add(location);
        AtomManifest manifest = model.addAtom(locations);
        assertEquals(manifest.getManifestType(), ManifestConstants.ATOM);

        Manifest retrievedManifest = model.getManifest(manifest.getContentGUID());
        assertTrue(retrievedManifest.verify(null));
    }

    @Test
    public void testAtomDataVerifyFails() throws Exception {
        Collection<OldLocation> locations = new ArrayList<OldLocation>();
        OldLocation location = Helper.createDummyDataFile(configuration);
        locations.add(location);
        AtomManifest manifest = model.addAtom(locations);
        assertEquals(manifest.getManifestType(), ManifestConstants.ATOM);

        Manifest retrievedManifest = model.getManifest(manifest.getContentGUID());
        Collection<OldLocation> retrievedLocations = ((AtomManifest) retrievedManifest).getLocations();
        OldLocation cachedLocation = retrievedLocations.iterator().next();

        Helper.appendToFile(cachedLocation, "Data has changed");
        assertFalse(retrievedManifest.verify(null));
    }

    @Test
    public void testAddAtomFromURL() throws Exception {
        Collection<OldLocation> locations = new ArrayList<OldLocation>();
        OldLocation location = new OldLocation("http://www.eastcottvets.co.uk/uploads/Animals/gingerkitten.jpg");

        locations.add(location);
        AtomManifest manifest = model.addAtom(locations);
        assertEquals(manifest.getManifestType(), ManifestConstants.ATOM);

        Manifest retrievedManifest = model.getManifest(manifest.getContentGUID());
        assertEquals(ManifestConstants.ATOM, retrievedManifest.getManifestType());

        System.out.println("SeaOfStuffAddAtomTest: " + manifest.getContentGUID());
    }

    @Test
    public void testAddAtomFromURLHttps() throws Exception {
        Collection<OldLocation> locations = new ArrayList<OldLocation>();
        OldLocation location = new OldLocation("https://i.ytimg.com/vi/NtgtMQwr3Ko/maxresdefault.jpg");
        locations.add(location);
        AtomManifest manifest = model.addAtom(locations);
        assertEquals(manifest.getManifestType(), ManifestConstants.ATOM);

        Manifest retrievedManifest = model.getManifest(manifest.getContentGUID());
        assertEquals(ManifestConstants.ATOM, retrievedManifest.getManifestType());

        System.out.println("SeaOfStuffAddAtomTest: " + manifest.getContentGUID());
    }

    @Test
    public void testAddAtomFromURLHttpsPdf() throws Exception {
        Collection<OldLocation> locations = new ArrayList<OldLocation>();
        OldLocation location = new OldLocation("https://studres.cs.st-andrews.ac.uk/CS1002/Lectures/W01/W01-Lecture.pdf");
        locations.add(location);
        AtomManifest manifest = model.addAtom(locations);
        assertEquals(manifest.getManifestType(), ManifestConstants.ATOM);

        Manifest retrievedManifest = model.getManifest(manifest.getContentGUID());
        assertEquals(ManifestConstants.ATOM, retrievedManifest.getManifestType());

        System.out.println("SeaOfStuffAddAtomTest: " + manifest.getContentGUID());
    }

    @Test
    public void testAddAtomFromURLHttpsTextFile() throws Exception {
        Collection<OldLocation> locations = new ArrayList<OldLocation>();
        OldLocation location = new OldLocation("https://studres.cs.st-andrews.ac.uk/CS1002/Examples/W01/Example1/W01Example1.java");
        locations.add(location);
        AtomManifest manifest = model.addAtom(locations);
        assertEquals(manifest.getManifestType(), ManifestConstants.ATOM);

        Manifest retrievedManifest = model.getManifest(manifest.getContentGUID());
        assertEquals(ManifestConstants.ATOM, retrievedManifest.getManifestType());

        System.out.println("SeaOfStuffAddAtomTest: " + manifest.getContentGUID());
    }
    */
}