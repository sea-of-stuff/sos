package uk.ac.standrews.cs.sos;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.sos.exceptions.IndexException;
import uk.ac.standrews.cs.sos.interfaces.index.Index;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.model.Configuration;
import uk.ac.standrews.cs.sos.model.index.LuceneIndex;
import uk.ac.standrews.cs.sos.node.Config;
import uk.ac.standrews.cs.sos.node.LocalSOSNode;
import uk.ac.standrews.cs.sos.node.SOSNode;
import uk.ac.standrews.cs.sos.utils.HelperTest;

import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SetUpTest {

    protected LocalSOSNode localSOSNode;
    protected Index index;
    protected Configuration configuration;

    @BeforeMethod
    public void setUp() throws Exception {
        Node testNode = new SOSNode(GUIDFactory.generateRandomGUID());
        Configuration.setNode(testNode);
        configuration = Configuration.getInstance();

        HelperTest.CreateDBTestDump();

        index = LuceneIndex.getInstance();

        LocalSOSNode.setIndex(index);
        LocalSOSNode.create(configuration);
        localSOSNode = LocalSOSNode.getInstance();
    }

    @AfterMethod
    public void tearDown() throws IOException, IndexException {
        index.flushDB();
        index.killInstance();

        HelperTest.DeletePath(configuration.getIndexDirectory());
        HelperTest.DeletePath(configuration.getManifestsDirectory());
        HelperTest.DeletePath(configuration.getTestDataDirectory());
        HelperTest.DeletePath(configuration.getDataDirectory());

        HelperTest.DeletePath(Config.DB_DIRECTORY);
    }
}
