package uk.ac.standrews.cs.sos.model.index;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.sos.exceptions.IndexException;
import uk.ac.standrews.cs.sos.exceptions.SeaConfigurationException;
import uk.ac.standrews.cs.sos.interfaces.index.Index;
import uk.ac.standrews.cs.sos.model.SeaConfiguration;
import uk.ac.standrews.cs.sos.model.manifests.AtomManifest;
import uk.ac.standrews.cs.utils.Helper;

import java.io.IOException;
import java.lang.reflect.Method;

import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class CommonIndexTest {

    private Index index;

    @BeforeMethod
    public void setUp(Method method) throws IndexException, SeaConfigurationException {
        SeaConfiguration.setRootName("test");
        SeaConfiguration configuration = SeaConfiguration.getInstance();
        index = LuceneIndex.getInstance(configuration);
    }

    @AfterMethod
    public void tearDown() throws IOException, IndexException {
        index.flushDB();
        index.killInstance();

        Helper.deleteDirectory(index.getConfiguration().getIndexPath().getPathname());
    }

    @Test(expectedExceptions = IndexException.class)
    public void testAddManifestWithWrongType() throws Exception {
        AtomManifest simpleManifestMocked = mock(AtomManifest.class);

        when(simpleManifestMocked.getContentGUID()).thenReturn(GUIDFactory.recreateGUID("123"));
        when(simpleManifestMocked.getManifestType()).thenReturn("WrongType");

        index.addManifest(simpleManifestMocked);
    }
}
