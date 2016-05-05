package uk.ac.standrews.cs.sos.node;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.SetUpTest;
import uk.ac.standrews.cs.sos.exceptions.NodeManagerException;
import uk.ac.standrews.cs.sos.exceptions.db.DatabasePersistenceException;
import uk.ac.standrews.cs.sos.interfaces.index.Index;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.model.index.LuceneIndex;

import java.net.InetSocketAddress;

import static org.testng.Assert.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class NodeManagerTest extends SetUpTest {

    NodeManager nodeManager;

    @Override
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        Index index = LuceneIndex.getInstance(configuration);

        NodeManager.setConfiguration(configuration);
        NodeManager.setIndex(index);
        nodeManager = NodeManager.getInstance();
    }

    @Test(priority=0)
    public void persistTest() throws GUIDGenerationException, DatabasePersistenceException, NodeManagerException {

        IGUID guid = GUIDFactory.generateGUID("test");
        InetSocketAddress inetSocketAddress = new InetSocketAddress("example.com", 8080);
        Node node = new SOSNode(guid, inetSocketAddress); // TODO - actually this should be external

        assertEquals(nodeManager.getKnownNodes().size(), 0);

        nodeManager.addNode(node);
        assertEquals(nodeManager.getKnownNodes().size(), 1);

        nodeManager.persist();
        assertEquals(nodeManager.getKnownNodes().size(), 1);
    }

    @Test(priority=1)
    public void getKnownNodesTest() throws DatabasePersistenceException, NodeManagerException {
        assertEquals(nodeManager.getKnownNodes().size(), 1);
    }

    @Test()
    public void getNodeTest() throws DatabasePersistenceException, NodeManagerException {
        assertEquals(nodeManager.getThisNode(), configuration.getNode());
    }

}
