package uk.ac.standrews.cs.sos.impl.node;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.SetUpTest;

import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertNotNull;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class LocalSOSNodeTest extends SetUpTest {

    @Test
    public void isClientByDefault() {
        assertTrue(localSOSNode.isAgent());
        assertNotNull(localSOSNode.getAgent());
    }

}
