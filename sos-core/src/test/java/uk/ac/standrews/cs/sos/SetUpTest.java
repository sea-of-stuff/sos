package uk.ac.standrews.cs.sos;

import org.testng.annotations.BeforeMethod;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.sos.interfaces.index.Index;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.model.Configuration;
import uk.ac.standrews.cs.sos.model.index.LuceneIndex;
import uk.ac.standrews.cs.sos.node.NodeManager;
import uk.ac.standrews.cs.sos.node.SOSNode;
import uk.ac.standrews.cs.sos.utils.Helper;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SetUpTest {

    protected NodeManager nodeManager;
    protected Index index;
    protected Configuration configuration;

    @BeforeMethod
    public void setUp() throws Exception {
        Configuration.setRootName("test");
        configuration = Configuration.getInstance();

        Helper.DeletePath(configuration.getDatabaseDump().getParent());

        Node node = new SOSNode(GUIDFactory.recreateGUID("12345678"));
        configuration.setNode(node);

        index = LuceneIndex.getInstance(configuration);

        NodeManager.setConfiguration(configuration);
        NodeManager.setIndex(index);
        nodeManager = NodeManager.getInstance();
    }
}
