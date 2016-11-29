package uk.ac.standrews.cs.sos.actors.protocol;

import org.mockserver.integration.ClientAndServer;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.interfaces.actors.NDS;
import uk.ac.standrews.cs.sos.interfaces.manifests.LocationsIndex;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.model.locations.sos.SOSURLProtocol;
import uk.ac.standrews.cs.sos.model.manifests.atom.LocationsIndexImpl;
import uk.ac.standrews.cs.sos.node.SOSNode;
import uk.ac.standrews.cs.sos.utils.HelperTest;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.mock;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.testng.AssertJUnit.assertFalse;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class DataReplicationServerFailureTest {

    private ClientAndServer mockServer;
    private static final int MOCK_SERVER_PORT = 10004;

    private static final String TEST_DATA = "test-data";
    private static final String TEST_MORE_DATA = "test-more-data";
    private static final String TEST_EMPTY_DATA = " ";
    private static final String TEST_NO_RESPONSE_DATA = "test-no-response-data";
    private static final String TEST_RANDOM_DATA = "test-random-data";

    private NDS mockNDS;

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

        SOSURLProtocol.getInstance().register(null); // Local storage is not needed for this set of tests
        mockNDS = mock(NDS.class);
    }

    @AfterMethod
    public void tearDown() {
        mockServer.stop();
    }

    @Test
    public void replicationFailsTest() throws IOException, InterruptedException, GUIDGenerationException, SOSProtocolException {
        IGUID testGUID = GUIDFactory.generateGUID(TEST_DATA);

        InputStream inputStream = HelperTest.StringToInputStream(TEST_DATA);
        Node node = new SOSNode(GUIDFactory.generateRandomGUID(),
                "localhost", MOCK_SERVER_PORT,
                false, true, false, false, false);

        Set<Node> nodes = new HashSet<>();
        nodes.add(node);

        LocationsIndex index = new LocationsIndexImpl();
        ExecutorService executorService = DataReplication.Replicate(inputStream, nodes, index, mockNDS);

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        Iterator<LocationBundle> it = index.findLocations(testGUID);
        assertFalse(it.hasNext());
    }

    @Test
    public void replicationFails400ErrorTest() throws IOException, InterruptedException, GUIDGenerationException, SOSProtocolException {
        IGUID testGUID = GUIDFactory.generateGUID(TEST_MORE_DATA);

        InputStream inputStream = HelperTest.StringToInputStream(TEST_MORE_DATA);
        Node node = new SOSNode(GUIDFactory.generateRandomGUID(),
                "localhost", MOCK_SERVER_PORT,
                false, true, false, false, false);

        Set<Node> nodes = new HashSet<>();
        nodes.add(node);

        LocationsIndex index = new LocationsIndexImpl();
        ExecutorService executorService = DataReplication.Replicate(inputStream, nodes, index, mockNDS);

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        Iterator<LocationBundle> it = index.findLocations(testGUID);
        assertFalse(it.hasNext());
    }

    @Test
    public void replicationNoDataFailsErrorTest() throws IOException, InterruptedException, GUIDGenerationException, SOSProtocolException {
        IGUID testGUID = GUIDFactory.generateGUID(TEST_EMPTY_DATA);

        InputStream inputStream = HelperTest.StringToInputStream(TEST_EMPTY_DATA);
        Node node = new SOSNode(GUIDFactory.generateRandomGUID(),
                "localhost", MOCK_SERVER_PORT,
                false, true, false, false, false);

        Set<Node> nodes = new HashSet<>();
        nodes.add(node);

        LocationsIndex index = new LocationsIndexImpl();
        ExecutorService executorService = DataReplication.Replicate(inputStream, nodes, index, mockNDS);

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        Iterator<LocationBundle> it = index.findLocations(testGUID);
        assertFalse(it.hasNext());
    }

    @Test
    public void replicationNoResponseFailsErrorTest() throws IOException, InterruptedException, GUIDGenerationException, SOSProtocolException {
        IGUID testGUID = GUIDFactory.generateGUID(TEST_NO_RESPONSE_DATA);

        InputStream inputStream = HelperTest.StringToInputStream(TEST_NO_RESPONSE_DATA);
        Node node = new SOSNode(GUIDFactory.generateRandomGUID(),
                "localhost", MOCK_SERVER_PORT,
                false, true, false, false, false);

        Set<Node> nodes = new HashSet<>();
        nodes.add(node);

        LocationsIndex index = new LocationsIndexImpl();
        ExecutorService executorService = DataReplication.Replicate(inputStream, nodes, index, mockNDS);

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        Iterator<LocationBundle> it = index.findLocations(testGUID);
        assertFalse(it.hasNext());
    }

    @Test
    public void badHostnameTest() throws IOException, InterruptedException, GUIDGenerationException, SOSProtocolException {
        IGUID testGUID = GUIDFactory.generateGUID(TEST_RANDOM_DATA);

        InputStream inputStream = HelperTest.StringToInputStream(TEST_RANDOM_DATA);
        Node node = new SOSNode(GUIDFactory.generateRandomGUID(),
                "badhostname", MOCK_SERVER_PORT,
                false, true, false, false, false);

        Set<Node> nodes = new HashSet<>();
        nodes.add(node);

        LocationsIndex index = new LocationsIndexImpl();
        ExecutorService executorService = DataReplication.Replicate(inputStream, nodes, index, mockNDS);

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        Iterator<LocationBundle> it = index.findLocations(testGUID);
        assertFalse(it.hasNext());
    }
}
