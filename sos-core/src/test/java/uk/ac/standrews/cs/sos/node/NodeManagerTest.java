package uk.ac.standrews.cs.sos.node;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.CommonTest;
import uk.ac.standrews.cs.sos.configuration.SOSConfiguration;
import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.exceptions.db.DatabaseException;
import uk.ac.standrews.cs.sos.exceptions.node.NodeManagerException;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.interfaces.node.NodeDatabase;
import uk.ac.standrews.cs.sos.node.database.DatabaseTypes;
import uk.ac.standrews.cs.sos.node.database.SQLDatabase;
import uk.ac.standrews.cs.sos.utils.HelperTest;

import java.io.IOException;
import java.lang.reflect.Method;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class NodeManagerTest extends CommonTest {

    NodeManager nodeManager;

    SOSConfiguration configurationMock = mock(SOSConfiguration.class);

    @BeforeMethod
    public void setUp(Method testMethod) throws Exception {
        super.setUp(testMethod);

        when(configurationMock.getDBType()).thenReturn(DatabaseTypes.SQLITE_DB);
        when(configurationMock.getDBPath()).thenReturn(System.getProperty("user.home") + "/sos/db/dump.db");

        NodeDatabase nodeDatabase;
        try {
            nodeDatabase = new SQLDatabase(configurationMock.getDBType(),
                    configurationMock.getDBPath());
        } catch (DatabaseException e) {
            throw new SOSException(e);
        }

        Node node = mock(SOSLocalNode.class);
        nodeManager = new NodeManager(node, nodeDatabase);
    }

    @AfterMethod
    public void classTearDown() throws IOException {
        HelperTest.DeletePath(configurationMock.getDBPath());
    }

    @Test
    public void persistMultipleNodesTest() throws GUIDGenerationException, NodeManagerException {
        IGUID guid = GUIDFactory.generateRandomGUID();
        Node node = new SOSNode(guid, "example.com", 8080, true, false, false, false, false);
        nodeManager.addNode(node);

        addNode(true, false, false, false, false);
        addNode(false, true, false, false, false);
        addNode(false, false, true, false, false);
        addNode(false, false, false, true, false);
        addNode(false, false, false, false, true);

        addNode(true, true, false, false, false);
        addNode(true, true, true, true, true);

        assertEquals(nodeManager.getKnownNodes().size(), 8);
        assertEquals(nodeManager.getNode(guid), node);
        assertEquals(nodeManager.getStorageNodes().size(), 3);
        assertEquals(nodeManager.getDDSNodes().size(), 2);
        assertEquals(nodeManager.getNDSNodes().size(), 2);
        assertEquals(nodeManager.getMCSNodes().size(), 2);
    }

    @Test
    public void persistTest() throws GUIDGenerationException, NodeManagerException {
        IGUID guid = GUIDFactory.generateRandomGUID();
        Node node = new SOSNode(guid, "example.com", 8080, true, false, false, false, false);

        assertEquals(nodeManager.getKnownNodes().size(), 0);

        nodeManager.addNode(node);
        assertEquals(nodeManager.getKnownNodes().size(), 1);

        nodeManager.persistNodesTable();
        assertEquals(nodeManager.getKnownNodes().size(), 1);
    }

    private void addNode(IGUID guid, boolean isClient, boolean isStorage, boolean isDDS, boolean isNDS, boolean isMCS) {
        nodeManager.addNode(new SOSNode(guid, "example.com", 8080,
                isClient, isStorage, isDDS,
                isNDS, isMCS));
    }

    private void addNode(boolean isClient, boolean isStorage, boolean isDDS, boolean isNDS, boolean isMCS) {
        IGUID guid = GUIDFactory.generateRandomGUID();
        addNode(guid, isClient, isStorage, isDDS, isNDS, isMCS);
    }
}
