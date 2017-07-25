package uk.ac.standrews.cs.sos.impl.services.protocol;

import org.mockserver.integration.ClientAndServer;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.SettingsConfiguration;
import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.exceptions.db.DatabaseException;
import uk.ac.standrews.cs.sos.exceptions.node.NodeRegistrationException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.impl.locations.sos.SOSURLProtocol;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.impl.node.SOSNode;
import uk.ac.standrews.cs.sos.impl.node.directory.DatabaseImpl;
import uk.ac.standrews.cs.sos.impl.services.SOSNodeDiscoveryService;
import uk.ac.standrews.cs.sos.interfaces.node.Database;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.utils.HelperTest;

import java.io.File;
import java.lang.reflect.Method;

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
public class NodeRegistrationTest {

    private SOSNodeDiscoveryService nds;
    private static IGUID localNodeGUID = GUIDFactory.generateRandomGUID();

    private ClientAndServer mockServer;
    private static final int MOCK_SERVER_PORT = 10007;

    private static final String TEST_DATA =
            "{\n" +
                    "\t\"guid\": \"" + localNodeGUID.toString() + "\",\n" +
                    "\t\"hostname\": \"localhost\",\n" +
                    "\t\"port\": 8080,\n" +
                    "\t\"roles\": {\n" +
                    "\t\t\"agent\": true,\n" +
                    "\t\t\"storage\": true,\n" +
                    "\t\t\"dds\": false,\n" +
                    "\t\t\"nds\": false,\n" +
                    "\t\t\"mms\": false,\n" +
                    "\t\t\"cms\": false,\n" +
                    "\t\t\"rms\": false\n" +
                    "\t}\n" +
                    "}";

    private static final String TEST_DATA_FAIL =
            "{\n" +
                    "\t\"guid\": \"" + localNodeGUID.toString() + "\",\n" +
                    "\t\"hostname\": \"localhost\",\n" +
                    "\t\"port\": 8081,\n" +
                    "\t\"roles\": {\n" +
                    "\t\t\"agent\": true,\n" +
                    "\t\t\"storage\": true,\n" +
                    "\t\t\"dds\": false,\n" +
                    "\t\t\"nds\": false,\n" +
                    "\t\t\"mms\": false,\n" +
                    "\t\t\"cms\": false,\n" +
                    "\t\t\"rms\": false\n" +
                    "\t}\n" +
                    "}";


    @BeforeMethod
    public void setUp() throws SOSProtocolException, GUIDGenerationException {

        mockServer = startClientAndServer(MOCK_SERVER_PORT);
        mockServer.dumpToLog();
        mockServer
                .when(
                        request()
                                .withMethod("POST")
                                .withPath("/nds/register")
                                .withBody(TEST_DATA)
                )
                .respond(
                        response()
                                .withStatusCode(200)
                );

        mockServer
                .when(
                        request()
                                .withMethod("POST")
                                .withPath("/nds/register")
                                .withBody(TEST_DATA_FAIL)
                )
                .respond(
                        response()
                                .withStatusCode(500)
                );

        SOSURLProtocol.getInstance().register(null, null); // Local storage is not needed for this set of tests
    }

    @AfterMethod
    public void tearDown() {
        mockServer.stop();
    }

    @BeforeMethod
    public void setUp(Method testMethod) throws Exception {

        SettingsConfiguration.Settings settings = new SettingsConfiguration(new File(TEST_RESOURCES_PATH + "configurations/node_registration_test.json")).getSettingsObj();

        // Make sure that the DB path is clean
        HelperTest.DeletePath(settings.getDatabase().getFilename());

        Database database;
        try {
            database = new DatabaseImpl(settings.getDatabase().getFilename());
        } catch (DatabaseException e) {
            throw new SOSException(e);
        }

        Node localNode = mock(SOSLocalNode.class);
        when(localNode.getNodeGUID()).thenReturn(localNodeGUID);

        nds = new SOSNodeDiscoveryService(localNode, database);
    }

    @Test
    public void basicRegistrationTest() throws NodeRegistrationException {

        Node nodeMock = makeMockNode();
        Node registeredNode = nds.registerNode(nodeMock, true);
        assertNotNull(registeredNode);
        assertEquals(registeredNode, nodeMock);
    }

    @Test (expectedExceptions = NodeRegistrationException.class)
    public void registrationFailsTest() throws NodeRegistrationException {

        nds.registerNode(null, true);
    }

    @Test
    public void registerToNDSTest() throws NodeRegistrationException {

        Node nodeMock = new SOSNode(localNodeGUID, "localhost", 8080, true, true, false, false, false, false, false);
        Node registeredNode = nds.registerNode(nodeMock, false);
        assertNotNull(registeredNode);
        assertEquals(registeredNode, nodeMock);
    }

    /**
     * The failure is logged, but nothing is returned to the user.
     * @throws NodeRegistrationException
     */
    @Test
    public void registerToNDSFailsTest() throws NodeRegistrationException {

        Node nodeMock = new SOSNode(localNodeGUID, "localhost", 8081, true, true, false, false, false, false, false);
        Node registeredNode = nds.registerNode(nodeMock, false);
        assertNotNull(registeredNode);
        assertEquals(registeredNode, nodeMock);
    }

    private Node makeMockNode() {
        return new SOSNode(GUIDFactory.generateRandomGUID(), "localhost", 8090, true, true, true, true, true, true, true);
    }
}