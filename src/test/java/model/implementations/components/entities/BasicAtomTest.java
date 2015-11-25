package model.implementations.components.entities;

import IO.sources.DataSource;
import model.interfaces.components.entities.Atom;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class BasicAtomTest {

    @Test
    public void testGetSource() throws Exception {
        DataSource source = mock(DataSource.class);
        Atom atom = new BasicAtom(source);

        assertEquals(atom.getSource(), source);
    }
}