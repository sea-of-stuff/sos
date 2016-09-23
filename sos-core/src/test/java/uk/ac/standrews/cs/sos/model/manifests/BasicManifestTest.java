package uk.ac.standrews.cs.sos.model.manifests;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.sos.CommonTest;
import uk.ac.standrews.cs.sos.constants.Hashes;

import static org.testng.Assert.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class BasicManifestTest extends CommonTest {

    @Test
    public void testGenerateGUID() throws Exception {
        assertEquals(GUIDFactory.generateGUID(Hashes.TEST_STRING).toString(), Hashes.TEST_STRING_HASHED);
    }
    
}
