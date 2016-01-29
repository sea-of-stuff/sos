package uk.ac.standrews.cs.sos.model.implementations;

import org.apache.commons.io.FileUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.configurations.SeaConfiguration;
import uk.ac.standrews.cs.sos.configurations.TestConfiguration;
import uk.ac.standrews.cs.sos.exceptions.UnknownGUIDException;
import uk.ac.standrews.cs.sos.exceptions.identity.KeyGenerationException;
import uk.ac.standrews.cs.sos.exceptions.identity.KeyLoadedException;
import uk.ac.standrews.cs.sos.managers.LuceneCache;
import uk.ac.standrews.cs.sos.managers.MemCache;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUIDsha1;
import uk.ac.standrews.cs.sos.model.interfaces.SeaOfStuff;

import java.io.File;
import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SeaOfStuffGeneralTest {

    protected SeaOfStuff model;
    protected MemCache cache;
    protected SeaConfiguration configuration;

    @BeforeMethod
    public void setUp() {
        try {
            configuration = new TestConfiguration();
            cache = LuceneCache.getInstance(configuration);
            model = new SeaOfStuffImpl(configuration, cache);
        } catch (KeyGenerationException e) {
            e.printStackTrace();
        } catch (KeyLoadedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterMethod
    public void tearDown() throws IOException {
        cache.flushDB();
        cache.killInstance();

        FileUtils.deleteDirectory(new File(cache.getConfiguration().getIndexPath()));
        FileUtils.cleanDirectory(new File(cache.getConfiguration().getLocalManifestsLocation()));
        FileUtils.cleanDirectory(new File(cache.getConfiguration().getDataPath()));
        FileUtils.cleanDirectory(new File(cache.getConfiguration().getCacheDataPath()));
    }

    @Test(expectedExceptions = UnknownGUIDException.class)
    public void testFailRetrieveManifest() throws Exception {
        model.getManifest(new GUIDsha1("123fail"));
    }

    @Test (expectedExceptions = UnknownGUIDException.class)
    public void testFailRetrieveManifestNull() throws Exception {
        model.getManifest(null);
    }
}
