package uk.ac.standrews.cs.sos.node.SOS;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import uk.ac.standrews.cs.sos.SetUpTest;
import uk.ac.standrews.cs.sos.exceptions.IndexException;
import uk.ac.standrews.cs.sos.exceptions.NodeManagerException;
import uk.ac.standrews.cs.sos.exceptions.SeaConfigurationException;
import uk.ac.standrews.cs.sos.interfaces.index.Index;
import uk.ac.standrews.cs.sos.interfaces.node.ROLE;
import uk.ac.standrews.cs.sos.interfaces.node.SeaOfStuff;
import uk.ac.standrews.cs.sos.model.SeaConfiguration;
import uk.ac.standrews.cs.sos.model.index.LuceneIndex;
import uk.ac.standrews.cs.sos.node.NodeManager;
import uk.ac.standrews.cs.sos.utils.Helper;

import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class SOSNodeTest extends SetUpTest {

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

        model = nodeManager.getSOS(nodeRole());
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

    public abstract ROLE nodeRole();
}
