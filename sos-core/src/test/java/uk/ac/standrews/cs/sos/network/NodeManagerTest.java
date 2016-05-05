package uk.ac.standrews.cs.sos.network;

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
import uk.ac.standrews.cs.sos.node.SOSNode;
import uk.ac.standrews.cs.sos.node.SOSNodeManager;

import java.net.InetSocketAddress;

import static org.testng.Assert.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class NodeManagerTest extends SetUpTest {

    SOSNodeManager sosNodeManager;

    @Override
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        Index index = LuceneIndex.getInstance(configuration);

        SOSNodeManager.setConfiguration(configuration);
        SOSNodeManager.setIndex(index);
        sosNodeManager = SOSNodeManager.getInstance();
    }

    @Test(priority=0)
    public void persistTest() throws GUIDGenerationException, DatabasePersistenceException, NodeManagerException {

        IGUID guid = GUIDFactory.generateGUID("test");
        InetSocketAddress inetSocketAddress = new InetSocketAddress("example.com", 8080);
        Node node = new SOSNode(guid, inetSocketAddress); // TODO - actually this should be external

        sosNodeManager.addNode(node);
        assertEquals(sosNodeManager.getKnownNodes().size(), 1);

        assertEquals(sosNodeManager.getKnownNodes().size(), 1);
    }

    @Test(priority=1)
    public void getTest() throws DatabasePersistenceException, NodeManagerException {
        assertEquals(sosNodeManager.getKnownNodes().size(), 1);
    }

}
