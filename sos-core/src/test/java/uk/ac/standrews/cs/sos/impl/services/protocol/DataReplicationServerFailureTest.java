package uk.ac.standrews.cs.sos.impl.services.protocol;

import org.mockserver.integration.ClientAndServer;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.guid.ALGORITHM;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.SetUpTest;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.impl.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.impl.locations.sos.SOSURLProtocol;
import uk.ac.standrews.cs.sos.impl.node.SOSNode;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.protocol.TasksQueue;
import uk.ac.standrews.cs.sos.protocol.tasks.DataReplication;
import uk.ac.standrews.cs.sos.services.DataDiscoveryService;
import uk.ac.standrews.cs.sos.services.NodeDiscoveryService;
import uk.ac.standrews.cs.sos.services.Storage;
import uk.ac.standrews.cs.sos.utils.HelperTest;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.testng.AssertJUnit.assertFalse;

/**
 * These tests will fail because of error responses from server
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class DataReplicationServerFailureTest extends SetUpTest {

    private ClientAndServer mockServer;
    private static final int MOCK_SERVER_PORT = 10004;

    private static final String TEST_DATA = "test-data";
    private static final String TEST_MORE_DATA = "test-more-data";
    private static final String TEST_EMPTY_DATA = " ";
    private static final String TEST_NO_RESPONSE_DATA = "test-no-response-data";
    private static final String TEST_RANDOM_DATA = "test-random-data";

    private NodeDiscoveryService mockNodeDiscoveryService;
    private DataDiscoveryService mockDataDiscoveryService;

    @BeforeMethod
    public void setUp() throws SOSProtocolException, GUIDGenerationException {

        mockServer = startClientAndServer(MOCK_SERVER_PORT);
        mockServer.dumpToLog();
        mockServer
                .when(
                        request()
                                .withMethod("POST")
                                .withPath("/storage/stream")
                                .withBody(TEST_DATA)
                )
                .respond(
                        response()
                                .withStatusCode(500)
                );

        mockServer
                .when(
                        request()
                                .withMethod("POST")
                                .withPath("/storage/stream")
                                .withBody(TEST_MORE_DATA)
                )
                .respond(
                        response()
                                .withStatusCode(400)
                );

        mockServer
                .when(
                        request()
                                .withMethod("POST")
                                .withPath("/storage/stream")
                                .withBody(TEST_EMPTY_DATA)
                )
                .respond(
                        response()
                                .withStatusCode(500)
                );

        mockServer
                .when(
                        request()
                                .withMethod("POST")
                                .withPath("/storage/stream")
                                .withBody(TEST_NO_RESPONSE_DATA)
                )
                .respond(
                        response()
                                .withStatusCode(201)
                                .withBody("")
                );

        SOSURLProtocol.getInstance().register(null, null); // Local storage is not needed for this set of tests
        new SOS_LOG(GUIDFactory.generateRandomGUID(ALGORITHM.SHA256));

        mockNodeDiscoveryService = mock(NodeDiscoveryService.class);
        mockDataDiscoveryService = mock(DataDiscoveryService.class);
    }

    @AfterMethod
    public void tearDown() {
        mockServer.stop();
    }

    @Test
    public void replicationFailsTest() throws IOException, InterruptedException, GUIDGenerationException, SOSProtocolException {
        IGUID testGUID = GUIDFactory.generateGUID(ALGORITHM.SHA256, TEST_DATA);

        InputStream inputStream = HelperTest.StringToInputStream(TEST_DATA);
        Node node = new SOSNode(GUIDFactory.generateRandomGUID(ALGORITHM.SHA256),
                "localhost", MOCK_SERVER_PORT,
                false, true, false, false, false, false, false);

        Set<Node> nodes = new HashSet<>();
        nodes.add(node);

        Storage storage = localSOSNode.getStorage();

        DataReplication replicationTask = new DataReplication(inputStream, nodes.iterator(), 1, storage, mockNodeDiscoveryService, mockDataDiscoveryService);
        TasksQueue.instance().performSyncTask(replicationTask);

        Iterator<LocationBundle> it = storage.findLocations(testGUID);
        assertFalse(it.hasNext());
    }

    @Test
    public void replicationFails400ErrorTest() throws IOException, InterruptedException, GUIDGenerationException, SOSProtocolException {
        IGUID testGUID = GUIDFactory.generateGUID(ALGORITHM.SHA256, TEST_MORE_DATA);

        InputStream inputStream = HelperTest.StringToInputStream(TEST_MORE_DATA);
        Node node = new SOSNode(GUIDFactory.generateRandomGUID(ALGORITHM.SHA256),
                "localhost", MOCK_SERVER_PORT,
                false, true, false, false, false, false, false);

        Set<Node> nodes = new HashSet<>();
        nodes.add(node);

        Storage storage = localSOSNode.getStorage();

        DataReplication replicationTask = new DataReplication(inputStream, nodes.iterator(), 1, storage, mockNodeDiscoveryService, mockDataDiscoveryService);
        TasksQueue.instance().performSyncTask(replicationTask);

        Iterator<LocationBundle> it = storage.findLocations(testGUID);
        assertFalse(it.hasNext());
    }

    @Test
    public void replicationNoDataFailsErrorTest() throws IOException, InterruptedException, GUIDGenerationException, SOSProtocolException {
        IGUID testGUID = GUIDFactory.generateGUID(ALGORITHM.SHA256, TEST_EMPTY_DATA);

        InputStream inputStream = HelperTest.StringToInputStream(TEST_EMPTY_DATA);
        Node node = new SOSNode(GUIDFactory.generateRandomGUID(ALGORITHM.SHA256),
                "localhost", MOCK_SERVER_PORT,
                false, true, false, false, false, false, false);

        Set<Node> nodes = new HashSet<>();
        nodes.add(node);

        Storage storage = localSOSNode.getStorage();

        DataReplication replicationTask = new DataReplication(inputStream, nodes.iterator(), 1, storage, mockNodeDiscoveryService, mockDataDiscoveryService);
        TasksQueue.instance().performSyncTask(replicationTask);

        Iterator<LocationBundle> it = storage.findLocations(testGUID);
        assertFalse(it.hasNext());
    }

    @Test
    public void replicationNoResponseFailsErrorTest() throws IOException, InterruptedException, GUIDGenerationException, SOSProtocolException {
        IGUID testGUID = GUIDFactory.generateGUID(ALGORITHM.SHA256, TEST_NO_RESPONSE_DATA);

        InputStream inputStream = HelperTest.StringToInputStream(TEST_NO_RESPONSE_DATA);
        Node node = new SOSNode(GUIDFactory.generateRandomGUID(ALGORITHM.SHA256),
                "localhost", MOCK_SERVER_PORT,
                false, true, false, false, false, false, false);

        Set<Node> nodes = new HashSet<>();
        nodes.add(node);

        Storage storage = localSOSNode.getStorage();

        DataReplication replicationTask = new DataReplication(inputStream, nodes.iterator(), 1, storage, mockNodeDiscoveryService, mockDataDiscoveryService);
        TasksQueue.instance().performSyncTask(replicationTask);

        Iterator<LocationBundle> it = storage.findLocations(testGUID);
        assertFalse(it.hasNext());
    }

    @Test
    public void badHostnameTest() throws IOException, InterruptedException, GUIDGenerationException, SOSProtocolException {
        IGUID testGUID = GUIDFactory.generateGUID(ALGORITHM.SHA256, TEST_RANDOM_DATA);

        InputStream inputStream = HelperTest.StringToInputStream(TEST_RANDOM_DATA);
        Node node = new SOSNode(GUIDFactory.generateRandomGUID(ALGORITHM.SHA256),
                "badhostname", MOCK_SERVER_PORT,
                false, true, false, false, false, false, false);

        Set<Node> nodes = new HashSet<>();
        nodes.add(node);

        Storage storage = localSOSNode.getStorage();

        DataReplication replicationTask = new DataReplication(inputStream, nodes.iterator(), 1, storage, mockNodeDiscoveryService, mockDataDiscoveryService);
        TasksQueue.instance().performSyncTask(replicationTask);

        Iterator<LocationBundle> it = storage.findLocations(testGUID);
        assertFalse(it.hasNext());
    }
}
