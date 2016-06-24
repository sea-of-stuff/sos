package uk.ac.standrews.cs.sos.node;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.exceptions.NodeManagerException;
import uk.ac.standrews.cs.sos.exceptions.db.DatabasePersistenceException;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.utils.HelperTest;

import java.io.IOException;
import java.net.InetSocketAddress;

import static org.testng.Assert.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class NodeManagerTest {

    NodeManager nodeManager;

    @BeforeMethod
    public void setUp() throws Exception {
        Config.db_type = Config.DB_TYPE_SQLITE;
        Config.initDatabaseInfo();

        nodeManager = new NodeManager();
    }

    @AfterClass
    public void classTearDown() throws ConfigurationException, IOException {
        HelperTest.DeletePath(Config.DB_DIRECTORY);
    }

    @Test(priority=0)
    public void persistTest() throws GUIDGenerationException, DatabasePersistenceException, NodeManagerException {
        IGUID guid = GUIDFactory.generateGUID("test");
        InetSocketAddress inetSocketAddress = new InetSocketAddress("example.com", 8080);
        Node node = new SOSNode(guid, inetSocketAddress);

        assertEquals(nodeManager.getKnownNodes().size(), 0);

        nodeManager.addNode(node);
        assertEquals(nodeManager.getKnownNodes().size(), 1);

        nodeManager.persistNodesTable();
        assertEquals(nodeManager.getKnownNodes().size(), 1);
    }

    @Test(priority=1, dependsOnMethods = { "persistTest" })
    public void getKnownNodesTest() throws DatabasePersistenceException, NodeManagerException {
        assertEquals(nodeManager.getKnownNodes().size(), 1);
    }

}
