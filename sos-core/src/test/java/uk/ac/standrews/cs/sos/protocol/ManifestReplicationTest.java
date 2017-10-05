package uk.ac.standrews.cs.sos.protocol;

import org.mockserver.integration.ClientAndServer;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.SettingsConfiguration;
import uk.ac.standrews.cs.sos.constants.Hashes;
import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.exceptions.node.NodeNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.impl.NodesCollectionImpl;
import uk.ac.standrews.cs.sos.impl.locations.sos.SOSURLProtocol;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.model.NodesCollection;
import uk.ac.standrews.cs.sos.protocol.tasks.ManifestReplication;
import uk.ac.standrews.cs.sos.services.DataDiscoveryService;
import uk.ac.standrews.cs.sos.services.NodeDiscoveryService;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static uk.ac.standrews.cs.sos.constants.Paths.TEST_RESOURCES_PATH;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ManifestReplicationTest {

    private ClientAndServer mockServer;
    private static final int MOCK_SERVER_PORT = 10002;

    private static final String GUID_VERSION = "SHA256_16_aaaaa025d7d3b2cf782da0ef24423181fdd4096091bd8cc18b18c3aab9cb00a4";
    private static final String TEST_MANIFEST = "" +
            "{" +
            "  \"type\":\"Version\"," +
            "  \"Invariant\":\""+ Hashes.TEST_STRING_HASHED+"\"," +
            "  \"GUID\":\""+ GUID_VERSION+"\"," +
            "  \"Signature\":\"AAAB\"," +
            "  \"Metadata\":\""+ Hashes.TEST_STRING_HASHED+"\"," +
            "  \"Previous\":[\""+ Hashes.TEST_STRING_HASHED+"\"]," +
            "  \"ContentGUID\": \""+ Hashes.TEST_STRING_HASHED+"\"" +
            "}";

    private static final String TEST_BAD_MANIFEST = "BAD Manifest";

    @BeforeMethod
    public void setUp() throws SOSProtocolException, GUIDGenerationException, ConfigurationException {

        SettingsConfiguration.Settings settings = new SettingsConfiguration(new File(TEST_RESOURCES_PATH + "configurations/manifest_replication_test.json")).getSettingsObj();
        SOSLocalNode.settings = settings;

        new SOS_LOG(GUIDFactory.generateRandomGUID());

        mockServer = startClientAndServer(MOCK_SERVER_PORT);
        mockServer.dumpToLog();
        mockServer
                .when(
                        request()
                                .withMethod("POST")
                                .withPath("/sos/dds/manifest")
                                .withBody(TEST_MANIFEST)
                )
                .respond(
                        response()
                                .withStatusCode(201)
                );

        mockServer
                .when(
                        request()
                                .withMethod("POST")
                                .withPath("/sos/dds/manifest")
                                .withBody(TEST_BAD_MANIFEST)
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

        // Let the mock server stop properly
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void basicManifestReplicationTest() throws InterruptedException, SOSProtocolException, NodeNotFoundException {

        Manifest mockManifest = mock(Manifest.class);
        when(mockManifest.toString()).thenReturn(TEST_MANIFEST);

        IGUID nodeGUID = GUIDFactory.generateRandomGUID();
        Node node = mock(Node.class);
        when(node.getNodeGUID()).thenReturn(nodeGUID);
        when(node.isDDS()).thenReturn(true);
        when(node.getHostname()).thenReturn("localhost");
        when(node.getHostAddress()).thenReturn(new InetSocketAddress("localhost", MOCK_SERVER_PORT));

        Set<IGUID> nodes = new HashSet<>();
        nodes.add(nodeGUID);
        NodesCollection nodesCollection = new NodesCollectionImpl(NodesCollection.TYPE.SPECIFIED, nodes);

        NodeDiscoveryService nodeDiscoveryServiceMock = mock(NodeDiscoveryService.class);
        when(nodeDiscoveryServiceMock.getNode(nodeGUID)).thenReturn(node);

        DataDiscoveryService dataDiscoveryServiceMock = mock(DataDiscoveryService.class);

        ManifestReplication replicationTask = new ManifestReplication(mockManifest, nodesCollection, 1, nodeDiscoveryServiceMock, dataDiscoveryServiceMock);
        TasksQueue.instance().performSyncTask(replicationTask);

        verify(node, times(1)).isDDS();
        verify(node, times(1)).getHostAddress();

        verify(dataDiscoveryServiceMock, times(1)).addManifestDDSMapping(anyObject(), anyObject());
    }

    @Test
    public void cannotReplicateManifestToNoDDSNodeReplicationTest() throws InterruptedException, SOSProtocolException, NodeNotFoundException {

        Manifest mockManifest = mock(Manifest.class);
        when(mockManifest.toString()).thenReturn(TEST_MANIFEST);

        IGUID nodeGUID = GUIDFactory.generateRandomGUID();
        Node node = mock(Node.class);
        when(node.getNodeGUID()).thenReturn(nodeGUID);
        when(node.isDDS()).thenReturn(false);

        Set<IGUID> nodes = new HashSet<>();
        nodes.add(nodeGUID);
        NodesCollection nodesCollection = new NodesCollectionImpl(NodesCollection.TYPE.SPECIFIED, nodes);

        NodeDiscoveryService nodeDiscoveryServiceMock = mock(NodeDiscoveryService.class);
        when(nodeDiscoveryServiceMock.getNode(nodeGUID)).thenReturn(node);

        DataDiscoveryService dataDiscoveryServiceMock = mock(DataDiscoveryService.class);

        ManifestReplication replicationTask = new ManifestReplication(mockManifest, nodesCollection, 1, nodeDiscoveryServiceMock, dataDiscoveryServiceMock);
        TasksQueue.instance().performSyncTask(replicationTask);

        verify(node, times(1)).isDDS();
        verify(node, times(0)).getHostAddress();

        verify(dataDiscoveryServiceMock, times(0)).addManifestDDSMapping(anyObject(), anyObject());
    }

    @Test (expectedExceptions = SOSProtocolException.class)
    public void basicManifestReplicationFailsTest() throws InterruptedException, SOSProtocolException {

        Manifest mockManifest = mock(Manifest.class);
        when(mockManifest.toString()).thenReturn(TEST_MANIFEST);

        IGUID nodeGUID = GUIDFactory.generateRandomGUID();
        Node node = mock(Node.class);
        when(node.getNodeGUID()).thenReturn(nodeGUID);
        when(node.isDDS()).thenReturn(true);
        when(node.getHostname()).thenReturn("localhost");
        when(node.getHostAddress()).thenReturn(new InetSocketAddress("localhost", MOCK_SERVER_PORT));

        Set<IGUID> nodes = new HashSet<>();
        nodes.add(nodeGUID);
        NodesCollection nodesCollection = new NodesCollectionImpl(NodesCollection.TYPE.SPECIFIED, nodes);

        ManifestReplication replicationTask = new ManifestReplication(mockManifest, nodesCollection, 1, null, null);
    }

    @Test
    public void badManifestReplicationTest() throws InterruptedException, SOSProtocolException, NodeNotFoundException {

        Manifest mockManifest = mock(Manifest.class);
        when(mockManifest.toString()).thenReturn(TEST_BAD_MANIFEST);

        IGUID nodeGUID = GUIDFactory.generateRandomGUID();
        Node node = mock(Node.class);
        when(node.getNodeGUID()).thenReturn(nodeGUID);
        when(node.isDDS()).thenReturn(true);
        when(node.getHostname()).thenReturn("localhost");
        when(node.getHostAddress()).thenReturn(new InetSocketAddress("localhost", MOCK_SERVER_PORT));

        Set<IGUID> nodes = new HashSet<>();
        nodes.add(nodeGUID);
        NodesCollection nodesCollection = new NodesCollectionImpl(NodesCollection.TYPE.SPECIFIED, nodes);

        NodeDiscoveryService nodeDiscoveryServiceMock = mock(NodeDiscoveryService.class);
        when(nodeDiscoveryServiceMock.getNode(nodeGUID)).thenReturn(node);

        DataDiscoveryService dataDiscoveryServiceMock = mock(DataDiscoveryService.class);

        ManifestReplication replicationTask = new ManifestReplication(mockManifest, nodesCollection, 1, nodeDiscoveryServiceMock, dataDiscoveryServiceMock);
        TasksQueue.instance().performSyncTask(replicationTask);

        verify(node, times(1)).isDDS();
        verify(node, times(1)).getHostAddress();

        verify(dataDiscoveryServiceMock, times(0)).addManifestDDSMapping(anyObject(), anyObject());
    }
}
