package uk.ac.standrews.cs.sos.node;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.configuration.Config;
import uk.ac.standrews.cs.sos.exceptions.NodeManagerException;
import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.exceptions.db.DatabaseException;
import uk.ac.standrews.cs.sos.exceptions.db.DatabasePersistenceException;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.interfaces.node.NodeDatabase;
import uk.ac.standrews.cs.sos.node.database.DatabaseType;
import uk.ac.standrews.cs.sos.node.database.SQLDatabase;
import uk.ac.standrews.cs.sos.utils.HelperTest;

import java.io.IOException;

import static org.mockito.Mockito.mock;
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

        NodeDatabase nodeDatabase;
        try {
            nodeDatabase = new SQLDatabase(new DatabaseType(Config.db_type),
                    Config.DB_DUMP_FILE.getPathname());
        } catch (DatabaseException e) {
            throw new SOSException(e);
        }

        Node node = mock(SOSLocalNode.class);
        nodeManager = new NodeManager(node, nodeDatabase);
    }

    @AfterClass
    public void classTearDown() throws IOException {
        HelperTest.DeletePath(Config.DB_DIRECTORY);
    }

    @Test(priority=0)
    public void persistTest() throws GUIDGenerationException, DatabasePersistenceException, NodeManagerException {
        IGUID guid = GUIDFactory.generateRandomGUID();
        Node node = new SOSNode(guid, "example.com", 8080, true, false, false); // TODO - more tests of this kind (with different combos of client,storage,coordinator)

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
