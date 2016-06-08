package uk.ac.standrews.cs.sos.node.SOS;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import uk.ac.standrews.cs.sos.SetUpTest;
import uk.ac.standrews.cs.sos.exceptions.IndexException;
import uk.ac.standrews.cs.sos.interfaces.node.ROLE;
import uk.ac.standrews.cs.sos.interfaces.node.SeaOfStuff;
import uk.ac.standrews.cs.sos.utils.Helper;

import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class SOSNodeTest extends SetUpTest {

    protected SeaOfStuff model;

    @Override
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();

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
