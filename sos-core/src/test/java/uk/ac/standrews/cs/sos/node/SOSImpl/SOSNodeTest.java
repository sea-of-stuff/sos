package uk.ac.standrews.cs.sos.node.SOSImpl;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import uk.ac.standrews.cs.sos.SetUpTest;
import uk.ac.standrews.cs.sos.exceptions.IndexException;
import uk.ac.standrews.cs.sos.interfaces.node.SeaOfStuff;
import uk.ac.standrews.cs.sos.node.ROLE;
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

        Helper.DeletePath(configuration.getIndexDirectory());
        Helper.DeletePath(configuration.getManifestsDirectory());
        Helper.DeletePath(configuration.getTestDataDirectory());
        Helper.DeletePath(configuration.getDataDirectory());
        Helper.DeletePath(configuration.getDatabaseDump().getParent());
    }

    public abstract ROLE nodeRole();
}
