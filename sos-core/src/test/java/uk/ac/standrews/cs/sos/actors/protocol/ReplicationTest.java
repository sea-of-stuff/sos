package uk.ac.standrews.cs.sos.actors.protocol;

import org.mockserver.integration.ClientAndServer;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.interfaces.manifests.LocationsIndex;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.model.locations.bundles.BundleTypes;
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

import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertFalse;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ReplicationTest {

    private ClientAndServer mockServer;
    private static final int MOCK_SERVER_PORT = 10001;

    private static final String TEST_DATA = "test-data";
    private static final String NODE_ID = "3c9bfd93ab9a6e2ed501fc583685088cca66bac2";

    @BeforeMethod
    public void setUp() throws SOSProtocolException, GUIDGenerationException {
        IGUID testGUID = GUIDFactory.generateGUID(TEST_DATA);

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
                                .withStatusCode(201)
                                .withBody(
                                        "{\n" +
                                        "  \"Type\": \"Atom\",\n" +
                                        "  \"ContentGUID\": \""+ testGUID +"\",\n" +
                                        "  \"Locations\": [\n" +
                                        "    {\n" +
                                        "      \"Type\": \"persistent\",\n" +
                                        "      \"Location\": \"sos://" + NODE_ID + "/" + testGUID + "\"\n" +
                                        "    }\n" +
                                        "  ]\n" +
                                        "}"
                                )
                );

        SOSURLProtocol.getInstance().register(null); // Local storage is not needed for this set of tests
    }

    @AfterMethod
    public void tearDown() {
        mockServer.stop();
    }

    @Test
    public void basicMockServerTest() throws IOException, InterruptedException, GUIDGenerationException {
        IGUID testGUID = GUIDFactory.generateGUID(TEST_DATA);

        InputStream inputStream = HelperTest.StringToInputStream(TEST_DATA);
        Node node = new SOSNode(GUIDFactory.generateRandomGUID(),
                "localhost", MOCK_SERVER_PORT,
                false, true, false, false, false);

        Set<Node> nodes = new HashSet<>();
        nodes.add(node);

        LocationsIndex index = new LocationsIndexImpl();
        ExecutorService executorService = Replication.ReplicateData(inputStream, nodes, index);

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        Iterator<LocationBundle> it = index.findLocations(testGUID);
        assertTrue(it.hasNext());

        LocationBundle locationBundle = it.next();
        assertEquals(locationBundle.getType(), BundleTypes.PERSISTENT);
        assertEquals(locationBundle.getLocation().toString(), "sos://" + NODE_ID + "/" + testGUID);
    }

    @Test
    public void replicateToNoStorageNodeTest() throws IOException, InterruptedException, GUIDGenerationException {
        IGUID testGUID = GUIDFactory.generateGUID(TEST_DATA);

        InputStream inputStream = HelperTest.StringToInputStream(TEST_DATA);
        Node node = new SOSNode(GUIDFactory.generateRandomGUID(),
                "localhost", MOCK_SERVER_PORT,
                false, false, false, false, false);

        Set<Node> nodes = new HashSet<>();
        nodes.add(node);

        LocationsIndex index = new LocationsIndexImpl();
        ExecutorService executorService = Replication.ReplicateData(inputStream, nodes, index);

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        Iterator<LocationBundle> it = index.findLocations(testGUID);
        assertFalse(it.hasNext()); // Data has not been replicated, because we the node is not a storage one
    }

    @Test
    public void replicateOnlyOnceTest() throws IOException, InterruptedException, GUIDGenerationException {
        IGUID testGUID = GUIDFactory.generateGUID(TEST_DATA);

        InputStream inputStream = HelperTest.StringToInputStream(TEST_DATA);
        Node node = new SOSNode(GUIDFactory.generateRandomGUID(),
                "localhost", MOCK_SERVER_PORT,
                false, false, false, false, false);


        Node storageNode = new SOSNode(GUIDFactory.generateRandomGUID(),
                "localhost", MOCK_SERVER_PORT,
                false, true, false, false, false);

        Set<Node> nodes = new HashSet<>();
        nodes.add(node);
        nodes.add(storageNode);

        LocationsIndex index = new LocationsIndexImpl();
        ExecutorService executorService = Replication.ReplicateData(inputStream, nodes, index);

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        Iterator<LocationBundle> it = index.findLocations(testGUID);
        assertTrue(it.hasNext());

        LocationBundle locationBundle = it.next();
        assertEquals(locationBundle.getType(), BundleTypes.PERSISTENT);
        assertEquals(locationBundle.getLocation().toString(), "sos://" + NODE_ID + "/" + testGUID);

        assertFalse(it.hasNext());
    }

    @Test
    public void replicateOnlyOnceSecondTest() throws IOException, InterruptedException, GUIDGenerationException {
        IGUID testGUID = GUIDFactory.generateGUID(TEST_DATA);

        InputStream inputStream = HelperTest.StringToInputStream(TEST_DATA);
        Node node = new SOSNode(GUIDFactory.generateRandomGUID(),
                "localhost", MOCK_SERVER_PORT,
                false, false, false, false, false);


        Node storageNode = new SOSNode(GUIDFactory.generateRandomGUID(),
                "localhost", MOCK_SERVER_PORT,
                false, true, false, false, false);

        Node anotherNode = new SOSNode(GUIDFactory.generateRandomGUID(),
                "localhost", MOCK_SERVER_PORT,
                true, false, true, true, true);

        Set<Node> nodes = new HashSet<>();
        nodes.add(node);
        nodes.add(storageNode);
        nodes.add(anotherNode);

        LocationsIndex index = new LocationsIndexImpl();
        ExecutorService executorService = Replication.ReplicateData(inputStream, nodes, index);

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        Iterator<LocationBundle> it = index.findLocations(testGUID);
        assertTrue(it.hasNext());

        LocationBundle locationBundle = it.next();
        assertEquals(locationBundle.getType(), BundleTypes.PERSISTENT);
        assertEquals(locationBundle.getLocation().toString(), "sos://" + NODE_ID + "/" + testGUID);

        assertFalse(it.hasNext());
    }

    @Test
    public void replicateToSameNodeTwiceTest() throws IOException, InterruptedException, GUIDGenerationException {
        IGUID testGUID = GUIDFactory.generateGUID(TEST_DATA);

        InputStream inputStream = HelperTest.StringToInputStream(TEST_DATA);

        Node storageNode = new SOSNode(GUIDFactory.generateRandomGUID(),
                "localhost", MOCK_SERVER_PORT,
                false, false, false, false, false);

        Node twinStorageNode = new SOSNode(GUIDFactory.generateRandomGUID(),
                "localhost", MOCK_SERVER_PORT,
                false, true, false, false, false);

        Set<Node> nodes = new HashSet<>();
        nodes.add(storageNode);
        nodes.add(twinStorageNode);

        LocationsIndex index = new LocationsIndexImpl();
        ExecutorService executorService = Replication.ReplicateData(inputStream, nodes, index);

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        Iterator<LocationBundle> it = index.findLocations(testGUID);
        assertTrue(it.hasNext());

        LocationBundle locationBundle = it.next();
        assertEquals(locationBundle.getType(), BundleTypes.PERSISTENT);
        assertEquals(locationBundle.getLocation().toString(), "sos://" + NODE_ID + "/" + testGUID);

        assertFalse(it.hasNext());
    }

}