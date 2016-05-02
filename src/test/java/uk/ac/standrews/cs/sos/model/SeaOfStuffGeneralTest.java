package uk.ac.standrews.cs.sos.model;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.SetUpTest;
import uk.ac.standrews.cs.sos.exceptions.IndexException;
import uk.ac.standrews.cs.sos.exceptions.SeaConfigurationException;
import uk.ac.standrews.cs.sos.exceptions.SeaOfStuffException;
import uk.ac.standrews.cs.sos.exceptions.storage.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.interfaces.SeaOfStuff;
import uk.ac.standrews.cs.sos.interfaces.index.Index;
import uk.ac.standrews.cs.sos.model.index.LuceneIndex;
import uk.ac.standrews.cs.utils.Helper;

import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SeaOfStuffGeneralTest extends SetUpTest {

    protected SeaOfStuff model;
    protected Index index;
    protected SeaConfiguration configuration;

    @BeforeMethod
    public void setUp() throws SeaOfStuffException, IndexException, SeaConfigurationException {
        SeaConfiguration.setRootName("test");
        configuration = SeaConfiguration.getInstance();
        index = LuceneIndex.getInstance(configuration);
        model = new SeaOfStuffImpl(configuration, index);
    }

    @AfterMethod
    public void tearDown() throws IOException, IndexException {
        index.flushDB();
        index.killInstance();

        Helper.deleteDirectory(index.getConfiguration().getIndexDirectory());
        Helper.cleanDirectory(index.getConfiguration().getManifestsDirectory());
        Helper.cleanDirectory(index.getConfiguration().getDataDirectory());
        Helper.cleanDirectory(index.getConfiguration().getCacheDirectory());
        Helper.cleanDirectory(index.getConfiguration().getDBDirectory());
    }

    @Test(expectedExceptions = ManifestNotFoundException.class)
    public void testFailRetrieveManifest() throws Exception {
        model.getManifest(GUIDFactory.recreateGUID("123fa11"));
    }

    @Test (expectedExceptions = ManifestNotFoundException.class)
    public void testFailRetrieveManifestNull() throws Exception {
        model.getManifest(null);
    }
}
