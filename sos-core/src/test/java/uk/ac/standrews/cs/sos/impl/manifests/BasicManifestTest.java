package uk.ac.standrews.cs.sos.impl.manifests;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.guid.ALGORITHM;
import uk.ac.standrews.cs.guid.BASE;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.sos.CommonTest;
import uk.ac.standrews.cs.sos.constants.Hashes;

import static org.testng.Assert.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class BasicManifestTest extends CommonTest {

    @Test
    public void testGenerateGUID() throws Exception {
        assertEquals(GUIDFactory.generateGUID(ALGORITHM.SHA256, Hashes.TEST_STRING).toMultiHash(BASE.HEX), Hashes.TEST_STRING_HASHED);
    }
    
}
