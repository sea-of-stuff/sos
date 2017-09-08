package uk.ac.standrews.cs.sos.protocol;

import com.adobe.xmp.impl.Base64;
import org.mockserver.integration.ClientAndServer;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.castore.data.StringData;
import uk.ac.standrews.cs.guid.ALGORITHM;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.SettingsConfiguration;
import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.exceptions.node.NodeNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.impl.NodesCollectionImpl;
import uk.ac.standrews.cs.sos.impl.locations.bundles.BundleTypes;
import uk.ac.standrews.cs.sos.impl.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.impl.locations.sos.SOSURLProtocol;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.impl.node.SOSNode;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.model.NodesCollection;
import uk.ac.standrews.cs.sos.protocol.tasks.DataReplication;
import uk.ac.standrews.cs.sos.services.NodeDiscoveryService;
import uk.ac.standrews.cs.sos.services.Storage;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;
import uk.ac.standrews.cs.utilities.crypto.CryptoException;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertFalse;
import static uk.ac.standrews.cs.sos.constants.Paths.TEST_RESOURCES_PATH;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class DataReplicationTest extends ProtocolTest {

    private ClientAndServer mockServer;
    private ClientAndServer mockServerTwin;
    private static final int MOCK_SERVER_PORT = 10001;
    private static final int MOCK_TWIN_SERVER_PORT = 10002;

    private final static String BASIC_REQUEST = "" +
            "{\n" +
            "  \"data\" : \"{DATA}\"\n" +
            "}";

    private static final String TEST_DATA = "test-data";
    private static final String NODE_ID = "SHA256_16_0000a025d7d3b2cf782da0ef24423181fdd4096091bd8cc18b18c3aab9cb00a4";
    private static final String TWIN_NODE_ID = "SHA256_16_bbbba025d7d3b2cf782da0ef24423181fdd4096091bd8cc18b18c3aab9cb00a4";

    private NodeDiscoveryService mockNodeDiscoveryService;

    @BeforeMethod
    public void setUp() throws SOSProtocolException, GUIDGenerationException, ConfigurationException, CryptoException, IOException, SOSException {
        super.setUp();

        SettingsConfiguration.Settings settings = new SettingsConfiguration(new File(TEST_RESOURCES_PATH + "configurations/data_replication_test.json")).getSettingsObj();
        SOSLocalNode.settings = settings;

        new SOS_LOG(GUIDFactory.generateRandomGUID());

        IGUID testGUID = GUIDFactory.generateGUID(ALGORITHM.SHA256, TEST_DATA);

        mockServer = startClientAndServer(MOCK_SERVER_PORT);
        mockServer.dumpToLog();
        mockServer
                .when(
                        request()
                                .withMethod("POST")
                                .withPath("/sos/storage/stream")
                                .withBody(BASIC_REQUEST.replace("{DATA}", Base64.encode(TEST_DATA)))
                )
                .respond(
                        response()
                                .withStatusCode(201)
                                .withBody(
                                        "    {\n" +
                                                "        \"Type\" : \"Atom\",\n" +
                                                "        \"GUID\" : \"" + testGUID.toMultiHash() + "\",\n" +
                                                "        \"Locations\" : \n" +
                                                "        [\n" +
                                                "              {\n" +
                                                "                \"type\" : \"persistent\",\n" +
                                                "                \"location\" : \"sos://" + NODE_ID + "/" + testGUID.toMultiHash() + "\"\n" +
                                                "            } \n" +
                                                "        ]\n" +
                                                "    }\n"
                                )
                );

        mockServerTwin = startClientAndServer(MOCK_TWIN_SERVER_PORT);
        mockServerTwin.dumpToLog();
        mockServerTwin
                .when(
                        request()
                                .withMethod("POST")
                                .withPath("/sos/storage/stream")
                                .withBody(BASIC_REQUEST.replace("{DATA}", Base64.encode(TEST_DATA)))
                )
                .respond(
                        response()
                                .withStatusCode(201)
                                .withBody(
                                        "    {\n" +
                                                "        \"Type\" : \"Atom\",\n" +
                                                "        \"GUID\" : \"" + testGUID.toMultiHash() + "\",\n" +
                                                "        \"Locations\" : \n" +
                                                "        [\n" +
                                                "              {\n" +
                                                "                \"type\" : \"persistent\",\n" +
                                                "                \"location\" : \"sos://" + TWIN_NODE_ID + "/" + testGUID.toMultiHash() + "\"\n" +
                                                "            } \n" +
                                                "        ]\n" +
                                                "    }\n"
                                )
                );

        SOSURLProtocol.getInstance().register(null, null); // Local storage is not needed for this set of tests

        mockNodeDiscoveryService = mock(NodeDiscoveryService.class);
    }

    @AfterMethod
    public void tearDown() throws InterruptedException {
        mockServer.stop();
        mockServerTwin.stop();

        // Let the mockServer stop cleanly (it takes some time)
        Thread.sleep(2000);
    }

    @Test
    public void basicMockServerTest() throws IOException, InterruptedException, GUIDGenerationException, SOSProtocolException, NodeNotFoundException {
        IGUID testGUID = GUIDFactory.generateGUID(ALGORITHM.SHA256, TEST_DATA);

        Data data = new StringData(TEST_DATA);
        Node node = new SOSNode(GUIDFactory.generateRandomGUID(), mockSignatureCertificate,
                "localhost", MOCK_SERVER_PORT,
                false, true, false, false, false, false, false);
        when(mockNodeDiscoveryService.getNode(node.getNodeGUID())).thenReturn(node);

        Set<IGUID> nodes = new HashSet<>();
        nodes.add(node.getNodeGUID());
        NodesCollection nodesCollection = new NodesCollectionImpl(NodesCollection.TYPE.SPECIFIED, nodes);

        Storage storage = localSOSNode.getStorage();

        DataReplication replicationTask = new DataReplication(data, nodesCollection, 1, storage, mockNodeDiscoveryService, false);
        TasksQueue.instance().performSyncTask(replicationTask);

        Iterator<LocationBundle> it = storage.findLocations(testGUID).iterator();
        assertTrue(it.hasNext());

        LocationBundle locationBundle = it.next();
        assertEquals(locationBundle.getType(), BundleTypes.PERSISTENT);
        assertEquals(locationBundle.getLocation().toString(), "sos://" + NODE_ID + "/" + testGUID.toMultiHash());
    }

    @Test
    public void replicateToNoStorageNodeTest() throws IOException, InterruptedException, GUIDGenerationException, SOSProtocolException, NodeNotFoundException {
        IGUID testGUID = GUIDFactory.generateGUID(ALGORITHM.SHA256, TEST_DATA);

        Data data = new StringData(TEST_DATA);
        Node node = new SOSNode(GUIDFactory.generateRandomGUID(), mockSignatureCertificate,
                "localhost", MOCK_SERVER_PORT,
                false, false, false, false, false, false, false);
        when(mockNodeDiscoveryService.getNode(node.getNodeGUID())).thenReturn(node);

        Set<IGUID> nodes = new HashSet<>();
        nodes.add(node.getNodeGUID());
        NodesCollection nodesCollection = new NodesCollectionImpl(NodesCollection.TYPE.SPECIFIED, nodes);

        Storage storage = localSOSNode.getStorage();

        DataReplication replicationTask = new DataReplication(data, nodesCollection, 1, storage, mockNodeDiscoveryService, false);
        TasksQueue.instance().performSyncTask(replicationTask);

        Iterator<LocationBundle> it = storage.findLocations(testGUID).iterator();
        assertFalse(it.hasNext()); // Data has not been replicated, because we the node is not a storage one
    }

    @Test
    public void replicateOnlyOnceTest() throws IOException, InterruptedException, GUIDGenerationException, SOSProtocolException, NodeNotFoundException {
        IGUID testGUID = GUIDFactory.generateGUID(ALGORITHM.SHA256, TEST_DATA);

        Data data = new StringData(TEST_DATA);
        Node node = new SOSNode(GUIDFactory.generateRandomGUID(), mockSignatureCertificate,
                "localhost", MOCK_SERVER_PORT,
                false, false, false, false, false, false, false); // Won't replicate to non-storage
        when(mockNodeDiscoveryService.getNode(node.getNodeGUID())).thenReturn(node);

        Node storageNode = new SOSNode(GUIDFactory.generateRandomGUID(), mockSignatureCertificate,
                "localhost", MOCK_SERVER_PORT,
                false, true, false, false, false, false, false);
        when(mockNodeDiscoveryService.getNode(storageNode.getNodeGUID())).thenReturn(storageNode);

        Set<IGUID> nodes = new HashSet<>();
        nodes.add(node.getNodeGUID());
        nodes.add(storageNode.getNodeGUID());
        NodesCollection nodesCollection = new NodesCollectionImpl(NodesCollection.TYPE.SPECIFIED, nodes);

        Storage storage = localSOSNode.getStorage();

        DataReplication replicationTask = new DataReplication(data, nodesCollection, 2, storage, mockNodeDiscoveryService, false);
        TasksQueue.instance().performSyncTask(replicationTask);

        Iterator<LocationBundle> it = storage.findLocations(testGUID).iterator();
        assertTrue(it.hasNext());

        LocationBundle locationBundle = it.next();
        assertEquals(locationBundle.getType(), BundleTypes.PERSISTENT);
        assertEquals(locationBundle.getLocation().toString(), "sos://" + NODE_ID + "/" + testGUID.toMultiHash());

        assertFalse(it.hasNext());
    }

    @Test
    public void replicateOnlyOnceSecondTest() throws IOException, InterruptedException, GUIDGenerationException, SOSProtocolException, NodeNotFoundException {
        IGUID testGUID = GUIDFactory.generateGUID(ALGORITHM.SHA256, TEST_DATA);

        Data data = new StringData(TEST_DATA);
        Node node = new SOSNode(GUIDFactory.generateRandomGUID(), mockSignatureCertificate,
                "localhost", MOCK_SERVER_PORT,
                false, false, false, false, false, false, false); // Won't replicate to non-storage
        when(mockNodeDiscoveryService.getNode(node.getNodeGUID())).thenReturn(node);

        Node storageNode = new SOSNode(GUIDFactory.generateRandomGUID(), mockSignatureCertificate,
                "localhost", MOCK_SERVER_PORT,
                false, true, false, false, false, false, false);
        when(mockNodeDiscoveryService.getNode(storageNode.getNodeGUID())).thenReturn(storageNode);

        Node anotherNode = new SOSNode(GUIDFactory.generateRandomGUID(), mockSignatureCertificate,
                "localhost", MOCK_SERVER_PORT,
                true, false, true, true, true, false, false); // Won't replicate to non-storage
        when(mockNodeDiscoveryService.getNode(anotherNode.getNodeGUID())).thenReturn(anotherNode);

        Set<IGUID> nodes = new HashSet<>();
        nodes.add(node.getNodeGUID());
        nodes.add(storageNode.getNodeGUID());
        nodes.add(anotherNode.getNodeGUID());
        NodesCollection nodesCollection = new NodesCollectionImpl(NodesCollection.TYPE.SPECIFIED, nodes);

        Storage storage = localSOSNode.getStorage();

        DataReplication replicationTask = new DataReplication(data, nodesCollection, 3, storage, mockNodeDiscoveryService, false); // TODO - test with different replication factor
        TasksQueue.instance().performSyncTask(replicationTask);

        Iterator<LocationBundle> it = storage.findLocations(testGUID).iterator();
        assertTrue(it.hasNext());

        LocationBundle locationBundle = it.next();
        assertEquals(locationBundle.getType(), BundleTypes.PERSISTENT);
        assertEquals(locationBundle.getLocation().toString(), "sos://" + NODE_ID + "/" + testGUID.toMultiHash());

        assertFalse(it.hasNext());
    }

    @Test
    public void replicateToSameNodeTwiceTest() throws IOException, InterruptedException, GUIDGenerationException, SOSProtocolException, NodeNotFoundException {
        IGUID testGUID = GUIDFactory.generateGUID(ALGORITHM.SHA256, TEST_DATA);

        Data data = new StringData(TEST_DATA);

        Node storageNode = new SOSNode(GUIDFactory.generateRandomGUID(), mockSignatureCertificate,
                "localhost", MOCK_SERVER_PORT,
                false, true, false, false, false, false, false);
        when(mockNodeDiscoveryService.getNode(storageNode.getNodeGUID())).thenReturn(storageNode);

        // Will have different GUID to get around the nodes Set. However, they will both return the same HTTP response (see mock server config for MOCK_SERVER_POST)
        Node twinStorageNode = new SOSNode(GUIDFactory.generateRandomGUID(), mockSignatureCertificate,
                "localhost", MOCK_SERVER_PORT,
                false, true, false, false, false, false, false);
        when(mockNodeDiscoveryService.getNode(twinStorageNode.getNodeGUID())).thenReturn(twinStorageNode);

        Set<IGUID> nodes = new HashSet<>();
        nodes.add(storageNode.getNodeGUID());
        nodes.add(twinStorageNode.getNodeGUID());
        NodesCollection nodesCollection = new NodesCollectionImpl(NodesCollection.TYPE.SPECIFIED, nodes);

        Storage storage = localSOSNode.getStorage();

        DataReplication replicationTask = new DataReplication(data, nodesCollection, 2, storage, mockNodeDiscoveryService, false); // TODO - rep factor 1
        TasksQueue.instance().performSyncTask(replicationTask);

        Iterator<LocationBundle> it = storage.findLocations(testGUID).iterator();
        assertTrue(it.hasNext());

        LocationBundle locationBundle = it.next();
        assertEquals(locationBundle.getType(), BundleTypes.PERSISTENT);
        assertEquals(locationBundle.getLocation().toString(), "sos://" + NODE_ID + "/" + testGUID.toMultiHash());

        assertFalse(it.hasNext());
    }

    @Test // FIXME - this test fails sometimes. Index does nt seem to be update consistently
    public void replicateSameDataTwiceTest() throws IOException, InterruptedException, GUIDGenerationException, SOSProtocolException, NodeNotFoundException {
        IGUID testGUID = GUIDFactory.generateGUID(ALGORITHM.SHA256, TEST_DATA);

        Data data = new StringData(TEST_DATA);

        Node storageNode = new SOSNode(GUIDFactory.recreateGUID(NODE_ID), mockSignatureCertificate,
                "localhost", MOCK_SERVER_PORT,
                false, true, false, false, false, false, false);
        when(mockNodeDiscoveryService.getNode(storageNode.getNodeGUID())).thenReturn(storageNode);

        Node twinStorageNode = new SOSNode(GUIDFactory.recreateGUID(TWIN_NODE_ID), mockSignatureCertificate,
                "localhost", MOCK_TWIN_SERVER_PORT,
                false, true, false, false, false, false, false);
        when(mockNodeDiscoveryService.getNode(twinStorageNode.getNodeGUID())).thenReturn(twinStorageNode);

        Set<IGUID> nodes = new HashSet<>();
        nodes.add(storageNode.getNodeGUID());
        nodes.add(twinStorageNode.getNodeGUID());
        NodesCollection nodesCollection = new NodesCollectionImpl(NodesCollection.TYPE.SPECIFIED, nodes);

        Storage storage = localSOSNode.getStorage();
        DataReplication replicationTask = new DataReplication(data, nodesCollection, 2, storage, mockNodeDiscoveryService, false);
        TasksQueue.instance().performSyncTask(replicationTask);

        Iterator<LocationBundle> it = storage.findLocations(testGUID).iterator();
        assertTrue(it.hasNext());
        LocationBundle locationBundle = it.next();
        assertEquals(locationBundle.getType(), BundleTypes.PERSISTENT);

        assertTrue(it.hasNext());
        locationBundle = it.next();
        assertEquals(locationBundle.getType(), BundleTypes.PERSISTENT);

        assertFalse(it.hasNext());
    }

}