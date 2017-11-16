package uk.ac.standrews.cs.sos.impl.protocol;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.node.NodeNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.sos.SOSURLProtocol;
import uk.ac.standrews.cs.sos.impl.node.NodesCollectionImpl;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.impl.protocol.tasks.ManifestReplication;
import uk.ac.standrews.cs.sos.model.*;
import uk.ac.standrews.cs.sos.services.ManifestsDataService;
import uk.ac.standrews.cs.sos.services.NodeDiscoveryService;
import uk.ac.standrews.cs.sos.utils.FileUtils;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.LinkedHashSet;
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


    private static final String GUID_ATOM = "SHA256_16_3a6eb0790f39ac87c94f3856b2dd2c5d110e6811602261a9a923d3bb23adc8b7"; // hash of "data"
    private static final String NODE_ID = "SHA256_16_0000a025d7d3b2cf782da0ef24423181fdd4096091bd8cc18b18c3aab9cb00a4";
    private static final String TEST_ATOM_MANIFEST = "" +
            "{\n" +
            "    \"type\" : \"Atom\",\n" +
            "    \"GUID\" : \"SHA256_16_3a6eb0790f39ac87c94f3856b2dd2c5d110e6811602261a9a923d3bb23adc8b7\",\n" +
            "    \"Locations\" : \n" +
            "    [\n" +
            "          {\n" +
            "            \"type\" : \"persistent\",\n" +
            "            \"location\" : \"sos://" + NODE_ID + "/" + GUID_ATOM + "\"\n" +
            "        } \n" +
            "    ]\n" +
            "}";

    private static final String TEST_FAT_CONTEXT_MANIFEST = "" +
            "{\n" +
            "  \"context\": {\n" +
            "    \"name\": \"All\",\n" +
            "    \"domain\": {\n" +
            "      \"type\": \"LOCAL\",\n" +
            "      \"nodes\": []\n" +
            "    },\n" +
            "    \"codomain\": {\n" +
            "      \"type\": \"SPECIFIED\",\n" +
            "      \"nodes\": [\"SHA256_16_1111a025d7d3b2cf782da0ef24423181fdd4096091bd8cc18b18c3aab9cb00a4\"]\n" +
            "    }\n" +
            "  },\n" +
            "  \"predicate\": {\n" +
            "    \"type\": \"Predicate\",\n" +
            "    \"predicate\": \"true;\",\n" +
            "    \"dependencies\": []\n" +
            "  },\n" +
            "  \"max_age\": 0,\n" +
            "  \"policies\": [{\n" +
            "    \"type\": \"Policy\",\n" +
            "    \"apply\": \"\",\n" +
            "    \"satisfied\": \"return true;\",\n" +
            "    \"dependencies\": []\n" +
            "  }]\n" +
            "}";

    private static final String TEST_THIN_CONTEXT_MANIFEST = "" +
            "{\n" +
            "  \"type\" : \"Context\",\n" +
            "  \"GUID\" : \"SHA256_16_e9561e61c65158f11fcf3d553ba0045a882f340ba6461480c2aab7feef4c672e\",\n" +
            "  \"name\" : \"All\",\n" +
            "  \"invariant\" : \"SHA256_16_76ad2bb7f12f9cc8ecf515931b374f3533527a206ebd909d015a5744c812e57e\",\n" +
            "  \"content\" : \"SHA256_16_95a44980ed66c2f36eb647520aaaf10cbf891ec1ce448ef0c3ce387634be118f\",\n" +
            "  \"domain\" : {\n" +
            "    \"type\" : \"LOCAL\",\n" +
            "    \"nodes\" : [ ]\n" +
            "  },\n" +
            "  \"codomain\" : {\n" +
            "    \"type\" : \"SPECIFIED\",\n" +
            "    \"nodes\" : [ \"SHA256_16_1111a025d7d3b2cf782da0ef24423181fdd4096091bd8cc18b18c3aab9cb00a4\" ]\n" +
            "  },\n" +
            "  \"predicate\" : \"SHA256_16_44ba2183cb1f84c827a103bad4635dd555d5cd585623aa98aacf8195a56b064e\",\n" +
            "  \"max_age\" : 0,\n" +
            "  \"policies\" : [ \"SHA256_16_0c094bb01ae9803b22b2c9dd4b350b3456c168eca5173002cdd01c7cd1f09905\" ]\n" +
            "}";

    private static final String TEST_PREDICATE_MANIFEST = "" +
            "{\n" +
            "  \"type\" : \"Predicate\",\n" +
            "  \"GUID\" : \"SHA256_16_44ba2183cb1f84c827a103bad4635dd555d5cd585623aa98aacf8195a56b064e\",\n" +
            "  \"dependencies\" : [ ],\n" +
            "  \"predicate\" : \"true;\"\n" +
            "}";

    private static final String TEST_POLICY_MANIFEST = "" +
            "{\n" +
            "  \"type\" : \"Policy\",\n" +
            "  \"GUID\" : \"SHA256_16_0c094bb01ae9803b22b2c9dd4b350b3456c168eca5173002cdd01c7cd1f09905\",\n" +
            "  \"dependencies\" : [ ],\n" +
            "  \"apply\" : \"\",\n" +
            "  \"satisfied\" : \"return true;\",\n" +
            "  \"fields\" : [ ]\n" +
            "}";


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

        // POST CONTEXT
        mockServer
                .when(
                        request()
                                .withMethod("POST")
                                .withPath("/sos/cms/context")
                                .withBody(TEST_FAT_CONTEXT_MANIFEST)
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
            Thread.sleep(2000);
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
    public void basicAtomManifestReplicationTest() throws InterruptedException, SOSProtocolException, NodeNotFoundException, GUIDGenerationException {

        Manifest mockManifest = mock(Manifest.class);
        when(mockManifest.toString()).thenReturn(TEST_ATOM_MANIFEST);
        when(mockManifest.getType()).thenReturn(ManifestType.ATOM);

        IGUID nodeGUID = GUIDFactory.recreateGUID(NODE_ID);
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
    public void basicFATContextManifestReplicationTest() throws InterruptedException, SOSProtocolException, NodeNotFoundException, GUIDGenerationException, ManifestNotFoundException, JsonProcessingException {

        Context mockManifest = mock(Context.class);
        when(mockManifest.toString()).thenReturn(TEST_THIN_CONTEXT_MANIFEST);
        when(mockManifest.getType()).thenReturn(ManifestType.CONTEXT);
        when(mockManifest.predicate()).thenReturn(GUIDFactory.recreateGUID("SHA256_16_44ba2183cb1f84c827a103bad4635dd555d5cd585623aa98aacf8195a56b064e"));
        IGUID policyRef = GUIDFactory.recreateGUID("SHA256_16_0c094bb01ae9803b22b2c9dd4b350b3456c168eca5173002cdd01c7cd1f09905");
        Set<IGUID> policieRefs = new LinkedHashSet<>();
        policieRefs.add(policyRef);
        when(mockManifest.policies()).thenReturn(policieRefs);
        when(mockManifest.toFATString(any(), any())).thenReturn(TEST_FAT_CONTEXT_MANIFEST);

        IGUID nodeGUID = GUIDFactory.generateRandomGUID();
        Node node = mock(Node.class);
        when(node.getType()).thenReturn(ManifestType.NODE);
        when(node.isValid()).thenReturn(true);
        when(node.guid()).thenReturn(nodeGUID);
        when(node.isDDS()).thenReturn(false);
        when(node.isCMS()).thenReturn(true);
        when(node.getHostname()).thenReturn("localhost");
        when(node.getHostAddress()).thenReturn(new InetSocketAddress("localhost", MOCK_SERVER_PORT));

        Set<IGUID> nodes = new HashSet<>();
        nodes.add(nodeGUID);
        NodesCollection nodesCollection = new NodesCollectionImpl(nodes);

        NodeDiscoveryService nodeDiscoveryServiceMock = mock(NodeDiscoveryService.class);
        when(nodeDiscoveryServiceMock.getNode(nodeGUID)).thenReturn(node);

        ManifestsDataService manifestsDataServiceMock = mock(ManifestsDataService.class);
        when(manifestsDataServiceMock.getManifest(
                GUIDFactory.recreateGUID("SHA256_16_44ba2183cb1f84c827a103bad4635dd555d5cd585623aa98aacf8195a56b064e")))
                .thenReturn(FileUtils.ManifestFromJson(TEST_PREDICATE_MANIFEST));
        when(manifestsDataServiceMock.getManifest(
                GUIDFactory.recreateGUID("SHA256_16_0c094bb01ae9803b22b2c9dd4b350b3456c168eca5173002cdd01c7cd1f09905")))
                .thenReturn(FileUtils.ManifestFromJson(TEST_POLICY_MANIFEST));

        ManifestReplication replicationTask = new ManifestReplication(mockManifest, nodesCollection, 1, nodeDiscoveryServiceMock, manifestsDataServiceMock);
        TasksQueue.instance().performSyncTask(replicationTask);

        verify(node, times(1)).getHostAddress();
        verify(manifestsDataServiceMock, times(1)).addManifestNodeMapping(anyObject(), anyObject());
    }

    // Cannot replicate VERSION manifest to noDDS node
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
