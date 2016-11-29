package uk.ac.standrews.cs.sos.actors.protocol;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.configuration.SOSConfiguration;
import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.exceptions.db.DatabaseException;
import uk.ac.standrews.cs.sos.exceptions.node.NodeNotFoundException;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.interfaces.node.NodesDatabase;
import uk.ac.standrews.cs.sos.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.node.directory.LocalNodesDirectory;
import uk.ac.standrews.cs.sos.node.directory.database.DatabaseTypes;
import uk.ac.standrews.cs.sos.node.directory.database.SQLDatabase;
import uk.ac.standrews.cs.sos.utils.HelperTest;

import java.lang.reflect.Method;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class NodeDiscoveryTest {

    LocalNodesDirectory localNodesDirectory;
    SOSConfiguration configurationMock = mock(SOSConfiguration.class);
    Node localNode;
    IGUID localNodeGUID = GUIDFactory.generateRandomGUID();

    @BeforeMethod
    public void setUp(Method testMethod) throws Exception {

        when(configurationMock.getDBType()).thenReturn(DatabaseTypes.SQLITE_DB);
        when(configurationMock.getDBPath()).thenReturn(System.getProperty("user.home") + "/sos/db/dump.db");

        // Make sure that the DB path is clean
        HelperTest.DeletePath(configurationMock.getDBPath());

        NodesDatabase nodesDatabase;
        try {
            nodesDatabase = new SQLDatabase(configurationMock.getDBType(),
                    configurationMock.getDBPath());
        } catch (DatabaseException e) {
            throw new SOSException(e);
        }

        localNode = mock(SOSLocalNode.class);
        when(localNode.getNodeGUID()).thenReturn(localNodeGUID);

        localNodesDirectory = new LocalNodesDirectory(localNode, nodesDatabase);
    }

    @Test
    public void basicNodeDiscoveryTest() throws NodeNotFoundException {
        NodeDiscovery nodeDiscovery = new NodeDiscovery(localNodesDirectory);

        Node node = nodeDiscovery.findNode(localNodeGUID);
        assertEquals(node, localNode);
    }

    @Test (expectedExceptions = NodeNotFoundException.class)
    public void findNullNodeTest() throws NodeNotFoundException {
        NodeDiscovery nodeDiscovery = new NodeDiscovery(localNodesDirectory);
        nodeDiscovery.findNode(null);
    }

    @Test (expectedExceptions = NodeNotFoundException.class)
    public void findUnknownNodeTest() throws NodeNotFoundException {
        NodeDiscovery nodeDiscovery = new NodeDiscovery(localNodesDirectory);
        Node node = nodeDiscovery.findNode(GUIDFactory.generateRandomGUID());
        System.out.println(node.toString());
    }
}