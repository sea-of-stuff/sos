package uk.ac.standrews.cs.sos.node;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.CommonTest;
import uk.ac.standrews.cs.sos.configuration.SOSConfiguration;
import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.exceptions.db.DatabaseException;
import uk.ac.standrews.cs.sos.exceptions.node.NodesDirectoryException;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.interfaces.node.NodesDatabase;
import uk.ac.standrews.cs.sos.node.directory.LocalNodesDirectory;
import uk.ac.standrews.cs.sos.node.directory.SQLiteDB;
import uk.ac.standrews.cs.sos.utils.HelperTest;

import java.lang.reflect.Method;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class LocalNodesDirectoryTest extends CommonTest {

    private LocalNodesDirectory localNodesDirectory;
    private SOSConfiguration configurationMock = mock(SOSConfiguration.class);
    private Node testNode;

    @BeforeMethod
    public void setUp(Method testMethod) throws Exception {
        super.setUp(testMethod);

        when(configurationMock.getDBPath()).thenReturn(System.getProperty("user.home") + "/sos/db/dump.db");

        // Make sure that the DB path is clean
        HelperTest.DeletePath(configurationMock.getDBPath());

        NodesDatabase nodesDatabase;
        try {
            nodesDatabase = new SQLiteDB(configurationMock.getDBPath());
        } catch (DatabaseException e) {
            throw new SOSException(e);
        }

        testNode = mock(SOSLocalNode.class);
        localNodesDirectory = new LocalNodesDirectory(testNode, nodesDatabase);
    }

    @Test
    public void getLocalNodeTest() {
        Node localNode = localNodesDirectory.getLocalNode();
        assertEquals(localNode, testNode);
    }

    @Test
    public void basicAddGetNodeTest() {
        IGUID guid = GUIDFactory.generateRandomGUID();
        Node node = new SOSNode(guid, "example.com", 8080, true, false, false, false, false);

        localNodesDirectory.addNode(node);
        Node retrievedNode = localNodesDirectory.getNode(guid);
        assertEquals(retrievedNode, node);
    }

    @Test
    public void persistMultipleNodesTest() throws GUIDGenerationException, NodesDirectoryException {

        Set<Node> nodes = localNodesDirectory.getKnownNodes();
        assertEquals(nodes.size(), 0);

        IGUID guid = GUIDFactory.generateRandomGUID();
        Node node = new SOSNode(guid, "example.com", 8080, true, false, false, false, false);
        localNodesDirectory.addNode(node);

        addNode(true, false, false, false, false);
        addNode(false, true, false, false, false);
        addNode(false, false, true, false, false);
        addNode(false, false, false, true, false);
        addNode(false, false, false, false, true);
        addNode(true, true, false, false, false);
        addNode(true, true, true, true, true);

        assertEquals(localNodesDirectory.getKnownNodes().size(), 8);
        assertEquals(localNodesDirectory.getNode(guid), node);
        assertEquals(localNodesDirectory.getStorageNodes(LocalNodesDirectory.NO_LIMIT).size(), 3);
        assertEquals(localNodesDirectory.getDDSNodes(LocalNodesDirectory.NO_LIMIT).size(), 2);
        assertEquals(localNodesDirectory.getNDSNodes(LocalNodesDirectory.NO_LIMIT).size(), 2);
        assertEquals(localNodesDirectory.getMCSNodes(LocalNodesDirectory.NO_LIMIT).size(), 2);
    }

    @Test
    public void persistTest() throws GUIDGenerationException, NodesDirectoryException {
        IGUID guid = GUIDFactory.generateRandomGUID();
        Node node = new SOSNode(guid, "example.com", 8080, true, false, false, false, false);

        assertEquals(localNodesDirectory.getKnownNodes().size(), 0);

        localNodesDirectory.addNode(node);
        assertEquals(localNodesDirectory.getKnownNodes().size(), 1);

        localNodesDirectory.persistNodesTable();
        assertEquals(localNodesDirectory.getKnownNodes().size(), 1);
    }

    @Test
    public void getStorageNodesWithLimitTest() {
        IGUID guid = GUIDFactory.generateRandomGUID();
        Node node = new SOSNode(guid, "example.com", 8080, true, false, false, false, false);
        localNodesDirectory.addNode(node);

        addNode(true, false, false, false, false);
        addNode(false, true, false, false, false);
        addNode(false, false, true, false, false);
        addNode(false, false, false, true, false);
        addNode(false, false, false, false, true);
        addNode(true, true, false, false, false);
        addNode(true, true, true, true, true);

        Set<Node> storageNodes = localNodesDirectory.getStorageNodes(3);
        assertEquals(storageNodes.size(), 3);
    }

    @Test
    public void getStorageNodesWithLimitCapTest() {
        IGUID guid = GUIDFactory.generateRandomGUID();
        Node node = new SOSNode(guid, "example.com", 8080, true, false, false, false, false);
        localNodesDirectory.addNode(node);

        addNode(true, false, false, false, false);
        addNode(false, true, false, false, false);
        addNode(false, false, true, false, false);
        addNode(false, false, false, true, false);
        addNode(false, false, false, false, true);
        addNode(true, true, false, false, false);
        addNode(true, true, true, true, true);

        Set<Node> storageNodes = localNodesDirectory.getStorageNodes(2);
        assertEquals(storageNodes.size(), 2);
    }

    @Test
    public void getStorageNodesWithLimitInExcessTest() {
        IGUID guid = GUIDFactory.generateRandomGUID();
        Node node = new SOSNode(guid, "example.com", 8080, true, false, false, false, false);
        localNodesDirectory.addNode(node);

        addNode(true, false, false, false, false);
        addNode(false, true, false, false, false);
        addNode(false, false, true, false, false);
        addNode(false, false, false, true, false);
        addNode(false, false, false, false, true);
        addNode(true, true, false, false, false);
        addNode(true, true, true, true, true);

        Set<Node> storageNodes = localNodesDirectory.getStorageNodes(10);
        assertEquals(storageNodes.size(), 3);
    }

    @Test
    public void getStorageNodesIgnoreNegativeLimitTest() {
        IGUID guid = GUIDFactory.generateRandomGUID();
        Node node = new SOSNode(guid, "example.com", 8080, true, false, false, false, false);
        localNodesDirectory.addNode(node);

        addNode(true, false, false, false, false);
        addNode(false, true, false, false, false);
        addNode(false, false, true, false, false);
        addNode(false, false, false, true, false);
        addNode(false, false, false, false, true);
        addNode(true, true, false, false, false);
        addNode(true, true, true, true, true);

        Set<Node> storageNodes = localNodesDirectory.getStorageNodes(-1);
        assertEquals(storageNodes.size(), 3);
    }

    @Test
    public void nodeIsUpdatedTest() throws NodesDirectoryException {
        IGUID guid = GUIDFactory.generateRandomGUID();
        Node node = new SOSNode(guid, "example.com", 8080, true, false, false, false, false);

        localNodesDirectory.addNode(node);
        Node retrievedNode = localNodesDirectory.getNode(guid);
        assertEquals(retrievedNode, node);

        // Now isStorage node too
        Node updatedNode = new SOSNode(guid, "example.com", 8080, true, true, false, false, false);
        localNodesDirectory.addNode(updatedNode);

        Node retrievedUpdatedNode = localNodesDirectory.getNode(guid);
        assertEquals(retrievedUpdatedNode, updatedNode);
        assertEquals(retrievedUpdatedNode.isStorage(), true);
    }

    private void addNode(IGUID guid, boolean isClient, boolean isStorage, boolean isDDS, boolean isNDS, boolean isMCS) {
        localNodesDirectory.addNode(new SOSNode(guid, "example.com", 8080,
                isClient, isStorage, isDDS,
                isNDS, isMCS));
    }

    private void addNode(boolean isClient, boolean isStorage, boolean isDDS, boolean isNDS, boolean isMCS) {
        IGUID guid = GUIDFactory.generateRandomGUID();
        addNode(guid, isClient, isStorage, isDDS, isNDS, isMCS);
    }
}
