package uk.ac.standrews.cs.sos.impl.datamodel;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.sos.CommonTest;
import uk.ac.standrews.cs.sos.constants.Hashes;

import static org.testng.Assert.assertEquals;
import static uk.ac.standrews.cs.sos.constants.Internals.GUID_ALGORITHM;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class BasicManifestTest extends CommonTest {

    @Test
    public void testGenerateGUID() throws Exception {
        assertEquals(GUIDFactory.generateGUID(GUID_ALGORITHM, Hashes.TEST_STRING).toMultiHash(), Hashes.TEST_STRING_HASHED);
    }
    
}
