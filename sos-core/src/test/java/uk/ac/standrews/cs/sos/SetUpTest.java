package uk.ac.standrews.cs.sos;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.sos.exceptions.IndexException;
import uk.ac.standrews.cs.sos.interfaces.index.Index;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.model.Configuration;
import uk.ac.standrews.cs.sos.model.index.LuceneIndex;
import uk.ac.standrews.cs.sos.node.LocalSOSNode;
import uk.ac.standrews.cs.sos.node.SOSNode;
import uk.ac.standrews.cs.sos.utils.Helper;

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

        Configuration.setRootName("test");
        Node testNode = new SOSNode(GUIDFactory.recreateGUID("12345678"));
        Configuration.setNode(testNode);
        configuration = Configuration.getInstance();
        Helper.DeletePath(configuration.getDatabaseDump().getParent());

        index = LuceneIndex.getInstance();

        LocalSOSNode.setIndex(index);
        LocalSOSNode.create(configuration);
        localSOSNode = LocalSOSNode.getInstance();
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
}
