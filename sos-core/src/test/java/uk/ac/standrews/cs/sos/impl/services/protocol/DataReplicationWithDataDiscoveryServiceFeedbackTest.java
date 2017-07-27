package uk.ac.standrews.cs.sos.impl.services.protocol;

import org.mockserver.integration.ClientAndServer;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.SetUpTest;
import uk.ac.standrews.cs.sos.constants.SOSConstants;
import uk.ac.standrews.cs.sos.exceptions.node.NodeRegistrationException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.impl.locations.bundles.BundleTypes;
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
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class DataReplicationWithDataDiscoveryServiceFeedbackTest extends SetUpTest {

    private ClientAndServer mockServer;
    private static final int MOCK_SERVER_PORT = 10003;

    private static final String TEST_DATA = "test-data";
    private static final String NODE_ID = "3c9bfd93ab9a6e2ed501fc583685088cca66bac2";

    private NodeDiscoveryService mockNodeDiscoveryService;
    private DataDiscoveryService mockDataDiscoveryService;

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
                                        "    \"" + SOSConstants.MANIFEST + "\" : \n" +
                                        "    {\n" +
                                        "        \"Type\" : \"Atom\",\n" +
                                        "        \"ContentGUID\" : \"" + testGUID + "\",\n" +
                                        "        \"Locations\" : \n" +
                                        "        [\n" +
                                        "              {\n" +
                                        "                \"Type\" : \"persistent\",\n" +
                                        "                \"Location\" : \"sos://" + NODE_ID + "/" + testGUID + "\"\n" +
                                        "            } \n" +
                                        "        ]\n" +
                                        "    },\n" +
                                        "    \"" + SOSConstants.DDD_INFO + "\" : \n" +
                                        "        [\n" +
                                        "                {\n" +
                                        "                        \"" + SOSConstants.GUID + "\" : \"aebbfd93ab9a6e2ed501fc583685088cca66bac2\",\n" +
                                        "                        \"" + SOSConstants.HOSTNAME + "\" : \"http://example1.org\",\n" +
                                        "                        \"" + SOSConstants.PORT + "\" : 12345\n" +
                                        "                },\n" +
                                        "                {\n" +
                                        "                        \"" + SOSConstants.GUID + "\" : \"5039a3ee5e6b4b0d3255bfef95601890afd80709\",\n" +
                                        "                        \"" + SOSConstants.HOSTNAME + "\" : \"http://example2.org\",\n" +
                                        "                        \"" + SOSConstants.PORT + "\" : 12346\n" +
                                        "                },\n" +
                                        "                {        \n" +
                                        "                        \"" + SOSConstants.GUID + "\" : \"002bfd93ab9a6e2ed501fc583685088cca66bac2\",\n" +
                                        "                        \"" + SOSConstants.HOSTNAME + "\" : \"http://example3.org\",\n" +
                                        "                        \"" + SOSConstants.PORT + "\" : 12347\n" +
                                        "                }\n" +
                                        "        ]\n" +
                                        "}")
                );

        SOSURLProtocol.getInstance().register(null, null); // Local storage is not needed for this set of tests
        new SOS_LOG(GUIDFactory.generateRandomGUID());

        mockNodeDiscoveryService = mock(NodeDiscoveryService.class);
        mockDataDiscoveryService = mock(DataDiscoveryService.class);
    }

    @AfterMethod
    public void tearDown() {
        mockServer.stop();
    }

    @Test
    public void registeringNDSFeedbackTest() throws GUIDGenerationException, UnsupportedEncodingException, InterruptedException, SOSProtocolException, NodeRegistrationException {
        IGUID testGUID = GUIDFactory.generateGUID(TEST_DATA);

        InputStream inputStream = HelperTest.StringToInputStream(TEST_DATA);
        Node node = new SOSNode(GUIDFactory.generateRandomGUID(),
                "localhost", MOCK_SERVER_PORT,
                false, true, false, false, false, false, false);

        Set<Node> nodes = new HashSet<>();
        nodes.add(node);

        Storage storage = localSOSNode.getStorage();

        DataReplication replicationTask = new DataReplication(inputStream, nodes.iterator(), 1, storage, mockNodeDiscoveryService, mockDataDiscoveryService);
        TasksQueue.instance().performSyncTask(replicationTask);

        Iterator<LocationBundle> it = storage.findLocations(testGUID);
        assertTrue(it.hasNext());

        LocationBundle locationBundle = it.next();
        assertEquals(locationBundle.getType(), BundleTypes.PERSISTENT);
        assertEquals(locationBundle.getLocation().toString(), "sos://" + NODE_ID + "/" + testGUID);

        verify(mockNodeDiscoveryService, times(3)).registerNode(anyObject(), anyBoolean());
        verify(mockDataDiscoveryService, times(3)).addManifestDDSMapping(anyObject(), anyObject());
    }

    @Test (expectedExceptions = SOSProtocolException.class)
    public void failWithNoIndexNDSNotCalledTest() throws IOException, InterruptedException, GUIDGenerationException, SOSProtocolException {

        InputStream inputStream = HelperTest.StringToInputStream(TEST_DATA);
        Node node = new SOSNode(GUIDFactory.generateRandomGUID(),
                "localhost", MOCK_SERVER_PORT,
                false, true, false, false, false, false, false);

        Set<Node> nodes = new HashSet<>();
        nodes.add(node);

        DataReplication replicationTask = new DataReplication(inputStream, nodes.iterator(), 1, null, mockNodeDiscoveryService, mockDataDiscoveryService);
        TasksQueue.instance().performSyncTask(replicationTask);
    }

    @Test (expectedExceptions = SOSProtocolException.class)
    public void failWithNullNDSTest() throws IOException, InterruptedException, GUIDGenerationException, SOSProtocolException {

        InputStream inputStream = HelperTest.StringToInputStream(TEST_DATA);
        Node node = new SOSNode(GUIDFactory.generateRandomGUID(),
                "localhost", MOCK_SERVER_PORT,
                false, true, false, false, false, false, false);

        Set<Node> nodes = new HashSet<>();
        nodes.add(node);

        Storage storage = localSOSNode.getStorage();

        DataReplication replicationTask = new DataReplication(inputStream, nodes.iterator(), 1, storage, null, mockDataDiscoveryService);
        TasksQueue.instance().performSyncTask(replicationTask);
    }

    @Test (expectedExceptions = SOSProtocolException.class)
    public void failWithNullDDSTest() throws IOException, InterruptedException, GUIDGenerationException, SOSProtocolException {

        InputStream inputStream = HelperTest.StringToInputStream(TEST_DATA);
        Node node = new SOSNode(GUIDFactory.generateRandomGUID(),
                "localhost", MOCK_SERVER_PORT,
                false, true, false, false, false, false, false);

        Set<Node> nodes = new HashSet<>();
        nodes.add(node);

        Storage storage = localSOSNode.getStorage();

        DataReplication replicationTask = new DataReplication(inputStream, nodes.iterator(), 1, storage, mockNodeDiscoveryService, null);
        TasksQueue.instance().performSyncTask(replicationTask);
    }
}
