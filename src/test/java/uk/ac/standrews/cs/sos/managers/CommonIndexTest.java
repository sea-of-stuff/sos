package uk.ac.standrews.cs.sos.managers;

import org.apache.commons.io.FileUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.configurations.SeaConfiguration;
import uk.ac.standrews.cs.sos.exceptions.manifest.UnknownManifestTypeException;
import uk.ac.standrews.cs.sos.model.implementations.components.manifests.AtomManifest;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUIDsha1;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class CommonIndexTest {

    private SeaConfiguration configuration;
    private Index index;

    @BeforeMethod
    public void setUp(Method method) throws IOException {
        configuration = SeaConfiguration.getInstance();
        index = LuceneIndex.getInstance(configuration);
    }

    @AfterMethod
    public void tearDown() throws IOException {
        index.flushDB();
        index.killInstance();

        FileUtils.deleteDirectory(new File(index.getConfiguration().getIndexPath()));
    }

    @Test(expectedExceptions = UnknownManifestTypeException.class)
    public void testAddManifestWithWrongType() throws Exception {
        AtomManifest simpleManifestMocked = mock(AtomManifest.class);

        when(simpleManifestMocked.getContentGUID()).thenReturn(new GUIDsha1("123"));
        when(simpleManifestMocked.getManifestType()).thenReturn("WrongType");

        index.addManifest(simpleManifestMocked);
    }
}
