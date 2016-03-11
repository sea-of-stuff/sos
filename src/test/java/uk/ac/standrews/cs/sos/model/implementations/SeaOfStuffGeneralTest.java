package uk.ac.standrews.cs.sos.model.implementations;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.SetUpTest;
import uk.ac.standrews.cs.sos.configurations.SeaConfiguration;
import uk.ac.standrews.cs.sos.exceptions.UnknownGUIDException;
import uk.ac.standrews.cs.sos.exceptions.identity.KeyGenerationException;
import uk.ac.standrews.cs.sos.exceptions.identity.KeyLoadedException;
import uk.ac.standrews.cs.sos.managers.Index;
import uk.ac.standrews.cs.sos.managers.LuceneIndex;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUIDsha1;
import uk.ac.standrews.cs.sos.model.interfaces.SeaOfStuff;
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
    public void setUp() {
        try {
            configuration = SeaConfiguration.getInstance();
            index = LuceneIndex.getInstance(configuration);
            model = new SeaOfStuffImpl(configuration, index);
        } catch (KeyGenerationException | KeyLoadedException | IOException e) {
            e.printStackTrace();
        }
    }

    @AfterMethod
    public void tearDown() throws IOException {
        index.flushDB();
        index.killInstance();

        Helper.deleteDirectory(index.getConfiguration().getIndexPath());
        Helper.cleanDirectory(index.getConfiguration().getLocalManifestsLocation());
        Helper.cleanDirectory(index.getConfiguration().getDataPath());
        Helper.cleanDirectory(index.getConfiguration().getCacheDataPath());
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
