package uk.ac.standrews.cs.sos.actors.protocol;

import org.mockserver.integration.ClientAndServer;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.configuration.SOSConfiguration;
import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.exceptions.db.DatabaseException;
import uk.ac.standrews.cs.sos.exceptions.node.NodeNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.interfaces.node.NodesDatabase;
import uk.ac.standrews.cs.sos.model.locations.sos.SOSURLProtocol;
import uk.ac.standrews.cs.sos.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.node.directory.LocalNodesDirectory;
import uk.ac.standrews.cs.sos.node.directory.database.DatabaseTypes;
import uk.ac.standrews.cs.sos.node.directory.database.SQLDatabase;
import uk.ac.standrews.cs.sos.utils.HelperTest;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class NodeDiscoveryTest {

    private LocalNodesDirectory localNodesDirectory;
    private SOSConfiguration configurationMock = mock(SOSConfiguration.class);
    private Node localNode;
    private IGUID localNodeGUID = GUIDFactory.generateRandomGUID();

    private ClientAndServer mockServer;

    private final static String NODE_HOSTNAME = "localhost";
    private final static int NODE_PORT = 12345;

    private IGUID nodeFound;
    private IGUID nodeNotFound;

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


        // MOCK SERVER SETUP
        nodeFound = GUIDFactory.generateRandomGUID();
        nodeNotFound = GUIDFactory.generateRandomGUID();

        mockServer = startClientAndServer(NODE_PORT);
        mockServer.dumpToLog();
        mockServer
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/nds/guid/" + nodeFound.toString())
                )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withBody(
                                        "{\n" +
                                                "    \"" + SOSConstants.GUID +"\": \"" +  nodeFound.toString() + "\",\n" +
                                                "    \"" + SOSConstants.HOSTNAME + "\": \"localhost\",\n" +
                                                "    \"" + SOSConstants.PORT + "\": 12345\n" +
                                                "}"
                                )
                );

        mockServer
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/nds/guid/" + nodeNotFound.toString())
                )
                .respond(
                        response()
                                .withStatusCode(400)
                );

        SOSURLProtocol.getInstance().register(null); // Local storage is not needed for this set of tests
    }

    @AfterMethod
    public void tearDown() {
        mockServer.stop();
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
        nodeDiscovery.findNode(GUIDFactory.generateRandomGUID());
    }

    @Test
    public void attemptToContactNDSNodeTest() throws NodeNotFoundException, SOSProtocolException {

        Node ndsMock = mock(Node.class);
        when(ndsMock.getNodeGUID()).thenReturn(GUIDFactory.generateRandomGUID());
        when(ndsMock.getHostAddress()).thenReturn(new InetSocketAddress(NODE_HOSTNAME, NODE_PORT));
        when(ndsMock.isNDS()).thenReturn(true);
        localNodesDirectory.addNode(ndsMock);

        NodeDiscovery nodeDiscovery = new NodeDiscovery(localNodesDirectory);
        Node node = nodeDiscovery.findNode(nodeFound);

        assertNotNull(node);
    }

    @Test (expectedExceptions = NodeNotFoundException.class)
    public void attemptToContactNDSNodeFailsTest() throws NodeNotFoundException, SOSProtocolException {

        Node ndsMock = mock(Node.class);
        when(ndsMock.getNodeGUID()).thenReturn(GUIDFactory.generateRandomGUID());
        when(ndsMock.getHostAddress()).thenReturn(new InetSocketAddress(NODE_HOSTNAME, NODE_PORT));
        when(ndsMock.isNDS()).thenReturn(true);
        localNodesDirectory.addNode(ndsMock);

        NodeDiscovery nodeDiscovery = new NodeDiscovery(localNodesDirectory);
        nodeDiscovery.findNode(nodeNotFound);
    }
}