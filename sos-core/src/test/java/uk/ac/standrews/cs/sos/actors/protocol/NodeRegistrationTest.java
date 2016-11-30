package uk.ac.standrews.cs.sos.actors.protocol;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.configuration.SOSConfiguration;
import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.exceptions.db.DatabaseException;
import uk.ac.standrews.cs.sos.exceptions.node.NodeRegistrationException;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.interfaces.node.NodesDatabase;
import uk.ac.standrews.cs.sos.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.node.directory.LocalNodesDirectory;
import uk.ac.standrews.cs.sos.node.directory.database.DatabaseTypes;
import uk.ac.standrews.cs.sos.node.directory.database.SQLDatabase;
import uk.ac.standrews.cs.sos.utils.HelperTest;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class NodeRegistrationTest {

    private LocalNodesDirectory localNodesDirectory;
    private SOSConfiguration configurationMock = mock(SOSConfiguration.class);
    private IGUID localNodeGUID = GUIDFactory.generateRandomGUID();

    @BeforeMethod
    public void setUp(Method testMethod) throws Exception {

        when(configurationMock.getDBType()).thenReturn(DatabaseTypes.SQLITE_DB);
        when(configurationMock.getDBPath()).thenReturn(System.getProperty("user.home") + "/sos/db/dump.db");

        // Make sure that the DB path is clean
        HelperTest.DeletePath(configurationMock.getDBPath());

        NodesDatabase nodesDatabase;
        try {
            nodesDatabase = new SQLDatabase(configurationMock.getDBType(), configurationMock.getDBPath());
        } catch (DatabaseException e) {
            throw new SOSException(e);
        }

        Node localNode = mock(SOSLocalNode.class);
        when(localNode.getNodeGUID()).thenReturn(localNodeGUID);
        localNodesDirectory = new LocalNodesDirectory(localNode, nodesDatabase);
    }

    @Test
    public void basicRegistrationTest() throws NodeRegistrationException {
        NodeRegistration nodeRegistration = new NodeRegistration(localNodesDirectory);

        Node nodeMock = makeMockNode();
        Node registeredNode = nodeRegistration.registerNode(nodeMock);
        assertNotNull(registeredNode);
        assertEquals(registeredNode, nodeMock);
    }

    @Test (expectedExceptions = NodeRegistrationException.class)
    public void registrationFailsTest() throws NodeRegistrationException {
        NodeRegistration nodeRegistration = new NodeRegistration(localNodesDirectory);

        nodeRegistration.registerNode(null);
    }

    private Node makeMockNode() {
        Node nodeMock = mock(Node.class);

        when(nodeMock.getHostAddress()).thenReturn(new InetSocketAddress("localhost", 8090));
        when(nodeMock.getNodeGUID()).thenReturn(GUIDFactory.generateRandomGUID());
        when(nodeMock.isAgent()).thenReturn(true);
        when(nodeMock.isNDS()).thenReturn(true);
        when(nodeMock.isDDS()).thenReturn(true);
        when(nodeMock.isMCS()).thenReturn(true);
        when(nodeMock.isStorage()).thenReturn(true);

        return nodeMock;
    }
}