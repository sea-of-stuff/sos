package uk.ac.standrews.cs.sos.model;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.sos.SetUpTest;
import uk.ac.standrews.cs.sos.exceptions.IndexException;
import uk.ac.standrews.cs.sos.exceptions.NodeManagerException;
import uk.ac.standrews.cs.sos.exceptions.SeaConfigurationException;
import uk.ac.standrews.cs.sos.exceptions.storage.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.interfaces.index.Index;
import uk.ac.standrews.cs.sos.interfaces.node.Roles;
import uk.ac.standrews.cs.sos.interfaces.node.SeaOfStuff;
import uk.ac.standrews.cs.sos.model.index.LuceneIndex;
import uk.ac.standrews.cs.sos.node.NodeManager;
import uk.ac.standrews.cs.sos.utils.Helper;

import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SeaOfStuffGeneralTest extends SetUpTest {

    protected NodeManager nodeManager;
    protected Index index;
    protected SeaConfiguration configuration;

    protected SeaOfStuff model;

    @Override
    @BeforeMethod
    public void setUp() throws IndexException, SeaConfigurationException, NodeManagerException {
        SeaConfiguration.setRootName("test");
        configuration = SeaConfiguration.getInstance();
        index = LuceneIndex.getInstance(configuration);

        NodeManager.setConfiguration(configuration);
        NodeManager.setIndex(index);
        nodeManager = NodeManager.getInstance();

        model = nodeManager.getSOS(Roles.CLIENT);
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
