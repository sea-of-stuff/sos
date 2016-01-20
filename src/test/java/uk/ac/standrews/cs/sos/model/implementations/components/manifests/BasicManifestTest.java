package uk.ac.standrews.cs.sos.model.implementations.components.manifests;

import org.mockito.Mockito;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.constants.Hashes;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class BasicManifestTest {

    @Test
    public void testGenerateGUID() throws Exception {
        BasicManifest manifest = mock(BasicManifest.class, Mockito.CALLS_REAL_METHODS);
        assertEquals(manifest.generateGUID(Hashes.TEST_STRING).toString(), Hashes.TEST_STRING_HASHED);
    }
    
}