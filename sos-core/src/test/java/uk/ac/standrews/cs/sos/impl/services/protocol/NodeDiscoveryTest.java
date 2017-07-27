package uk.ac.standrews.cs.sos.impl.services.protocol;

import org.mockserver.integration.ClientAndServer;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.SettingsConfiguration;
import uk.ac.standrews.cs.sos.constants.SOSConstants;
import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.exceptions.db.DatabaseException;
import uk.ac.standrews.cs.sos.exceptions.node.NodeNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.node.NodeRegistrationException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.impl.locations.sos.SOSURLProtocol;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.impl.node.directory.DatabaseImpl;
import uk.ac.standrews.cs.sos.impl.services.SOSNodeDiscoveryService;
import uk.ac.standrews.cs.sos.interfaces.node.Database;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.utils.HelperTest;

import java.io.File;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static uk.ac.standrews.cs.sos.constants.Paths.TEST_RESOURCES_PATH;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class NodeDiscoveryTest {

    private SOSNodeDiscoveryService nds;
    private Node localNode;
    private IGUID localNodeGUID = GUIDFactory.generateRandomGUID();

    private ClientAndServer mockServer;

    private final static String NODE_HOSTNAME = "localhost";
    private final static int NODE_PORT = 12345;

    private IGUID nodeFound;
    private IGUID nodeNotFound;

    @BeforeMethod
    public void setUp(Method testMethod) throws Exception {

        SettingsConfiguration.Settings settings = new SettingsConfiguration(new File(TEST_RESOURCES_PATH + "configurations/node_discovery_test.json")).getSettingsObj();

        // Make sure that the DB path is clean
        HelperTest.DeletePath(settings.getDatabase().getFilename());

        Database database;
        try {
            database = new DatabaseImpl(settings.getDatabase().getFilename());
        } catch (DatabaseException e) {
            throw new SOSException(e);
        }

        localNode = mock(SOSLocalNode.class);
        when(localNode.getNodeGUID()).thenReturn(localNodeGUID);
        nds = new SOSNodeDiscoveryService(localNode, database);

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

        SOSURLProtocol.getInstance().register(null, null); // Local storage is not needed for this set of tests
    }

    @AfterMethod
    public void tearDown() {
        mockServer.stop();
    }

    @Test
    public void basicNodeDiscoveryTest() throws NodeNotFoundException {

        Node node = nds.getNode(localNodeGUID);
        assertEquals(node, localNode);
    }

    @Test (expectedExceptions = NodeNotFoundException.class)
    public void findNullNodeTest() throws NodeNotFoundException {

        nds.getNode(null);
    }

    @Test (expectedExceptions = NodeNotFoundException.class)
    public void findUnknownNodeTest() throws NodeNotFoundException {

        nds.getNode(GUIDFactory.generateRandomGUID());
    }

    @Test
    public void attemptToContactNDSNodeTest() throws NodeNotFoundException, SOSProtocolException, NodeRegistrationException {

        Node ndsMock = mock(Node.class);
        when(ndsMock.getNodeGUID()).thenReturn(GUIDFactory.generateRandomGUID());
        when(ndsMock.getHostAddress()).thenReturn(new InetSocketAddress(NODE_HOSTNAME, NODE_PORT));
        when(ndsMock.isNDS()).thenReturn(true);
        nds.registerNode(ndsMock, true);

        Node node = nds.getNode(nodeFound);

        assertNotNull(node);
    }

    @Test (expectedExceptions = NodeNotFoundException.class)
    public void attemptToContactNDSNodeFailsTest() throws NodeNotFoundException, SOSProtocolException, NodeRegistrationException {

        Node ndsMock = mock(Node.class);
        when(ndsMock.getNodeGUID()).thenReturn(GUIDFactory.generateRandomGUID());
        when(ndsMock.getHostAddress()).thenReturn(new InetSocketAddress(NODE_HOSTNAME, NODE_PORT));
        when(ndsMock.isNDS()).thenReturn(true);
        nds.registerNode(ndsMock, true);

        nds.getNode(nodeNotFound);
    }
}