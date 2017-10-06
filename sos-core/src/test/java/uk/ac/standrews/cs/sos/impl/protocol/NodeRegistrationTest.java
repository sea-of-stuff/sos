package uk.ac.standrews.cs.sos.impl.protocol;

import org.mockserver.integration.ClientAndServer;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.SettingsConfiguration;
import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.exceptions.db.DatabaseException;
import uk.ac.standrews.cs.sos.exceptions.node.NodeRegistrationException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.impl.database.DatabaseFactory;
import uk.ac.standrews.cs.sos.impl.database.DatabaseType;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.sos.SOSURLProtocol;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.impl.node.SOSNode;
import uk.ac.standrews.cs.sos.impl.services.SOSNodeDiscoveryService;
import uk.ac.standrews.cs.sos.interfaces.database.NodesDatabase;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.utils.HelperTest;
import uk.ac.standrews.cs.utilities.crypto.CryptoException;

import java.io.File;
import java.io.IOException;

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
public class NodeRegistrationTest extends ProtocolTest {

    private SOSNodeDiscoveryService nds;
    private static IGUID localNodeGUID = GUIDFactory.generateRandomGUID();

    private ClientAndServer mockServer;
    private static final int MOCK_SERVER_PORT = 10007;

    private static final String TEST_DATA =
            "{" +
                    "    \"guid\": \"" + localNodeGUID.toMultiHash()  + "\"," +
                    "    \"hostname\": \"localhost\"," +
                    "    \"port\": 8080," +
                    "    \"services\": {" +
                    "        \"storage\": {" +
                    "            \"exposed\": true" +
                    "        }," +
                    "        \"cms\": {" +
                    "            \"exposed\": true" +
                    "        }," +
                    "        \"dds\": {" +
                    "            \"exposed\": true" +
                    "        }," +
                    "        \"nds\": {" +
                    "            \"exposed\": true" +
                    "        }," +
                    "        \"rms\": {" +
                    "            \"exposed\": true" +
                    "        }," +
                    "        \"mms\": {" +
                    "            \"exposed\": true" +
                    "        }" +
                    "    }" +
                    "}";

    private static final String TEST_DATA_FAIL =
            "{" +
                    "    \"guid\": \"" + localNodeGUID.toMultiHash()  + "\"," +
                    "    \"hostname\": \"localhost\"," +
                    "    \"port\": 8081," +
                    "    \"services\": {" +
                    "        \"storage\": {" +
                    "            \"exposed\": true" +
                    "        }," +
                    "        \"cms\": {" +
                    "            \"exposed\": true" +
                    "        }," +
                    "        \"dds\": {" +
                    "            \"exposed\": true" +
                    "        }," +
                    "        \"nds\": {" +
                    "            \"exposed\": true" +
                    "        }," +
                    "        \"rms\": {" +
                    "            \"exposed\": true" +
                    "        }," +
                    "        \"mms\": {" +
                    "            \"exposed\": true" +
                    "        }" +
                    "    }" +
                    "}";

    @BeforeMethod
    public void setUp() throws SOSProtocolException, ConfigurationException, IOException, SOSException, CryptoException, GUIDGenerationException {
        super.setUp();

        SettingsConfiguration.Settings settings = new SettingsConfiguration(new File(TEST_RESOURCES_PATH + "configurations/node_registration_test.json")).getSettingsObj();
        SOSLocalNode.settings = settings;

        // Make sure that the DB path is clean
        HelperTest.DeletePath(settings.getDatabase().getFilename());

        NodesDatabase nodesDatabase;
        try {
            DatabaseFactory.initInstance(settings.getDatabase().getFilename());
            nodesDatabase = (NodesDatabase) DatabaseFactory.instance().getDatabase(DatabaseType.NODES);
        } catch (DatabaseException e) {
            throw new SOSException(e);
        }

        Node localNode = mock(SOSLocalNode.class);
        when(localNode.getNodeGUID()).thenReturn(localNodeGUID);

        nds = new SOSNodeDiscoveryService(localNode, nodesDatabase);

        mockServer = startClientAndServer(MOCK_SERVER_PORT);
        mockServer.dumpToLog();
        mockServer
                .when(
                        request()
                                .withMethod("POST")
                                .withPath("/sos/nds/register")
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
                                .withPath("/sos/nds/register")
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

        DatabaseFactory.kill();

        mockServer.stop();
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

        Node nodeMock = new SOSNode(localNodeGUID, mockSignatureCertificate, "localhost", 8080, true, true, false, false, false, false, false);
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

        Node nodeMock = new SOSNode(localNodeGUID, mockSignatureCertificate, "localhost", 8081, true, true, false, false, false, false, false);
        Node registeredNode = nds.registerNode(nodeMock, false);
        assertNotNull(registeredNode);
        assertEquals(registeredNode, nodeMock);
    }

    private Node makeMockNode() {
        return new SOSNode(GUIDFactory.generateRandomGUID(), mockSignatureCertificate, "localhost", 8090, true, true, true, true, true, true, true);
    }
}