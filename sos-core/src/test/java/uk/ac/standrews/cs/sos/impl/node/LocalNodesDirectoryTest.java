package uk.ac.standrews.cs.sos.impl.node;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.castore.interfaces.IFile;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.SetUpTest;
import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.exceptions.db.DatabaseException;
import uk.ac.standrews.cs.sos.exceptions.node.NodesDirectoryException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.database.DatabaseFactory;
import uk.ac.standrews.cs.sos.impl.database.DatabaseType;
import uk.ac.standrews.cs.sos.interfaces.database.NodesDatabase;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.utilities.crypto.CryptoException;
import uk.ac.standrews.cs.utilities.crypto.DigitalSignature;

import java.io.IOException;
import java.lang.reflect.Method;
import java.security.PublicKey;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static uk.ac.standrews.cs.sos.constants.Internals.DB_FILE;
import static uk.ac.standrews.cs.sos.constants.Internals.GUID_ALGORITHM;
import static uk.ac.standrews.cs.sos.impl.services.SOSNodeDiscoveryService.NO_LIMIT;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class LocalNodesDirectoryTest extends SetUpTest {

    private LocalNodesDirectory localNodesDirectory;
    private Node testNode;
    private PublicKey mockSignatureCertificate;

    @BeforeMethod
    public void setUp(Method testMethod) throws Exception {
        super.setUp(testMethod);

        // Make sure that the DB path is clean
        localStorage.getNodeDirectory().remove(DB_FILE);

        NodesDatabase nodesDatabase;
        try {
            IFile dbFile = localStorage.createFile(localStorage.getNodeDirectory(), DB_FILE);
            DatabaseFactory.initInstance(dbFile);
            nodesDatabase = (NodesDatabase) DatabaseFactory.instance().getDatabase(DatabaseType.NODES);
        } catch (DatabaseException e) {
            throw new SOSException(e);
        }

        testNode = mock(SOSLocalNode.class);
        when(testNode.guid()).thenReturn(GUIDFactory.generateRandomGUID(GUID_ALGORITHM));
        localNodesDirectory = new LocalNodesDirectory(testNode, nodesDatabase);

        try {
            mockSignatureCertificate = DigitalSignature.generateKeys().getPublic();
        } catch (CryptoException e) {
            throw new Exception();
        }
    }

    @AfterMethod
    public void tearDown() throws InterruptedException, DataStorageException, IOException {
        super.tearDown();

        DatabaseFactory.kill();
    }

    @Test
    public void getLocalNodeTest() {
        Node localNode = localNodesDirectory.getLocalNode();
        assertEquals(localNode, testNode);
    }

    @Test
    public void basicAddGetNodeTest() {
        IGUID guid = GUIDFactory.generateRandomGUID(GUID_ALGORITHM);
        Node node = new SOSNode(guid, mockSignatureCertificate, "example.com", 8080, true, false, false, false, false, false, false, false);

        localNodesDirectory.addNode(node);
        Node retrievedNode = localNodesDirectory.getNode(guid);
        assertEquals(retrievedNode, node);
    }

    @Test
    public void persistMultipleNodesTest() {

        Set<IGUID> nodes = localNodesDirectory.getNodes(p -> true, NO_LIMIT);
        assertEquals(nodes.size(), 0);

        IGUID guid = GUIDFactory.generateRandomGUID(GUID_ALGORITHM);
        Node node = new SOSNode(guid, mockSignatureCertificate, "example.com", 8080, true, false, false, false, false, false, false, false);
        localNodesDirectory.addNode(node);

        addNode(true, false, false, false, false, false, false, false);
        addNode(false, true, false, false, false, false, false, false);
        addNode(false, false, true, false, false, false, false, false);
        addNode(false, false, false, true, false, false, false, false);
        addNode(false, false, false, false, true, false, false, false);
        addNode(true, true, false, false, false, false, false, false);
        addNode(true, true, true, true, true, false, false, false);

        assertEquals(localNodesDirectory.getNodes(p -> true, NO_LIMIT).size(), 8);
        assertEquals(localNodesDirectory.getNode(guid), node);

        assertEquals(localNodesDirectory.getNodes(Node::isStorage, NO_LIMIT).size(), 3);
        assertEquals(localNodesDirectory.getNodes(Node::isMDS, NO_LIMIT).size(), 2);
        assertEquals(localNodesDirectory.getNodes(Node::isNDS, NO_LIMIT).size(), 2);
        assertEquals(localNodesDirectory.getNodes(Node::isMMS, NO_LIMIT).size(), 2);
    }

    @Test
    public void persistTest() throws NodesDirectoryException {
        IGUID guid = GUIDFactory.generateRandomGUID(GUID_ALGORITHM);
        Node node = new SOSNode(guid, mockSignatureCertificate,"example.com", 8080, true, false, false, false, false, false, false, false);

        assertEquals(localNodesDirectory.getNodes(p -> true, NO_LIMIT).size(), 0);

        localNodesDirectory.addNode(node);
        assertEquals(localNodesDirectory.getNodes(p -> true, NO_LIMIT).size(), 1);

        localNodesDirectory.persistNodesTable();
        assertEquals(localNodesDirectory.getNodes(p -> true, NO_LIMIT).size(), 1);
    }

    @Test
    public void getStorageNodesWithLimitTest() {
        IGUID guid = GUIDFactory.generateRandomGUID(GUID_ALGORITHM);
        Node node = new SOSNode(guid, mockSignatureCertificate,"example.com", 8080, true, false, false, false, false, false, false, false);
        localNodesDirectory.addNode(node);

        addNode(true, false, false, false, false, false, false, false);
        addNode(false, true, false, false, false, false, false, false);
        addNode(false, false, true, false, false, false, false, false);
        addNode(false, false, false, true, false, false, false, false);
        addNode(false, false, false, false, true, false, false, false);
        addNode(true, true, false, false, false, false, false, false);
        addNode(true, true, true, true, true, false, false, false);

        Set<IGUID> storageNodes = localNodesDirectory.getNodes(Node::isStorage, 3);
        assertEquals(storageNodes.size(), 3);
    }

    @Test
    public void getStorageNodesWithLimitCapTest() {
        IGUID guid = GUIDFactory.generateRandomGUID(GUID_ALGORITHM);
        Node node = new SOSNode(guid, mockSignatureCertificate,"example.com", 8080, true, false, false, false, false, false, false, false);
        localNodesDirectory.addNode(node);

        addNode(true, false, false, false, false, false, false, false);
        addNode(false, true, false, false, false, false, false, false);
        addNode(false, false, true, false, false, false, false, false);
        addNode(false, false, false, true, false, false, false, false);
        addNode(false, false, false, false, true, false, false, false);
        addNode(true, true, false, false, false, false, false, false);
        addNode(true, true, true, true, true, false, false, false);

        Set<IGUID> storageNodes = localNodesDirectory.getNodes(Node::isStorage, 2);
        assertEquals(storageNodes.size(), 2);
    }

    @Test
    public void getStorageNodesWithLimitInExcessTest() {
        IGUID guid = GUIDFactory.generateRandomGUID(GUID_ALGORITHM);
        Node node = new SOSNode(guid, mockSignatureCertificate,"example.com", 8080, true, false, false, false, false, false, false, false);
        localNodesDirectory.addNode(node);

        addNode(true, false, false, false, false, false, false, false);
        addNode(false, true, false, false, false, false, false, false);
        addNode(false, false, true, false, false, false, false, false);
        addNode(false, false, false, true, false, false, false, false);
        addNode(false, false, false, false, true, false, false, false);
        addNode(true, true, false, false, false, false, false, false);
        addNode(true, true, true, true, true, false, false, false);

        Set<IGUID> storageNodes = localNodesDirectory.getNodes(Node::isStorage, 10);
        assertEquals(storageNodes.size(), 3);
    }

    @Test
    public void getStorageNodesIgnoreNegativeLimitTest() {
        IGUID guid = GUIDFactory.generateRandomGUID(GUID_ALGORITHM);
        Node node = new SOSNode(guid, mockSignatureCertificate,"example.com", 8080, true, false, false, false, false, false, false, false);
        localNodesDirectory.addNode(node);

        addNode(true, false, false, false, false, false, false, false);
        addNode(false, true, false, false, false, false, false, false);
        addNode(false, false, true, false, false, false, false, false);
        addNode(false, false, false, true, false, false, false, false);
        addNode(false, false, false, false, true, false, false, false);
        addNode(true, true, false, false, false, false, false, false);
        addNode(true, true, true, true, true, false, false, false);

        Set<IGUID> storageNodes = localNodesDirectory.getNodes(Node::isStorage, -1);
        assertEquals(storageNodes.size(), 3);
    }

    @Test
    public void nodeIsUpdatedTest() {
        IGUID guid = GUIDFactory.generateRandomGUID(GUID_ALGORITHM);
        Node node = new SOSNode(guid, mockSignatureCertificate,"example.com", 8080, true, false, false, false, false, false, false, false);

        localNodesDirectory.addNode(node);
        Node retrievedNode = localNodesDirectory.getNode(guid);
        assertEquals(retrievedNode, node);

        // Now isStorage node too
        Node updatedNode = new SOSNode(guid, mockSignatureCertificate,"example.com", 8080, true, true, false, false, false, false, false, false);
        localNodesDirectory.addNode(updatedNode);

        Node retrievedUpdatedNode = localNodesDirectory.getNode(guid);
        assertEquals(retrievedUpdatedNode, updatedNode);
        assertEquals(retrievedUpdatedNode.isStorage(), true);
    }

    private void addNode(IGUID guid, boolean isClient, boolean isStorage, boolean isMDS, boolean isNDS, boolean isMCS, boolean isCMS, boolean isRMS, boolean isExperiment) {
        localNodesDirectory.addNode(new SOSNode(guid, mockSignatureCertificate,"example.com", 8080,
                isClient, isStorage, isMDS,
                isNDS, isMCS, isCMS, isRMS, isExperiment));
    }

    private void addNode(boolean isClient, boolean isStorage, boolean isMDS, boolean isNDS, boolean isMCS, boolean isCMS, boolean isRMS, boolean isExperiment) {
        IGUID guid = GUIDFactory.generateRandomGUID(GUID_ALGORITHM);
        addNode(guid, isClient, isStorage, isMDS, isNDS, isMCS, isCMS, isRMS, isExperiment);
    }
}
