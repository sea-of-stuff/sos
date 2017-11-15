package uk.ac.standrews.cs.sos.impl.protocol;

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
import uk.ac.standrews.cs.sos.impl.datamodel.locations.sos.SOSURLProtocol;
import uk.ac.standrews.cs.sos.impl.node.NodesCollectionImpl;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.impl.protocol.tasks.ManifestReplication;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.model.NodesCollection;
import uk.ac.standrews.cs.sos.services.ManifestsDataService;
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
    private static final String TEST_VERSION_MANIFEST = "" +
            "{" +
            "  \"type\":\"Version\"," +
            "  \"Invariant\":\""+ Hashes.TEST_STRING_HASHED+"\"," +
            "  \"GUID\":\""+ GUID_VERSION+"\"," +
            "  \"Signature\":\"AAAB\"," +
            "  \"Metadata\":\""+ Hashes.TEST_STRING_HASHED+"\"," +
            "  \"Previous\":[\""+ Hashes.TEST_STRING_HASHED+"\"]," +
            "  \"ContentGUID\": \""+ Hashes.TEST_STRING_HASHED+"\"" +
            "}";

    private static final String GUID_COMPOUND = "SHA256_16_964dab35b9136a610687d31b56fd346bdda027be0a66e6761e0fd1238262cd9f";
    private static final String TEST_COMPOUND_MANIFEST = "" +
            "{" +
            "  \"type\":\"Compound\"," +
            "  \"GUID\":\"" + GUID_COMPOUND + "\"," +
            "  \"Signature\":\"AAAB\"," +
            "  \"Signer\": \"" + Hashes.TEST_STRING_HASHED+"\"," +
            "  \"Compound_Type\":\"DATA\"," +
            "  \"Content\":" +
            "  [{" +
            "    \"Label\":\"cat\"," +
            "    \"GUID\":\""+ Hashes.TEST_STRING_HASHED+"\"" +
            "  }]" +
            "}";


    protected static final String GUID_ATOM = "SHA256_16_3a6eb0790f39ac87c94f3856b2dd2c5d110e6811602261a9a923d3bb23adc8b7"; // hash of "data"
    protected static final String NODE_ID = "SHA256_16_0000a025d7d3b2cf782da0ef24423181fdd4096091bd8cc18b18c3aab9cb00a4";
    private static final String TEST_ATOM_MANIFEST = "" +
            "    {\n" +
            "        \"type\" : \"Atom\",\n" +
            "        \"GUID\" : \"" + GUID_ATOM + "\",\n" +
            "        \"Locations\" : \n" +
            "        [\n" +
            "              {\n" +
            "                \"type\" : \"persistent\",\n" +
            "                \"location\" : \"sos://" + NODE_ID + "/" + GUID_ATOM + "\"\n" +
            "            } \n" +
            "        ]\n" +
            "    }\n";


    private static final String TEST_BAD_MANIFEST = "BAD Manifest";

    @BeforeMethod
    public void setUp() throws SOSProtocolException, GUIDGenerationException, ConfigurationException {

        SettingsConfiguration.Settings settings = new SettingsConfiguration(new File(TEST_RESOURCES_PATH + "configurations/manifest_replication_test.json")).getSettingsObj();
        SOSLocalNode.settings = settings;

        new SOS_LOG(GUIDFactory.generateRandomGUID());

        mockServer = startClientAndServer(MOCK_SERVER_PORT);
        mockServer.dumpToLog();

        // POST VERSION
        mockServer
                .when(
                        request()
                                .withMethod("POST")
                                .withPath("/sos/dds/manifest")
                                .withBody(TEST_VERSION_MANIFEST)
                )
                .respond(
                        response()
                                .withStatusCode(201)
                );

        // POST COMPOUND
        mockServer
                .when(
                        request()
                                .withMethod("POST")
                                .withPath("/sos/dds/manifest")
                                .withBody(TEST_COMPOUND_MANIFEST)
                )
                .respond(
                        response()
                                .withStatusCode(201)
                );

        // POST ATOM
        mockServer
                .when(
                        request()
                                .withMethod("POST")
                                .withPath("/sos/dds/manifest")
                                .withBody(TEST_ATOM_MANIFEST)
                )
                .respond(
                        response()
                                .withStatusCode(201)
                );

        // POST BAD MANIFEST
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
    public void basicVersionManifestReplicationTest() throws InterruptedException, SOSProtocolException, NodeNotFoundException {

        Manifest mockManifest = mock(Manifest.class);
        when(mockManifest.toString()).thenReturn(TEST_VERSION_MANIFEST);
        when(mockManifest.getType()).thenReturn(ManifestType.VERSION);

        IGUID nodeGUID = GUIDFactory.generateRandomGUID();
        Node node = mock(Node.class);
        when(node.getType()).thenReturn(ManifestType.NODE);
        when(node.isValid()).thenReturn(true);
        when(node.guid()).thenReturn(nodeGUID);
        when(node.isDDS()).thenReturn(true);
        when(node.getHostname()).thenReturn("localhost");
        when(node.getHostAddress()).thenReturn(new InetSocketAddress("localhost", MOCK_SERVER_PORT));

        Set<IGUID> nodes = new HashSet<>();
        nodes.add(nodeGUID);
        NodesCollection nodesCollection = new NodesCollectionImpl(nodes);

        NodeDiscoveryService nodeDiscoveryServiceMock = mock(NodeDiscoveryService.class);
        when(nodeDiscoveryServiceMock.getNode(nodeGUID)).thenReturn(node);

        ManifestsDataService manifestsDataServiceMock = mock(ManifestsDataService.class);

        ManifestReplication replicationTask = new ManifestReplication(mockManifest, nodesCollection, 1, nodeDiscoveryServiceMock, manifestsDataServiceMock);
        TasksQueue.instance().performSyncTask(replicationTask);

        verify(node, times(1)).getHostAddress();

        verify(manifestsDataServiceMock, times(1)).addManifestNodeMapping(anyObject(), anyObject());
    }

    @Test
    public void basicCompoundManifestReplicationTest() throws InterruptedException, SOSProtocolException, NodeNotFoundException {

        Manifest mockManifest = mock(Manifest.class);
        when(mockManifest.toString()).thenReturn(TEST_COMPOUND_MANIFEST);
        when(mockManifest.getType()).thenReturn(ManifestType.COMPOUND);

        IGUID nodeGUID = GUIDFactory.generateRandomGUID();
        Node node = mock(Node.class);
        when(node.getType()).thenReturn(ManifestType.NODE);
        when(node.isValid()).thenReturn(true);
        when(node.guid()).thenReturn(nodeGUID);
        when(node.isDDS()).thenReturn(true);
        when(node.getHostname()).thenReturn("localhost");
        when(node.getHostAddress()).thenReturn(new InetSocketAddress("localhost", MOCK_SERVER_PORT));

        Set<IGUID> nodes = new HashSet<>();
        nodes.add(nodeGUID);
        NodesCollection nodesCollection = new NodesCollectionImpl(nodes);

        NodeDiscoveryService nodeDiscoveryServiceMock = mock(NodeDiscoveryService.class);
        when(nodeDiscoveryServiceMock.getNode(nodeGUID)).thenReturn(node);

        ManifestsDataService manifestsDataServiceMock = mock(ManifestsDataService.class);

        ManifestReplication replicationTask = new ManifestReplication(mockManifest, nodesCollection, 1, nodeDiscoveryServiceMock, manifestsDataServiceMock);
        TasksQueue.instance().performSyncTask(replicationTask);

        verify(node, times(1)).getHostAddress();

        verify(manifestsDataServiceMock, times(1)).addManifestNodeMapping(anyObject(), anyObject());
    }

    @Test
    public void basicAtomManifestReplicationTest() throws InterruptedException, SOSProtocolException, NodeNotFoundException {

        Manifest mockManifest = mock(Manifest.class);
        when(mockManifest.toString()).thenReturn(TEST_ATOM_MANIFEST);
        when(mockManifest.getType()).thenReturn(ManifestType.ATOM);

        IGUID nodeGUID = GUIDFactory.generateRandomGUID();
        Node node = mock(Node.class);
        when(node.getType()).thenReturn(ManifestType.NODE);
        when(node.isValid()).thenReturn(true);
        when(node.guid()).thenReturn(nodeGUID);
        when(node.isDDS()).thenReturn(true);
        when(node.getHostname()).thenReturn("localhost");
        when(node.getHostAddress()).thenReturn(new InetSocketAddress("localhost", MOCK_SERVER_PORT));

        Set<IGUID> nodes = new HashSet<>();
        nodes.add(nodeGUID);
        NodesCollection nodesCollection = new NodesCollectionImpl(nodes);

        NodeDiscoveryService nodeDiscoveryServiceMock = mock(NodeDiscoveryService.class);
        when(nodeDiscoveryServiceMock.getNode(nodeGUID)).thenReturn(node);

        ManifestsDataService manifestsDataServiceMock = mock(ManifestsDataService.class);

        ManifestReplication replicationTask = new ManifestReplication(mockManifest, nodesCollection, 1, nodeDiscoveryServiceMock, manifestsDataServiceMock);
        TasksQueue.instance().performSyncTask(replicationTask);

        verify(node, times(1)).getHostAddress();

        verify(manifestsDataServiceMock, times(1)).addManifestNodeMapping(anyObject(), anyObject());
    }

    @Test
    public void cannotReplicateManifestToNoDDSNodeReplicationTest() throws InterruptedException, SOSProtocolException, NodeNotFoundException {

        Manifest mockManifest = mock(Manifest.class);
        when(mockManifest.toString()).thenReturn(TEST_VERSION_MANIFEST);
        when(mockManifest.getType()).thenReturn(ManifestType.VERSION);

        IGUID nodeGUID = GUIDFactory.generateRandomGUID();
        Node node = mock(Node.class);
        when(node.getType()).thenReturn(ManifestType.NODE);
        when(node.isValid()).thenReturn(true);
        when(node.guid()).thenReturn(nodeGUID);
        when(node.isDDS()).thenReturn(false);

        Set<IGUID> nodes = new HashSet<>();
        nodes.add(nodeGUID);
        NodesCollection nodesCollection = new NodesCollectionImpl(nodes);

        NodeDiscoveryService nodeDiscoveryServiceMock = mock(NodeDiscoveryService.class);
        when(nodeDiscoveryServiceMock.getNode(nodeGUID)).thenReturn(node);

        ManifestsDataService manifestsDataServiceMock = mock(ManifestsDataService.class);

        ManifestReplication replicationTask = new ManifestReplication(mockManifest, nodesCollection, 1, nodeDiscoveryServiceMock, manifestsDataServiceMock);
        TasksQueue.instance().performSyncTask(replicationTask);

        verify(node, times(0)).getHostAddress();

        verify(manifestsDataServiceMock, times(0)).addManifestNodeMapping(anyObject(), anyObject());
    }

    @Test (expectedExceptions = SOSProtocolException.class)
    public void basicManifestReplicationFailsTest() throws InterruptedException, SOSProtocolException {

        Manifest mockManifest = mock(Manifest.class);
        when(mockManifest.toString()).thenReturn(TEST_VERSION_MANIFEST);
        when(mockManifest.getType()).thenReturn(ManifestType.VERSION);

        IGUID nodeGUID = GUIDFactory.generateRandomGUID();
        Node node = mock(Node.class);
        when(node.getType()).thenReturn(ManifestType.NODE);
        when(node.guid()).thenReturn(nodeGUID);
        when(node.isDDS()).thenReturn(true);
        when(node.getHostname()).thenReturn("localhost");
        when(node.getHostAddress()).thenReturn(new InetSocketAddress("localhost", MOCK_SERVER_PORT));

        Set<IGUID> nodes = new HashSet<>();
        nodes.add(nodeGUID);
        NodesCollection nodesCollection = new NodesCollectionImpl(nodes);

        ManifestReplication replicationTask = new ManifestReplication(mockManifest, nodesCollection, 1, null, null);
    }

    @Test
    public void badManifestReplicationTest() throws InterruptedException, SOSProtocolException, NodeNotFoundException {

        Manifest mockManifest = mock(Manifest.class);
        when(mockManifest.toString()).thenReturn(TEST_BAD_MANIFEST);
        when(mockManifest.getType()).thenReturn(ManifestType.ATOM); // It can actually be whatever (except context)

        IGUID nodeGUID = GUIDFactory.generateRandomGUID();
        Node node = mock(Node.class);
        when(node.getType()).thenReturn(ManifestType.NODE);
        when(node.isValid()).thenReturn(true);
        when(node.guid()).thenReturn(nodeGUID);
        when(node.isDDS()).thenReturn(true);
        when(node.getHostname()).thenReturn("localhost");
        when(node.getHostAddress()).thenReturn(new InetSocketAddress("localhost", MOCK_SERVER_PORT));

        Set<IGUID> nodes = new HashSet<>();
        nodes.add(nodeGUID);
        NodesCollection nodesCollection = new NodesCollectionImpl(nodes);

        NodeDiscoveryService nodeDiscoveryServiceMock = mock(NodeDiscoveryService.class);
        when(nodeDiscoveryServiceMock.getNode(nodeGUID)).thenReturn(node);

        ManifestsDataService manifestsDataServiceMock = mock(ManifestsDataService.class);

        ManifestReplication replicationTask = new ManifestReplication(mockManifest, nodesCollection, 1, nodeDiscoveryServiceMock, manifestsDataServiceMock);
        TasksQueue.instance().performSyncTask(replicationTask);

        verify(node, times(1)).getHostAddress();

        verify(manifestsDataServiceMock, times(0)).addManifestNodeMapping(anyObject(), anyObject());
    }
}
