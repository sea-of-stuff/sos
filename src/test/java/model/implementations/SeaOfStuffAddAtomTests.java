package model.implementations;

import model.interfaces.SeaOfStuff;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SeaOfStuffAddAtomTests {

    private SeaOfStuff model;

    @BeforeMethod
    public void setUp() {
        model = new SeaOfStuffImpl();
    }

    @AfterMethod
    public void tearDown() {
        model = null;
    }

    // XXX - addAtom throws NotImplementedException
    /*
    @Test (expectedExceptions = NotImplementedException.class)
    public void testAddAtom() throws Exception {
        DataSource source = mock(DataSource.class);
        Atom atom = AtomFactory.makeAtom(source);

        AtomManifest atomManifest = model.addAtom(atom);

        // TODO - test atom manifest
        assertEquals(atomManifest.getManifestType(), ManifestConstants.ATOM);
    }
    */
}