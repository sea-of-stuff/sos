package model.implementations;

import IO.sources.DataSource;
import model.factories.AtomFactory;
import model.implementations.components.manifests.ManifestTypes;
import model.interfaces.SeaOfStuff;
import model.interfaces.components.entities.Atom;
import model.interfaces.components.manifests.AtomManifest;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertEquals;

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

    @Test
    public void testAddAtom() throws Exception {
        DataSource source = mock(DataSource.class);
        Atom atom = AtomFactory.makeAtom(source);
        AtomManifest atomManifest = model.addAtom(atom);

        // TODO - test atom manifest
        assertEquals(atomManifest.getManifestType(), ManifestTypes.ATOM);
    }
}