package uk.ac.standrews.cs.sos.impl.node;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.CommonTest;
import uk.ac.standrews.cs.sos.SettingsConfiguration;
import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.exceptions.db.DatabaseException;
import uk.ac.standrews.cs.sos.exceptions.node.NodesDirectoryException;
import uk.ac.standrews.cs.sos.impl.node.directory.DatabaseImpl;
import uk.ac.standrews.cs.sos.impl.node.directory.LocalNodesDirectory;
import uk.ac.standrews.cs.sos.interfaces.node.Database;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.utils.HelperTest;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static uk.ac.standrews.cs.sos.constants.Paths.TEST_RESOURCES_PATH;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class LocalNodesDirectoryTest extends CommonTest {

    private LocalNodesDirectory localNodesDirectory;
    private Node testNode;

    @BeforeMethod
    public void setUp(Method testMethod) throws Exception {
        super.setUp(testMethod);

        SettingsConfiguration.Settings settings = new SettingsConfiguration(new File(TEST_RESOURCES_PATH + "PATH TO CONFIGURATION FILE")).getSettingsObj();

        // Make sure that the DB path is clean
        HelperTest.DeletePath(settings.getDatabase().getFilename());

        Database database;
        try {
            database = new DatabaseImpl(settings.getDatabase().getFilename());
        } catch (DatabaseException e) {
            throw new SOSException(e);
        }

        testNode = mock(SOSLocalNode.class);
        when(testNode.getNodeGUID()).thenReturn(GUIDFactory.generateRandomGUID());
        localNodesDirectory = new LocalNodesDirectory(testNode, database);
    }

    @Test
    public void getLocalNodeTest() {
        Node localNode = localNodesDirectory.getLocalNode();
        assertEquals(localNode, testNode);
    }

    @Test
    public void basicAddGetNodeTest() {
        IGUID guid = GUIDFactory.generateRandomGUID();
        Node node = new SOSNode(guid, "example.com", 8080, true, false, false, false, false, false, false);

        localNodesDirectory.addNode(node);
        Node retrievedNode = localNodesDirectory.getNode(guid);
        assertEquals(retrievedNode, node);
    }

    @Test
    public void persistMultipleNodesTest() throws GUIDGenerationException, NodesDirectoryException {

        Set<Node> nodes = localNodesDirectory.getKnownNodes();
        assertEquals(nodes.size(), 0);

        IGUID guid = GUIDFactory.generateRandomGUID();
        Node node = new SOSNode(guid, "example.com", 8080, true, false, false, false, false, false, false);
        localNodesDirectory.addNode(node);

        addNode(true, false, false, false, false, false, false);
        addNode(false, true, false, false, false, false, false);
        addNode(false, false, true, false, false, false, false);
        addNode(false, false, false, true, false, false, false);
        addNode(false, false, false, false, true, false, false);
        addNode(true, true, false, false, false, false, false);
        addNode(true, true, true, true, true, false, false);

        assertEquals(localNodesDirectory.getKnownNodes().size(), 8);
        assertEquals(localNodesDirectory.getNode(guid), node);

        assertEquals(localNodesDirectory.getNodes(Node::isStorage, LocalNodesDirectory.NO_LIMIT).size(), 3);
        assertEquals(localNodesDirectory.getNodes(Node::isDDS, LocalNodesDirectory.NO_LIMIT).size(), 2);
        assertEquals(localNodesDirectory.getNodes(Node::isNDS, LocalNodesDirectory.NO_LIMIT).size(), 2);
        assertEquals(localNodesDirectory.getNodes(Node::isMMS, LocalNodesDirectory.NO_LIMIT).size(), 2);
    }

    @Test
    public void persistTest() throws GUIDGenerationException, NodesDirectoryException {
        IGUID guid = GUIDFactory.generateRandomGUID();
        Node node = new SOSNode(guid, "example.com", 8080, true, false, false, false, false, false, false);

        assertEquals(localNodesDirectory.getKnownNodes().size(), 0);

        localNodesDirectory.addNode(node);
        assertEquals(localNodesDirectory.getKnownNodes().size(), 1);

        localNodesDirectory.persistNodesTable();
        assertEquals(localNodesDirectory.getKnownNodes().size(), 1);
    }

    @Test
    public void getStorageNodesWithLimitTest() {
        IGUID guid = GUIDFactory.generateRandomGUID();
        Node node = new SOSNode(guid, "example.com", 8080, true, false, false, false, false, false, false);
        localNodesDirectory.addNode(node);

        addNode(true, false, false, false, false, false, false);
        addNode(false, true, false, false, false, false, false);
        addNode(false, false, true, false, false, false, false);
        addNode(false, false, false, true, false, false, false);
        addNode(false, false, false, false, true, false, false);
        addNode(true, true, false, false, false, false, false);
        addNode(true, true, true, true, true, false, false);

        Set<Node> storageNodes = localNodesDirectory.getNodes(Node::isStorage, 3);
        assertEquals(storageNodes.size(), 3);
    }

    @Test
    public void getStorageNodesWithLimitCapTest() {
        IGUID guid = GUIDFactory.generateRandomGUID();
        Node node = new SOSNode(guid, "example.com", 8080, true, false, false, false, false, false, false);
        localNodesDirectory.addNode(node);

        addNode(true, false, false, false, false, false, false);
        addNode(false, true, false, false, false, false, false);
        addNode(false, false, true, false, false, false, false);
        addNode(false, false, false, true, false, false, false);
        addNode(false, false, false, false, true, false, false);
        addNode(true, true, false, false, false, false, false);
        addNode(true, true, true, true, true, false, false);

        Set<Node> storageNodes = localNodesDirectory.getNodes(Node::isStorage, 2);
        assertEquals(storageNodes.size(), 2);
    }

    @Test
    public void getStorageNodesWithLimitInExcessTest() {
        IGUID guid = GUIDFactory.generateRandomGUID();
        Node node = new SOSNode(guid, "example.com", 8080, true, false, false, false, false, false, false);
        localNodesDirectory.addNode(node);

        addNode(true, false, false, false, false, false, false);
        addNode(false, true, false, false, false, false, false);
        addNode(false, false, true, false, false, false, false);
        addNode(false, false, false, true, false, false, false);
        addNode(false, false, false, false, true, false, false);
        addNode(true, true, false, false, false, false, false);
        addNode(true, true, true, true, true, false, false);

        Set<Node> storageNodes = localNodesDirectory.getNodes(Node::isStorage, 10);
        assertEquals(storageNodes.size(), 3);
    }

    @Test
    public void getStorageNodesIgnoreNegativeLimitTest() {
        IGUID guid = GUIDFactory.generateRandomGUID();
        Node node = new SOSNode(guid, "example.com", 8080, true, false, false, false, false, false, false);
        localNodesDirectory.addNode(node);

        addNode(true, false, false, false, false, false, false);
        addNode(false, true, false, false, false, false, false);
        addNode(false, false, true, false, false, false, false);
        addNode(false, false, false, true, false, false, false);
        addNode(false, false, false, false, true, false, false);
        addNode(true, true, false, false, false, false, false);
        addNode(true, true, true, true, true, false, false);

        Set<Node> storageNodes = localNodesDirectory.getNodes(Node::isStorage, -1);
        assertEquals(storageNodes.size(), 3);
    }

    @Test
    public void nodeIsUpdatedTest() throws NodesDirectoryException {
        IGUID guid = GUIDFactory.generateRandomGUID();
        Node node = new SOSNode(guid, "example.com", 8080, true, false, false, false, false, false, false);

        localNodesDirectory.addNode(node);
        Node retrievedNode = localNodesDirectory.getNode(guid);
        assertEquals(retrievedNode, node);

        // Now isStorage node too
        Node updatedNode = new SOSNode(guid, "example.com", 8080, true, true, false, false, false, false, false);
        localNodesDirectory.addNode(updatedNode);

        Node retrievedUpdatedNode = localNodesDirectory.getNode(guid);
        assertEquals(retrievedUpdatedNode, updatedNode);
        assertEquals(retrievedUpdatedNode.isStorage(), true);
    }

    private void addNode(IGUID guid, boolean isClient, boolean isStorage, boolean isDDS, boolean isNDS, boolean isMCS, boolean isCMS, boolean isRMS) {
        localNodesDirectory.addNode(new SOSNode(guid, "example.com", 8080,
                isClient, isStorage, isDDS,
                isNDS, isMCS, isCMS, isRMS));
    }

    private void addNode(boolean isClient, boolean isStorage, boolean isDDS, boolean isNDS, boolean isMCS, boolean isCMS, boolean isRMS) {
        IGUID guid = GUIDFactory.generateRandomGUID();
        addNode(guid, isClient, isStorage, isDDS, isNDS, isMCS, isCMS, isRMS);
    }
}
